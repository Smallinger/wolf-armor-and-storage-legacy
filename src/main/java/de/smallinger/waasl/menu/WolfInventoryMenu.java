package de.smallinger.waasl.menu;

import de.smallinger.waasl.item.ModItems;
import de.smallinger.waasl.item.WolfArmorItem;
import de.smallinger.waasl.item.WolfChestItem;
import de.smallinger.waasl.util.WolfHelper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Menu für Wolf Inventar mit Armor + Chest Slots
 * Layout:
 * - Slot 0: Armor Slot (oben links)
 * - Slot 1: Chest Upgrade Slot (darunter)
 * - Slots 2-15: Chest Storage (14 Slots, 7x2, nur aktiv wenn Chest vorhanden)
 * - Slots 16+: Player Inventory
 */
public class WolfInventoryMenu extends AbstractContainerMenu {
    
    private static final int WOLF_INVENTORY_SIZE = 14; // 2 Reihen à 7 Slots
    
    private final TamableAnimal wolf;
    private final Container wolfInventory;
    private final Container wolfArmorContainer;
    private boolean hasChestEquipped = false; // Client-seitig synchronisiert

    /**
     * Client-seitiger Konstruktor
     */
    public WolfInventoryMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, new SimpleContainer(WOLF_INVENTORY_SIZE), new SimpleContainer(2));
    }

    /**
     * Server-seitiger Konstruktor
     */
    public WolfInventoryMenu(int containerId, Inventory playerInventory, TamableAnimal wolf, Container wolfInventory, Container wolfArmorContainer) {
        super(ModMenuTypes.WOLF_INVENTORY.get(), containerId);
        
        this.wolf = wolf;
        this.wolfInventory = wolfInventory;
        this.wolfArmorContainer = wolfArmorContainer;
        
        // Data Slot für Chest-Status (Server -> Client Sync)
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                // Server: Lese von Wolf
                return wolf != null && WolfHelper.hasChest(wolf) ? 1 : 0;
            }
            
            @Override
            public void set(int value) {
                // Client: Speichere lokal
                hasChestEquipped = value == 1;
            }
        });
        
        // Slot 0: Armor Slot (oben links bei x=8, y=18)
        this.addSlot(new Slot(wolfArmorContainer, 0, 8, 18) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof WolfArmorItem;
            }
            
            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                super.onTake(player, stack);
                // Wenn Rüstung entfernt wird, update sofort
                if (wolf != null && !player.level().isClientSide()) {
                    WolfHelper.setArmorStack(wolf, ItemStack.EMPTY);
                }
            }
            
            @Override
            public void set(@NotNull ItemStack stack) {
                super.set(stack);
                // Wenn Rüstung hinzugefügt/geändert wird, update sofort
                if (wolf != null && !wolf.level().isClientSide()) {
                    WolfHelper.setArmorStack(wolf, stack);
                }
            }
            
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        
        // Slot 1: Chest Upgrade Slot (darunter bei x=8, y=36)
        this.addSlot(new Slot(wolfArmorContainer, 1, 8, 36) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof WolfChestItem;
            }
            
            @Override
            public boolean mayPickup(@NotNull Player player) {
                // Verhindere das Entfernen des Chests wenn Items im Inventar sind
                // Prüfe das GUI-Container (wolfInventory), nicht das Wolf-Inventar
                for (int i = 0; i < wolfInventory.getContainerSize(); i++) {
                    if (!wolfInventory.getItem(i).isEmpty()) {
                        return false; // Inventar nicht leer - Chest kann nicht entfernt werden
                    }
                }
                return true;
            }
            
            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                super.onTake(player, stack);
                // Wenn Chest entfernt wird, update den Status sofort
                if (wolf != null && !player.level().isClientSide()) {
                    WolfHelper.setHasChest(wolf, false);
                }
            }
            
            @Override
            public void set(@NotNull ItemStack stack) {
                super.set(stack);
                // Wenn Chest hinzugefügt wird, update den Status sofort
                if (wolf != null && !stack.isEmpty()) {
                    WolfHelper.setHasChest(wolf, true);
                }
            }
            
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        
        // Slots 2-15: Wolf Chest Storage (7x2 Grid, rechts neben den Armor/Chest Slots)
        // Position: x=44, y=18
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 7; col++) {
                final int slotIndex = col + row * 7;
                this.addSlot(new Slot(wolfInventory, slotIndex, 44 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean isActive() {
                        // Server: Prüfe Wolf direkt
                        // Client: Verwende synchronisierten Wert
                        if (wolf != null) {
                            return WolfHelper.hasChest(wolf);
                        }
                        return hasChestEquipped;
                    }
                });
            }
        }
        
        // Player Inventory (3 Reihen à 9 Slots)
        // Position: y=84 (Abstand nach Wolf-Slots)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        
        // Player Hotbar (9 Slots)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        // Client: Immer true
        if (wolf == null) {
            return true;
        }
        
        // Server: Prüfe ob Wolf noch existiert, gezähmt ist und in Reichweite
        return wolf.isAlive() 
            && wolf.isTame() 
            && wolf.distanceToSqr(player) <= 64.0D; // 8 Blöcke Reichweite
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            
            // Index-Bereiche:
            // 0: Armor Slot
            // 1: Chest Upgrade Slot
            // 2-15: Wolf Inventory (14 Slots, 7x2)
            // 16-42: Player Inventory (27 Slots)
            // 43-51: Player Hotbar (9 Slots)
            
            if (index == 0) {
                // Aus Armor Slot -> Player-Inventar
                if (!this.moveItemStackTo(stack, 16, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 1) {
                // Aus Chest Slot -> Player-Inventar
                // Prüfe ob Inventar leer ist
                boolean hasItems = false;
                for (int i = 0; i < wolfInventory.getContainerSize(); i++) {
                    if (!wolfInventory.getItem(i).isEmpty()) {
                        hasItems = true;
                        break;
                    }
                }
                
                if (hasItems) {
                    // Inventar nicht leer - Chest kann nicht entfernt werden
                    return ItemStack.EMPTY;
                }
                
                if (!this.moveItemStackTo(stack, 16, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 16) {
                // Aus Wolf-Inventar -> Player-Inventar
                if (!this.moveItemStackTo(stack, 16, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Aus Player-Inventar -> prüfe wohin
                if (stack.getItem() instanceof WolfArmorItem) {
                    // Wolf Armor -> Armor Slot
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() instanceof WolfChestItem) {
                    // Wolf Chest -> Chest Slot
                    if (!this.moveItemStackTo(stack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    // Andere Items -> Wolf Inventory (nur wenn Chest vorhanden)
                    if (wolf != null && WolfHelper.hasChest(wolf)) {
                        if (!this.moveItemStackTo(stack, 2, 16, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
            }
            
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, stack);
        }
        
        return result;
    }

    /**
     * Wird beim Schließen des Menüs aufgerufen
     */
    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.wolfInventory.stopOpen(player);
        this.wolfArmorContainer.stopOpen(player);
        
        // Sync armor/chest/inventory changes back to wolf
        if (wolf != null && !player.level().isClientSide()) {
            // Update armor
            ItemStack armorStack = wolfArmorContainer.getItem(0);
            WolfHelper.setArmorStack(wolf, armorStack);
            
            // Update chest status - Chest Item wird NICHT gedroppt!
            ItemStack chestStack = wolfArmorContainer.getItem(1);
            boolean hadChest = WolfHelper.hasChest(wolf);
            boolean hasChestNow = !chestStack.isEmpty();
            
            if (hadChest && !hasChestNow) {
                // Chest wurde entfernt - gib das Item dem Spieler
                if (!player.getInventory().add(new ItemStack(ModItems.WOLF_CHEST.get()))) {
                    player.drop(new ItemStack(ModItems.WOLF_CHEST.get()), false);
                }
                WolfHelper.setHasChest(wolf, false);
            } else if (!hadChest && hasChestNow) {
                // Chest wurde hinzugefügt
                WolfHelper.setHasChest(wolf, true);
            }
            
            // Update inventory items
            var wolfInventoryData = WolfHelper.getInventoryData(wolf);
            var wolfItems = wolfInventoryData.getItems();
            for (int i = 0; i < 14; i++) {
                wolfItems.set(i, wolfInventory.getItem(i));
            }
            // Trigger sync to client after inventory changes
            wolf.setData(de.smallinger.waasl.attachment.ModAttachments.WOLF_INVENTORY, wolfInventoryData);
        }
    }
    
    /**
     * Prüft ob eine Chest equipped ist (für Client-Side Rendering)
     */
    public boolean hasChestEquipped() {
        return hasChestEquipped;
    }
    
    public TamableAnimal getWolf() {
        return wolf;
    }
}
