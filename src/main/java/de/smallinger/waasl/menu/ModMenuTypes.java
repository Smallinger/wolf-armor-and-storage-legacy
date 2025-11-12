package de.smallinger.waasl.menu;

import de.smallinger.waasl.WolfArmorandStorageLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registrierung aller MenuTypes
 */
public class ModMenuTypes {
    
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = 
            DeferredRegister.create(Registries.MENU, WolfArmorandStorageLegacy.MODID);

    /**
     * Wolf Inventory Menu
     * Simple MenuType ohne Buffer (da SimpleMenuProvider verwendet wird)
     */
    public static final DeferredHolder<MenuType<?>, MenuType<WolfInventoryMenu>> WOLF_INVENTORY = 
            MENU_TYPES.register("wolf_inventory", () -> 
                new MenuType<>(WolfInventoryMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS)
            );
}
