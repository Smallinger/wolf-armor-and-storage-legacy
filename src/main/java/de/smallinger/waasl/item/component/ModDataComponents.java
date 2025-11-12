package de.smallinger.waasl.item.component;

import de.smallinger.waasl.WolfArmorandStorageLegacy;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registrierung aller Custom Data Components
 */
public class ModDataComponents {
    
    public static final DeferredRegister.DataComponents REGISTRAR = 
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, WolfArmorandStorageLegacy.MODID);

    /**
     * Wolf Armor Tooltip Component - Zeigt Defense/Toughness/Knockback an
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WolfArmorTooltip>> WOLF_ARMOR_TOOLTIP =
            REGISTRAR.registerComponentType(
                    "wolf_armor_tooltip",
                    builder -> builder
                            .persistent(WolfArmorTooltip.CODEC)
                            .networkSynchronized(WolfArmorTooltip.STREAM_CODEC)
            );
}
