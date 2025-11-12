package de.smallinger.waasl.item;

import de.smallinger.waasl.WolfArmorandStorageLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Creative Tab für Wolf Armor and Storage Legacy Items
 */
public class ModCreativeTabs {
    
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WolfArmorandStorageLegacy.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOLF_ARMOR_TAB =
            CREATIVE_MODE_TABS.register("wolf_armor_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.wolfarmorandstoragelegacy"))
                    .icon(() -> new ItemStack(ModItems.DIAMOND_WOLF_ARMOR.get()))
                    .displayItems((parameters, output) -> {
                        // Add all armor items
                        output.accept(ModItems.LEATHER_WOLF_ARMOR.get());
                        output.accept(ModItems.COPPER_WOLF_ARMOR.get());
                        output.accept(ModItems.CHAINMAIL_WOLF_ARMOR.get());
                        output.accept(ModItems.IRON_WOLF_ARMOR.get());
                        output.accept(ModItems.GOLD_WOLF_ARMOR.get());
                        output.accept(ModItems.DIAMOND_WOLF_ARMOR.get());
                        output.accept(ModItems.NETHERITE_WOLF_ARMOR.get());
                        
                        // Add wolf chest
                        output.accept(ModItems.WOLF_CHEST.get());
                    })
                    .build()
            );
}
