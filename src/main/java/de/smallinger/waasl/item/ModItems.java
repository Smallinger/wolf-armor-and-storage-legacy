package de.smallinger.waasl.item;

import de.smallinger.waasl.WolfArmorandStorageLegacy;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = 
        DeferredRegister.createItems(WolfArmorandStorageLegacy.MODID);

    public static final DeferredItem<Item> LEATHER_WOLF_ARMOR = ITEMS.registerItem(
        "leather_wolf_armor",
        props -> new WolfArmorItem(WolfArmorMaterials.LEATHER, "leather", props)
    );

    public static final DeferredItem<Item> COPPER_WOLF_ARMOR = ITEMS.registerItem(
        "copper_wolf_armor",
        props -> new WolfArmorItem(WolfArmorMaterials.COPPER, "copper", props)
    );

    public static final DeferredItem<Item> CHAINMAIL_WOLF_ARMOR = ITEMS.registerItem(
        "chainmail_wolf_armor",
        props -> new WolfArmorItem(WolfArmorMaterials.CHAINMAIL, "chainmail", props)
    );

    public static final DeferredItem<Item> IRON_WOLF_ARMOR = ITEMS.registerItem(
        "iron_wolf_armor",
        props -> new WolfArmorItem(WolfArmorMaterials.IRON, "iron", props)
    );

    public static final DeferredItem<Item> GOLD_WOLF_ARMOR = ITEMS.registerItem(
        "gold_wolf_armor",
        props -> new WolfArmorItem(WolfArmorMaterials.GOLD, "gold", props)
    );

    public static final DeferredItem<Item> DIAMOND_WOLF_ARMOR = ITEMS.registerItem(
        "diamond_wolf_armor",
        props -> new WolfArmorItem(WolfArmorMaterials.DIAMOND, "diamond", props)
    );

    public static final DeferredItem<Item> NETHERITE_WOLF_ARMOR = ITEMS.registerItem(
        "netherite_wolf_armor",
        props -> new WolfArmorItem(WolfArmorMaterials.NETHERITE, "netherite", props.fireResistant())
    );

    public static final DeferredItem<Item> WOLF_CHEST = ITEMS.registerItem(
        "wolf_chest",
        props -> new WolfChestItem(props.stacksTo(1))
    );
}
