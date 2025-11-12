package de.smallinger.waasl.event;

import com.mojang.logging.LogUtils;
import de.smallinger.waasl.Config;
import de.smallinger.waasl.WolfArmorandStorageLegacy;
import de.smallinger.waasl.item.ModItems;
import de.smallinger.waasl.item.WolfArmorItem;
import de.smallinger.waasl.item.WolfChestItem;
import de.smallinger.waasl.menu.ModMenuTypes;
import de.smallinger.waasl.menu.WolfInventoryMenu;
import de.smallinger.waasl.util.WolfHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Event Handler für Wolf Armor Gameplay Features
 * - Player Interact: Armor/Chest equipping
 * - Living Damage: Armor protection calculation
 * - Entity Join Level: Sync armor to body slot for rendering
 */
@EventBusSubscriber(modid = WolfArmorandStorageLegacy.MODID)
public class ModEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Handles player right-clicking on entities (Wolf armor/chest equipping)
     */
    @SubscribeEvent
    public static void onPlayerInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
        // Only handle TamableAnimal (wolves in 1.21.10)
        if (!(event.getTarget() instanceof TamableAnimal wolf)) {
            return;
        }

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack itemStack = player.getItemInHand(hand);

        // Only owner can interact with tamed wolf
        if (!wolf.isTame() || !wolf.isOwnedBy(player)) {
            return;
        }

        // Don't interact with baby wolves
        if (wolf.isBaby()) {
            return;
        }

        // Case 1: Shift + Right-Click -> ALWAYS open GUI
        if (player.isCrouching()) {
            if (!player.level().isClientSide()) {
                openWolfInventory(player, wolf);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        // Case 2: Holding Wolf Armor -> Equip armor
        if (itemStack.getItem() instanceof WolfArmorItem armorItem) {
            ItemStack currentArmor = WolfHelper.getArmorStack(wolf);
            
            if (currentArmor.isEmpty()) {
                // No armor equipped -> equip new armor
                if (!player.level().isClientSide()) {
                    ItemStack toEquip = itemStack.copy();
                    toEquip.setCount(1);
                    WolfHelper.setArmorStack(wolf, toEquip);
                    
                    // Play equip sound
                    wolf.playSound(SoundEvents.ARMOR_EQUIP_WOLF.value(), 1.0F, 1.0F);
                    
                    // Consume item from player hand
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
            // Armor already equipped -> do nothing (let vanilla handle it)
        }

        // Case 3: Holding Wolf Chest -> Equip chest
        if (itemStack.getItem() instanceof WolfChestItem) {
            if (!WolfHelper.hasChest(wolf)) {
                if (!player.level().isClientSide()) {
                    WolfHelper.setHasChest(wolf, true);
                    
                    // Play equip sound
                    wolf.playSound(SoundEvents.ARMOR_EQUIP_WOLF.value(), 1.0F, 1.0F);
                    
                    // Consume item from player hand
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }
    }

    /**
     * Opens the wolf inventory GUI for the player
     */
    private static void openWolfInventory(@NotNull Player player, @NotNull TamableAnimal wolf) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                (containerId, playerInventory, p) -> {
                    // Create inventory container for chest storage (14 slots: 7x2)
                    SimpleContainer inventory = new SimpleContainer(14);
                    var items = WolfHelper.getInventory(wolf);
                    
                    // Copy items to container
                    for (int i = 0; i < 14; i++) {
                        inventory.setItem(i, items.get(i));
                    }
                    
                    // Create armor container (2 slots: armor + chest upgrade)
                    SimpleContainer armorContainer = new SimpleContainer(2);
                    armorContainer.setItem(0, WolfHelper.getArmorStack(wolf)); // Armor slot
                    armorContainer.setItem(1, WolfHelper.hasChest(wolf) ? new ItemStack(ModItems.WOLF_CHEST.get()) : ItemStack.EMPTY); // Chest indicator
                    
                    return new WolfInventoryMenu(containerId, playerInventory, wolf, inventory, armorContainer);
                },
                wolf.getDisplayName()
            ));
        }
    }

    /**
     * Handles damage events for wolves with armor (damage absorption like Vanilla Wolf Armor)
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        // Only handle TamableAnimal (wolves)
        if (!(event.getEntity() instanceof TamableAnimal wolf)) {
            return;
        }

        // Check if wolf has armor equipped
        ItemStack armorStack = WolfHelper.getArmorStack(wolf);
        if (armorStack.isEmpty() || !(armorStack.getItem() instanceof WolfArmorItem armorItem)) {
            return;
        }

        // Get damage amount
        float originalDamage = event.getOriginalDamage();
        if (originalDamage <= 0) {
            return;
        }

        // Check if this damage type should bypass armor (like vanilla wolf armor)
        // Wolf armor does NOT absorb: drowning, freezing, suffocating, magic, thorns, wither, void, etc.
        var damageSource = event.getSource();
        
        // Check if damage bypasses armor using the damage type tag
        if (damageSource.is(DamageTypeTags.BYPASSES_WOLF_ARMOR)) {
            return; // Damage bypasses armor
        }

        // Wolf armor ABSORBS ALL damage (not reduces it)
        // According to Minecraft Wiki: "Wolf armor absorbs all damage done to the wolf"
        event.setNewDamage(0.0F);

        // Damage the armor item by the amount of damage absorbed
        // "Wolf armor loses one point of durability for each point of damage absorbed"
        // "If the damage of an attack exceeds the remaining durability, all damage is absorbed anyway"
        if (!wolf.level().isClientSide()) {
            int damageToArmor = Math.max(1, Math.round(originalDamage));
            int maxDurability = armorStack.getMaxDamage();
            int currentDamage = armorStack.getDamageValue();
            int remainingDurability = maxDurability - currentDamage;
            
            // Check for cracking thresholds (60, 44, 20 durability left)
            boolean shouldCrack = false;
            if (remainingDurability > 60 && remainingDurability - damageToArmor <= 60) {
                shouldCrack = true;
            } else if (remainingDurability > 44 && remainingDurability - damageToArmor <= 44) {
                shouldCrack = true;
            } else if (remainingDurability > 20 && remainingDurability - damageToArmor <= 20) {
                shouldCrack = true;
            }
            
            if (shouldCrack) {
                // Play cracking sound
                wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), 
                    SoundEvents.WOLF_ARMOR_CRACK, wolf.getSoundSource(), 1.0F, 1.0F);
            }
            
            // Play damage sound
            wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), 
                SoundEvents.WOLF_ARMOR_DAMAGE, wolf.getSoundSource(), 1.0F, 
                0.8F + wolf.getRandom().nextFloat() * 0.4F);
            
            armorStack.hurtAndBreak(
                damageToArmor,
                wolf,
                wolf.getEquipmentSlotForItem(armorStack)
            );
            
            // Update armor in attachment and body slot
            if (armorStack.isEmpty()) {
                WolfHelper.setArmorStack(wolf, ItemStack.EMPTY);
                // Play break sound
                wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), 
                    SoundEvents.WOLF_ARMOR_BREAK, wolf.getSoundSource(), 1.0F, 1.0F);
            } else {
                // Sync updated durability to attachment and body slot
                WolfHelper.setArmorStack(wolf, armorStack);
            }
        }
    }

    /**
     * Handles wolf death - drops chest inventory, chest item, and armor
     */
    @SubscribeEvent
    public static void onWolfDeath(LivingDamageEvent.Post event) {
        // Only handle TamableAnimal (wolves)
        if (!(event.getEntity() instanceof TamableAnimal wolf)) {
            return;
        }
        
        // Only on server side and if wolf actually dies
        if (wolf.level().isClientSide() || !wolf.isDeadOrDying()) {
            return;
        }
        
        var serverLevel = (net.minecraft.server.level.ServerLevel) wolf.level();
        float dropHeight = 0.5f; // Leicht über der Wolf-Position (halber Block)
        
        // Drop armor if equipped
        ItemStack armorStack = WolfHelper.getArmorStack(wolf);
        if (!armorStack.isEmpty()) {
            wolf.spawnAtLocation(serverLevel, armorStack.copy(), dropHeight);
            WolfHelper.setArmorStack(wolf, ItemStack.EMPTY);
        }
        
        // Drop chest and inventory if has chest
        if (WolfHelper.hasChest(wolf)) {
            // ERST: Drop all items from chest inventory
            var items = WolfHelper.getInventory(wolf);
            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = items.get(i);
                if (!stack.isEmpty()) {
                    wolf.spawnAtLocation(serverLevel, stack.copy(), dropHeight);
                }
            }
            
            // Clear inventory with sync
            WolfHelper.clearInventory(wolf);
            
            // DANN: Drop the chest item itself (nachdem Inventar geleert wurde)
            wolf.spawnAtLocation(serverLevel, new ItemStack(ModItems.WOLF_CHEST.get()), dropHeight);
            
            // Update status
            WolfHelper.setHasChest(wolf, false);
        }
    }
    
    /**
     * Synchronisiert die Rüstung aus dem Attachment in den Body-Armor-Slot beim Laden
     * und registriert Custom AI Goals für Wölfe
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        // Nur für Wolf
        if (!(event.getEntity() instanceof net.minecraft.world.entity.animal.wolf.Wolf wolf)) {
            return;
        }
        
        // Nur auf Server-Seite
        if (wolf.level().isClientSide()) {
            return;
        }
        
        // Synchronisiere die Rüstung aus dem Attachment in den Body-Armor-Slot
        ItemStack armorFromAttachment = WolfHelper.getArmorStack(wolf);
        if (!armorFromAttachment.isEmpty()) {
            wolf.setBodyArmorItem(armorFromAttachment.copy());
        }
        
        // Registriere Auto-Heal AI Goal (nur einmal)
        // Priority 4 = nach wichtigen Goals wie Follow Owner, Sit, etc.
        if (!hasAutoHealGoal(wolf)) {
            wolf.goalSelector.addGoal(4, new de.smallinger.waasl.entity.ai.WolfAutoHealGoal(wolf));
        }
        
        // Registriere Howl at Moon AI Goal (nur für wilde Wölfe, niedrige Priority)
        // Priority 9 = sehr niedrig, läuft nur wenn nichts wichtigeres zu tun ist
        if (!wolf.isTame() && !hasHowlGoal(wolf)) {
            wolf.goalSelector.addGoal(9, new de.smallinger.waasl.entity.ai.WolfHowlAtMoonGoal(wolf));
        }
    }
    
    /**
     * Prüft ob der Wolf bereits die Auto-Heal AI hat
     */
    private static boolean hasAutoHealGoal(net.minecraft.world.entity.animal.wolf.Wolf wolf) {
        return wolf.goalSelector.getAvailableGoals().stream()
                .anyMatch(goal -> goal.getGoal() instanceof de.smallinger.waasl.entity.ai.WolfAutoHealGoal);
    }
    
    /**
     * Prüft ob der Wolf bereits die Howl-at-Moon AI hat
     */
    private static boolean hasHowlGoal(net.minecraft.world.entity.animal.wolf.Wolf wolf) {
        return wolf.goalSelector.getAvailableGoals().stream()
                .anyMatch(goal -> goal.getGoal() instanceof de.smallinger.waasl.entity.ai.WolfHowlAtMoonGoal);
    }
}
