package de.smallinger.waasl.item;

import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.neoforge.common.Tags;

import java.util.EnumMap;

/**
 * Definition aller Wolf Armor Materials mit ihren Eigenschaften
 * Basiert auf NeoForge 1.21.10 ArmorMaterial API mit TagKey für Reparaturmaterial
 */
public class WolfArmorMaterials {

    public static final ArmorMaterial LEATHER = new ArmorMaterial(
            // Haltbarkeit - Base wird mit 16 multipliziert für BODY slot!
            5,  // 5 × 16 = 80 (wie Vanilla Wolf Armor)
            // Verteidigungswerte nach ArmorType
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 0);
                map.put(ArmorType.LEGGINGS, 0);
                map.put(ArmorType.CHESTPLATE, 0);
                map.put(ArmorType.HELMET, 0);
                map.put(ArmorType.BODY, 6);
            }),
            // Verzauberbarkeit
            15,
            // Ausrüstungsgeräusch
            SoundEvents.ARMOR_EQUIP_LEATHER,
            // Härtewert (Toughness)
            0.0F,
            // Rückstoßresistenz
            0.0F,
            // Reparaturmaterial als TagKey
            Tags.Items.LEATHERS,
            // Equipment Asset ID
            ResourceKey.create(EquipmentAssets.ROOT_ID, 
                ResourceLocation.fromNamespaceAndPath("wolfarmorandstoragelegacy", "leather_wolf"))
    );

    public static final ArmorMaterial COPPER = new ArmorMaterial(
            10,  // 10 × 16 = 160 (~200% von Vanilla)
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 0);
                map.put(ArmorType.LEGGINGS, 0);
                map.put(ArmorType.CHESTPLATE, 0);
                map.put(ArmorType.HELMET, 0);
                map.put(ArmorType.BODY, 10);
            }),
            8,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            0.0F,
            0.0F,
            Tags.Items.INGOTS_COPPER,
            ResourceKey.create(EquipmentAssets.ROOT_ID, 
                ResourceLocation.fromNamespaceAndPath("wolfarmorandstoragelegacy", "copper_wolf"))
    );

    public static final ArmorMaterial CHAINMAIL = new ArmorMaterial(
            11,  // 11 × 16 = 176 (~220% von Vanilla)
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 0);
                map.put(ArmorType.LEGGINGS, 0);
                map.put(ArmorType.CHESTPLATE, 0);
                map.put(ArmorType.HELMET, 0);
                map.put(ArmorType.BODY, 12);
            }),
            12,
            SoundEvents.ARMOR_EQUIP_CHAIN,
            0.0F,
            0.0F,
            Tags.Items.INGOTS_IRON,
            ResourceKey.create(EquipmentAssets.ROOT_ID, 
                ResourceLocation.fromNamespaceAndPath("wolfarmorandstoragelegacy", "chainmail_wolf"))
    );

    public static final ArmorMaterial IRON = new ArmorMaterial(
            15,  // 15 × 16 = 240 (~300% von Vanilla)
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 0);
                map.put(ArmorType.LEGGINGS, 0);
                map.put(ArmorType.CHESTPLATE, 0);
                map.put(ArmorType.HELMET, 0);
                map.put(ArmorType.BODY, 15);
            }),
            9,
            SoundEvents.ARMOR_EQUIP_IRON,
            0.0F,
            0.0F,
            Tags.Items.INGOTS_IRON,
            ResourceKey.create(EquipmentAssets.ROOT_ID, 
                ResourceLocation.fromNamespaceAndPath("wolfarmorandstoragelegacy", "iron_wolf"))
    );

    public static final ArmorMaterial GOLD = new ArmorMaterial(
            7,  // 7 × 16 = 112 (schwächer, aber enchantable)
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 0);
                map.put(ArmorType.LEGGINGS, 0);
                map.put(ArmorType.CHESTPLATE, 0);
                map.put(ArmorType.HELMET, 0);
                map.put(ArmorType.BODY, 7);
            }),
            25,
            SoundEvents.ARMOR_EQUIP_GOLD,
            0.0F,
            0.0F,
            Tags.Items.INGOTS_GOLD,
            ResourceKey.create(EquipmentAssets.ROOT_ID, 
                ResourceLocation.fromNamespaceAndPath("wolfarmorandstoragelegacy", "gold_wolf"))
    );

    public static final ArmorMaterial DIAMOND = new ArmorMaterial(
            33,  // 33 × 16 = 528 (~650% von Vanilla)
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 0);
                map.put(ArmorType.LEGGINGS, 0);
                map.put(ArmorType.CHESTPLATE, 0);
                map.put(ArmorType.HELMET, 0);
                map.put(ArmorType.BODY, 20);
            }),
            10,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            2.0F,
            0.0F,
            Tags.Items.GEMS_DIAMOND,
            ResourceKey.create(EquipmentAssets.ROOT_ID, 
                ResourceLocation.fromNamespaceAndPath("wolfarmorandstoragelegacy", "diamond_wolf"))
    );

    public static final ArmorMaterial NETHERITE = new ArmorMaterial(
            37,  // 37 × 16 = 592 (~730% von Vanilla)
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 0);
                map.put(ArmorType.LEGGINGS, 0);
                map.put(ArmorType.CHESTPLATE, 0);
                map.put(ArmorType.HELMET, 0);
                map.put(ArmorType.BODY, 24);
            }),
            15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            3.0F,
            0.1F,
            Tags.Items.INGOTS_NETHERITE,
            ResourceKey.create(EquipmentAssets.ROOT_ID, 
                ResourceLocation.fromNamespaceAndPath("wolfarmorandstoragelegacy", "netherite_wolf"))
    );

    /**
     * Konstruktor privat, da dies eine Utility-Klasse ist
     */
    private WolfArmorMaterials() {
        throw new UnsupportedOperationException("Utility class");
    }
}
