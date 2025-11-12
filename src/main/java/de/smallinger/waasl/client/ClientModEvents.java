package de.smallinger.waasl.client;

import de.smallinger.waasl.WolfArmorandStorageLegacy;
import de.smallinger.waasl.client.event.RenderStateModifierHandler;
import de.smallinger.waasl.client.model.WolfChestModel;
import de.smallinger.waasl.client.renderer.WolfChestRenderLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Client-seitige Mod-Event-Registrierung
 * Wird nur auf dem physikalischen Client geladen
 */
@Mod(value = WolfArmorandStorageLegacy.MODID, dist = Dist.CLIENT)
public class ClientModEvents {
    
    public ClientModEvents(IEventBus modEventBus) {
        WolfArmorandStorageLegacy.LOGGER.info("Registering client-side mod events...");
        
        // Registriere Render State Modifiers
        modEventBus.addListener(RenderStateModifierHandler::registerRenderStateModifiers);
        
        // Registriere Model Layers
        modEventBus.addListener(this::registerLayerDefinitions);
        
        WolfArmorandStorageLegacy.LOGGER.info("Client-side mod events registered!");
    }
    
    private void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        WolfArmorandStorageLegacy.LOGGER.info("Registering model layer definitions...");
        
        // Registriere Wolf Chest Model Layer
        event.registerLayerDefinition(WolfChestRenderLayer.WOLF_CHEST_LAYER, WolfChestModel::createBodyLayer);
        
        WolfArmorandStorageLegacy.LOGGER.info("Model layer definitions registered!");
    }
}
