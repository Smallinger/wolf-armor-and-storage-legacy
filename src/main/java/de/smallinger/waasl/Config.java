package de.smallinger.waasl;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // General Settings
    public static final ModConfigSpec.BooleanValue ENABLE_CRAFTING;
    public static final ModConfigSpec.BooleanValue ENABLE_WOLF_INVENTORY;
    public static final ModConfigSpec.IntValue INVENTORY_SIZE;
    
    // Behavior Settings
    public static final ModConfigSpec.BooleanValue ENABLE_AUTO_HEAL;
    public static final ModConfigSpec.BooleanValue ENABLE_DISPENSER;
    
    // Client Settings
    public static final ModConfigSpec.BooleanValue RENDER_ARMOR;
    public static final ModConfigSpec.BooleanValue RENDER_INVENTORY;
    public static final ModConfigSpec.BooleanValue SHOW_STATS;

    static {
        BUILDER.push("general");
        ENABLE_CRAFTING = BUILDER
            .comment("Enable crafting recipes for wolf armor")
            .define("enableCrafting", true);
        ENABLE_WOLF_INVENTORY = BUILDER
            .comment("Enable wolf inventory/backpack system")
            .define("enableWolfInventory", true);
        INVENTORY_SIZE = BUILDER
            .comment("Number of inventory slots per wolf (1-15)")
            .defineInRange("inventorySize", 6, 1, 15);
        BUILDER.pop();

        BUILDER.push("behavior");
        ENABLE_AUTO_HEAL = BUILDER
            .comment("Wolves automatically eat from their inventory when damaged")
            .define("enableAutoHeal", true);
        ENABLE_DISPENSER = BUILDER
            .comment("Dispensers can equip wolves with armor")
            .define("enableDispenser", true);
        BUILDER.pop();

        BUILDER.push("client");
        RENDER_ARMOR = BUILDER
            .comment("Render wolf armor on wolves")
            .define("renderArmor", true);
        RENDER_INVENTORY = BUILDER
            .comment("Render backpack on wolves with inventory")
            .define("renderInventory", true);
        SHOW_STATS = BUILDER
            .comment("Show armor/health bars above wolf heads")
            .define("showStats", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
