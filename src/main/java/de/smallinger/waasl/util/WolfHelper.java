package de.smallinger.waasl.util;

import de.smallinger.waasl.attachment.ModAttachments;
import de.smallinger.waasl.attachment.WolfArmorData;
import de.smallinger.waasl.attachment.WolfInventoryData;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;

/**
 * Helper-Klasse für den Zugriff auf Wolf-Daten via Attachments
 */
public class WolfHelper {
    
    // ========== ARMOR METHODS ==========
    
    /**
     * Gibt die Armor-Daten eines Wolfs zurück
     */
    public static WolfArmorData getArmorData(TamableAnimal wolf) {
        return wolf.getData(ModAttachments.WOLF_ARMOR);
    }

    /**
     * Gibt den Rüstungs-ItemStack eines Wolfs zurück
     */
    public static ItemStack getArmorStack(TamableAnimal wolf) {
        return getArmorData(wolf).getArmorStack();
    }

    /**
     * Setzt die Rüstung eines Wolfs
     * Baby-Wölfe können keine Rüstung tragen
     */
    public static void setArmorStack(TamableAnimal wolf, ItemStack stack) {
        // Baby-Wölfe können keine Rüstung tragen
        if (wolf.isBaby()) {
            return;
        }
        
        WolfArmorData data = getArmorData(wolf);
        data.setArmorStack(stack);
        
        // WICHTIG: Setze die Rüstung auch im Body-Armor-Slot für das Rendering!
        wolf.setBodyArmorItem(stack.copy());
        
        // Trigger sync to client
        wolf.setData(ModAttachments.WOLF_ARMOR, data);
    }

    /**
     * Prüft ob ein Wolf Rüstung trägt
     */
    public static boolean hasArmor(TamableAnimal wolf) {
        return getArmorData(wolf).hasArmor();
    }

    /**
     * Entfernt die Rüstung eines Wolfs und gibt sie zurück
     */
    public static ItemStack removeArmor(TamableAnimal wolf) {
        WolfArmorData data = getArmorData(wolf);
        ItemStack removed = data.removeArmor();
        
        // WICHTIG: Entferne auch die Rüstung aus dem Body-Armor-Slot für das Rendering!
        wolf.setBodyArmorItem(ItemStack.EMPTY);
        
        // Trigger sync to client
        wolf.setData(ModAttachments.WOLF_ARMOR, data);
        return removed;
    }

    // ========== INVENTORY METHODS ==========
    
    /**
     * Gibt die Inventar-Daten eines Wolfs zurück
     */
    public static WolfInventoryData getInventoryData(TamableAnimal wolf) {
        return wolf.getData(ModAttachments.WOLF_INVENTORY);
    }

    /**
     * Gibt die Items-Liste des Wolf-Inventars zurück
     */
    public static NonNullList<ItemStack> getInventory(TamableAnimal wolf) {
        return getInventoryData(wolf).getItems();
    }

    /**
     * Prüft ob ein Wolf eine Truhe hat
     */
    public static boolean hasChest(TamableAnimal wolf) {
        return getInventoryData(wolf).hasChest();
    }

    /**
     * Setzt ob ein Wolf eine Truhe hat
     * Baby-Wölfe können keine Truhe tragen
     */
    public static void setHasChest(TamableAnimal wolf, boolean hasChest) {
        // Baby-Wölfe können keine Truhe tragen
        if (wolf.isBaby()) {
            return;
        }
        
        WolfInventoryData data = getInventoryData(wolf);
        data.setHasChest(hasChest);
        // Trigger sync to client
        wolf.setData(ModAttachments.WOLF_INVENTORY, data);
    }

    /**
     * Gibt einen ItemStack aus dem Wolf-Inventar zurück
     */
    public static ItemStack getStackInSlot(TamableAnimal wolf, int slot) {
        return getInventoryData(wolf).getStackInSlot(slot);
    }

    /**
     * Setzt einen ItemStack im Wolf-Inventar
     */
    public static void setStackInSlot(TamableAnimal wolf, int slot, ItemStack stack) {
        WolfInventoryData data = getInventoryData(wolf);
        data.setStackInSlot(slot, stack);
        // Trigger sync to client
        wolf.setData(ModAttachments.WOLF_INVENTORY, data);
    }

    /**
     * Prüft ob das Wolf-Inventar leer ist
     */
    public static boolean isInventoryEmpty(TamableAnimal wolf) {
        return getInventoryData(wolf).isEmpty();
    }

    /**
     * Leert das Wolf-Inventar vollständig
     */
    public static void clearInventory(TamableAnimal wolf) {
        WolfInventoryData data = getInventoryData(wolf);
        data.clearInventory();
        // Trigger sync to client
        wolf.setData(ModAttachments.WOLF_INVENTORY, data);
    }

    /**
     * Prüft ob ein Wolf bereit ist, ein Inventar zu öffnen
     * (muss gezähmt sein, eine Truhe haben und KEIN Baby sein)
     */
    public static boolean canOpenInventory(TamableAnimal wolf) {
        return wolf.isTame() && !wolf.isBaby() && hasChest(wolf);
    }
}
