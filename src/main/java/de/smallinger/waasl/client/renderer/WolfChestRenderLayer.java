package de.smallinger.waasl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.smallinger.waasl.WolfArmorandStorageLegacy;
import de.smallinger.waasl.client.event.RenderStateModifierHandler;
import de.smallinger.waasl.client.model.WolfChestModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Render-Layer für Wolf-Truhe (Wolf Chest)
 * Rendert 2 Truhen-Taschen links und rechts am Wolf mit Band darüber
 */
public class WolfChestRenderLayer extends RenderLayer<WolfRenderState, WolfModel> {
    
    public static final ModelLayerLocation WOLF_CHEST_LAYER = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "wolf"),
        "chest"
    );
    
    private static final ResourceLocation CHEST_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        WolfArmorandStorageLegacy.MODID,
        "textures/entity/wolf/wolf_chest.png"
    );
    
    private final WolfChestModel chestModel;
    
    public WolfChestRenderLayer(RenderLayerParent<WolfRenderState, WolfModel> parent, EntityModelSet modelSet) {
        super(parent);
        // Erstelle das Custom Chest Model (eigenständig, nicht von WolfModel abgeleitet)
        this.chestModel = new WolfChestModel(modelSet.bakeLayer(WOLF_CHEST_LAYER));
    }
    
    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight,
                       WolfRenderState renderState, float partialTick, float yRot) {
        
        // Baby-Wölfe können keine Truhe tragen
        if (renderState.isBaby) {
            return;
        }
        
        // Lese hasChest aus dem RenderState via NeoForge's ContextKey System
        Boolean hasChest = renderState.getRenderData(RenderStateModifierHandler.HAS_CHEST_CONTEXT);
        
        // Wenn hasChest null oder false ist, nicht rendern
        if (hasChest == null || !hasChest) {
            return;
        }
        
        // Setup Animationen für das Chest Model
        this.chestModel.setupAnim(renderState);
        
        // Rendere die 2 Truhen-Taschen mit Band
        nodeCollector.submitModel(
            this.chestModel,
            renderState,
            poseStack,
            RenderType.entityCutoutNoCull(CHEST_TEXTURE),
            packedLight,
            OverlayTexture.NO_OVERLAY,
            renderState.outlineColor,
            null
        );
    }
}
