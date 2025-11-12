package de.smallinger.waasl.client.event;

import com.google.common.reflect.TypeToken;
import de.smallinger.waasl.WolfArmorandStorageLegacy;
import de.smallinger.waasl.item.WolfArmorItem;
import de.smallinger.waasl.util.WolfHelper;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

/**
 * Event Handler für Client-seitige Render State Modifications
 * Verwendet NeoForge's eingebautes System statt Mixins
 */
public class RenderStateModifierHandler {
    
    /**
     * Context Keys für Wolf-Informationen im RenderState
     */
    public static final ContextKey<Boolean> HAS_CHEST_CONTEXT = new ContextKey<>(
        ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "has_chest")
    );
    
    public static final ContextKey<Float> HEALTH_CONTEXT = new ContextKey<>(
        ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "health")
    );
    
    public static final ContextKey<Float> MAX_HEALTH_CONTEXT = new ContextKey<>(
        ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "max_health")
    );
    
    public static final ContextKey<Integer> ARMOR_VALUE_CONTEXT = new ContextKey<>(
        ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "armor_value")
    );
    
    public static final ContextKey<Float> ARMOR_DURABILITY_PERCENT_CONTEXT = new ContextKey<>(
        ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "armor_durability_percent")
    );
    
    public static final ContextKey<Integer> CHEST_USED_SLOTS_CONTEXT = new ContextKey<>(
        ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "chest_used_slots")
    );
    
    public static final ContextKey<Component> CUSTOM_NAME_CONTEXT = new ContextKey<>(
        ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "custom_name")
    );
    
    public static final ContextKey<Boolean> IS_TAMED_CONTEXT = new ContextKey<>(
        ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "is_tamed")
    );
    
    public static final ContextKey<Integer> ENTITY_ID_CONTEXT = new ContextKey<>(
        ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "entity_id")
    );
    
    /**
     * Registriert einen Render State Modifier für WolfRenderer
     * Dieser fügt alle benötigten Wolf-Informationen zum WolfRenderState hinzu
     */
    @SubscribeEvent
    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        WolfArmorandStorageLegacy.LOGGER.info("Registering Render State Modifiers...");
        
        // Registriere Modifier für WolfRenderer
        event.registerEntityModifier(
            new TypeToken<WolfRenderer>(){},
            (wolf, renderState) -> {
                // Chest-Daten
                boolean hasChest = WolfHelper.hasChest(wolf);
                renderState.setRenderData(HAS_CHEST_CONTEXT, hasChest);
                
                // Health-Daten
                renderState.setRenderData(HEALTH_CONTEXT, wolf.getHealth());
                renderState.setRenderData(MAX_HEALTH_CONTEXT, wolf.getMaxHealth());
                
                // Armor-Daten
                ItemStack armorStack = WolfHelper.getArmorStack(wolf);
                int armorValue = 0;
                float durabilityPercent = 1.0f;
                
                if (!armorStack.isEmpty() && armorStack.getItem() instanceof WolfArmorItem wolfArmorItem) {
                    armorValue = wolfArmorItem.getMaterial().defense().getOrDefault(
                        net.minecraft.world.item.equipment.ArmorType.BODY, 0
                    );
                    
                    int maxDurability = armorStack.getMaxDamage();
                    int currentDamage = armorStack.getDamageValue();
                    int remainingDurability = maxDurability - currentDamage;
                    durabilityPercent = maxDurability > 0 ? (float) remainingDurability / maxDurability : 1.0f;
                }
                
                renderState.setRenderData(ARMOR_VALUE_CONTEXT, armorValue);
                renderState.setRenderData(ARMOR_DURABILITY_PERCENT_CONTEXT, durabilityPercent);
                
                // Chest-Inventar-Daten
                int usedSlots = 0;
                if (hasChest) {
                    var inventory = WolfHelper.getInventory(wolf);
                    for (ItemStack stack : inventory) {
                        if (!stack.isEmpty()) {
                            usedSlots++;
                        }
                    }
                }
                renderState.setRenderData(CHEST_USED_SLOTS_CONTEXT, usedSlots);
                
                // Wolf-Metadaten
                renderState.setRenderData(CUSTOM_NAME_CONTEXT, 
                    wolf.hasCustomName() ? wolf.getCustomName() : Component.literal("Wolf"));
                renderState.setRenderData(IS_TAMED_CONTEXT, wolf.isTame());
                renderState.setRenderData(ENTITY_ID_CONTEXT, wolf.getId());
            }
        );
        
        WolfArmorandStorageLegacy.LOGGER.info("Registered Wolf Render State Modifier with all Wolf data");
    }
}
