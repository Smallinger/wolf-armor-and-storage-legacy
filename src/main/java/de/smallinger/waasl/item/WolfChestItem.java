package de.smallinger.waasl.item;

import net.minecraft.world.item.Item;

/**
 * Chest Item zum Ausrüsten von Wölfen mit Storage
 * Rechtsklick auf gezähmten Wolf gibt ihm ein Inventar
 */
public class WolfChestItem extends Item {
    
    public WolfChestItem(Properties properties) {
        super(properties);
    }
    
    // Die Logik für Rechtsklick kommt später in Phase 7 (Event Handler)
}
