package de.smallinger.waasl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.smallinger.waasl.item.WolfArmorItem;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

/**
 * Render-Layer für Wolf-Rüstung (Custom Wolf Armor)
 * Rendert die Rüstungs-Textur auf dem Wolf-Modell
 */
public class WolfArmorRenderLayer extends RenderLayer<WolfRenderState, WolfModel> {
    
    private final WolfModel adultModel;
    private final WolfModel babyModel;
    
    public WolfArmorRenderLayer(RenderLayerParent<WolfRenderState, WolfModel> parent, EntityModelSet modelSet) {
        super(parent);
        this.adultModel = new WolfModel(modelSet.bakeLayer(ModelLayers.WOLF_ARMOR));
        this.babyModel = new WolfModel(modelSet.bakeLayer(ModelLayers.WOLF_BABY_ARMOR));
    }
    
    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight,
                       WolfRenderState renderState, float partialTick, float yRot) {
        
        // Baby-Wölfe können keine Custom Armor tragen
        if (renderState.isBaby) {
            return;
        }
        
        // Hole Custom Armor aus RenderState
        ItemStack armorStack = renderState.bodyArmorItem;
        
        if (armorStack.isEmpty() || !(armorStack.getItem() instanceof WolfArmorItem wolfArmorItem)) {
            return;
        }
        
        // Wähle Modell basierend auf Baby/Adult
        WolfModel model = renderState.isBaby ? this.babyModel : this.adultModel;
        
        // Hole Textur für die Rüstung
        ResourceLocation armorTexture = wolfArmorItem.getTexture();
        
        // Rendere Basis-Rüstung
        nodeCollector.submitModel(
            model,
            renderState,
            poseStack,
            RenderType.entityCutoutNoCull(armorTexture),
            packedLight,
            OverlayTexture.NO_OVERLAY,
            renderState.outlineColor,
            null
        );
        
        // Wenn die Rüstung färbbar ist, rendere den overlay
        if (armorStack.has(net.minecraft.core.component.DataComponents.DYED_COLOR)) {
            DyedItemColor dyedColor = armorStack.get(net.minecraft.core.component.DataComponents.DYED_COLOR);
            if (dyedColor != null) {
                // Overlay-Textur für gefärbten Teil
                ResourceLocation overlayTexture = wolfArmorItem.getOverlayTexture();
                
                nodeCollector.submitModel(
                    model,
                    renderState,
                    poseStack,
                    RenderType.entityCutoutNoCull(overlayTexture),
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    dyedColor.rgb() | 0xFF000000, // Add alpha channel
                    null
                );
            }
        }
    }
}
