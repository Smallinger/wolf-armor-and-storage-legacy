package de.smallinger.waasl;

import de.smallinger.waasl.client.gui.WolfInventoryScreen;
import de.smallinger.waasl.client.renderer.WolfArmorRenderLayer;
import de.smallinger.waasl.client.renderer.WolfChestRenderLayer;
import de.smallinger.waasl.client.renderer.WolfCustomNameTagRenderer;
import de.smallinger.waasl.menu.ModMenuTypes;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = WolfArmorandStorageLegacy.MODID, dist = Dist.CLIENT)
public class WolfArmorandStorageLegacyClient {
    
    public WolfArmorandStorageLegacyClient(IEventBus modEventBus, ModContainer modContainer) {
        WolfArmorandStorageLegacy.LOGGER.info("Wolf Armor and Storage Legacy Client initializing...");
        
        // Register config screen
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        
        // Register Menu Screens
        modEventBus.addListener(this::registerScreens);
        
        // Register Wolf Render Layers
        modEventBus.addListener(this::onRegisterLayers);
        
        // Register Custom Wolf NameTag Renderer (on game event bus)
        NeoForge.EVENT_BUS.addListener(WolfCustomNameTagRenderer::onRenderWolf);
        NeoForge.EVENT_BUS.addListener(WolfCustomNameTagRenderer::onRenderNameTag);
        WolfArmorandStorageLegacy.LOGGER.info("Registered Custom Wolf NameTag Renderer");
    }
    
    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.WOLF_INVENTORY.get(), WolfInventoryScreen::new);
        WolfArmorandStorageLegacy.LOGGER.info("Registered Wolf Inventory Screen");
    }
    
    /**
     * Fügt alle Render Layers zum Wolf Renderer hinzu:
     * - Armor Layer (rendert Rüstung auf dem Wolf)
     * - Chest Layer (rendert Taschen am Wolf)
     * - Info Layer (zeigt Informationen über dem Wolf)
     */
    private void onRegisterLayers(EntityRenderersEvent.AddLayers event) {
        // Hole den Wolf Renderer
        var wolfRenderer = event.getRenderer(EntityType.WOLF);
        
        if (wolfRenderer instanceof WolfRenderer renderer) {
            // Füge unsere Render Layers hinzu
            renderer.addLayer(new WolfArmorRenderLayer(renderer, event.getEntityModels()));
            WolfArmorandStorageLegacy.LOGGER.info("Registered Wolf Armor Render Layer");
            
            renderer.addLayer(new WolfChestRenderLayer(renderer, event.getEntityModels()));
            WolfArmorandStorageLegacy.LOGGER.info("Registered Wolf Chest Render Layer");
            
            // Note: Wolf info display is now handled by WolfCustomNameTagRenderer event listener
            // No longer using WolfInfoRenderLayer
        } else {
            WolfArmorandStorageLegacy.LOGGER.error("Failed to register Wolf Layers - Wolf Renderer not found!");
        }
    }
}
