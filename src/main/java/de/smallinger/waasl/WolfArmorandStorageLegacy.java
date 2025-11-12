package de.smallinger.waasl;

import com.mojang.logging.LogUtils;
import de.smallinger.waasl.attachment.ModAttachments;
import de.smallinger.waasl.item.ModCreativeTabs;
import de.smallinger.waasl.item.ModItems;
import de.smallinger.waasl.item.component.ModDataComponents;
import de.smallinger.waasl.menu.ModMenuTypes;
import de.smallinger.waasl.sound.ModSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(WolfArmorandStorageLegacy.MODID)
public class WolfArmorandStorageLegacy {
    public static final String MODID = "wolfarmorandstoragelegacy";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WolfArmorandStorageLegacy(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Wolf Armor and Storage Legacy initializing...");

        // Register Config
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register Data Components
        ModDataComponents.REGISTRAR.register(modEventBus);
        LOGGER.info("Registered {} data components", ModDataComponents.REGISTRAR.getEntries().size());

        // Register Attachments
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        LOGGER.info("Registered {} attachment types", ModAttachments.ATTACHMENT_TYPES.getEntries().size());

        // Register Items
        ModItems.ITEMS.register(modEventBus);
        LOGGER.info("Registered {} wolf armor items", ModItems.ITEMS.getEntries().size());

        // Register Creative Tabs
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        LOGGER.info("Registered {} creative tabs", ModCreativeTabs.CREATIVE_MODE_TABS.getEntries().size());

        // Register Menu Types
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        LOGGER.info("Registered {} menu types", ModMenuTypes.MENU_TYPES.getEntries().size());

        // Register Sound Events
        ModSounds.SOUND_EVENTS.register(modEventBus);
        LOGGER.info("Registered {} sound events", ModSounds.SOUND_EVENTS.getEntries().size());

        // TODO: Register Event Handlers
        // NeoForge.EVENT_BUS.register(ModEventHandler.class);

        // TODO: Register Packets
        // modEventBus.addListener(ModMessages::register);

        LOGGER.info("Wolf Armor and Storage Legacy initialized!");
    }
}
