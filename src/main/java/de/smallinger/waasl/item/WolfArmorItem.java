package de.smallinger.waasl.item;

import de.smallinger.waasl.WolfArmorandStorageLegacy;
import de.smallinger.waasl.item.component.ModDataComponents;
import de.smallinger.waasl.item.component.WolfArmorTooltip;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * Wolf Armor Item mit ArmorMaterial und Tooltip-Unterstützung
 */
public class WolfArmorItem extends Item {
    
    private final ArmorMaterial material;
    private final String armorType;

    /**
     * Erstellt ein neues Wolf Armor Item
     * 
     * @param material Das ArmorMaterial mit allen Eigenschaften
     * @param armorType Der Typ der Rüstung (für spätere Verwendung)
     * @param properties Die Item Properties
     */
    public WolfArmorItem(ArmorMaterial material, String armorType, Properties properties) {
        super(properties.wolfArmor(material).component(
                ModDataComponents.WOLF_ARMOR_TOOLTIP.get(),
                new WolfArmorTooltip(
                        material.defense().getOrDefault(ArmorType.BODY, 0),
                        material.toughness(),
                        material.knockbackResistance()
                )
        ));
        this.material = material;
        this.armorType = armorType;
    }

    /**
     * Gibt das ArmorMaterial zurück
     */
    public ArmorMaterial getMaterial() {
        return material;
    }

    /**
     * Gibt den Armor-Typ zurück
     */
    public String getArmorType() {
        return armorType;
    }
    
    /**
     * Gibt die Textur für die Rüstung zurück
     * Minecraft 1.21+ verwendet das Equipment-System: /entity/equipment/wolf_body/
     */
    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath(
            WolfArmorandStorageLegacy.MODID,
            "textures/entity/equipment/wolf_body/" + armorType + ".png"
        );
    }
    
    /**
     * Gibt die Overlay-Textur für färbbare Rüstungen zurück
     * (Nur für Leather verwendet)
     * Minecraft 1.21+ verwendet das Equipment-System: /entity/equipment/wolf_body/
     */
    public ResourceLocation getOverlayTexture() {
        return ResourceLocation.fromNamespaceAndPath(
            WolfArmorandStorageLegacy.MODID,
            "textures/entity/equipment/wolf_body/" + armorType + "_overlay.png"
        );
    }
}
