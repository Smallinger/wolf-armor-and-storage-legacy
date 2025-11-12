package de.smallinger.waasl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

import de.smallinger.waasl.client.event.RenderStateModifierHandler;

/**
 * Render layer stub - actual rendering happens in RenderLivingEvent.Post
 * using SubmitNodeCollector.submitText() for custom text rendering.
 */
public class WolfInfoRenderLayer extends RenderLayer<WolfRenderState, WolfModel> {

    public WolfInfoRenderLayer(RenderLayerParent<WolfRenderState, WolfModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, 
                      WolfRenderState renderState, float yRot, float xRot) {
        // Rendering happens in RenderLivingEvent.Post
    }
    
    /**
     * Event handler that renders custom nameplate above tamed wolves.
     */
    public static class WolfInfoRenderer {
        
        private static final float SCALE = 0.025f;
        private static final float LINE_HEIGHT = 10.0f;
        
        @SubscribeEvent
        public static void onRenderWolf(RenderLivingEvent.Post<?, ?, ?> event) {
            // Only process wolf render states
            if (!(event.getRenderState() instanceof WolfRenderState renderState)) {
                return;
            }
            
            // Only render for tamed wolves
            Boolean isTamed = renderState.getRenderData(RenderStateModifierHandler.IS_TAMED_CONTEXT);
            if (isTamed == null || !isTamed) {
                return;
            }
            
            // Load data from ContextKeys
            Component customName = renderState.getRenderData(RenderStateModifierHandler.CUSTOM_NAME_CONTEXT);
            Float health = renderState.getRenderData(RenderStateModifierHandler.HEALTH_CONTEXT);
            Float maxHealth = renderState.getRenderData(RenderStateModifierHandler.MAX_HEALTH_CONTEXT);
            Integer armorValue = renderState.getRenderData(RenderStateModifierHandler.ARMOR_VALUE_CONTEXT);
            Float armorDurability = renderState.getRenderData(RenderStateModifierHandler.ARMOR_DURABILITY_PERCENT_CONTEXT);
            Boolean hasChest = renderState.getRenderData(RenderStateModifierHandler.HAS_CHEST_CONTEXT);
            Integer usedSlots = renderState.getRenderData(RenderStateModifierHandler.CHEST_USED_SLOTS_CONTEXT);
            
            // Need at least health to render
            if (health == null || maxHealth == null) {
                return;
            }
            
            // Fallback values
            if (armorValue == null) armorValue = 0;
            if (armorDurability == null) armorDurability = 1.0f;
            if (hasChest == null) hasChest = false;
            if (usedSlots == null) usedSlots = 0;
            
            PoseStack poseStack = event.getPoseStack();
            SubmitNodeCollector nodeCollector = event.getSubmitNodeCollector();
            int packedLight = renderState.lightCoords;
            Font font = Minecraft.getInstance().font;
            
            poseStack.pushPose();
            
            // Position above wolf (similar to nametag position)
            // Wolf height is ~0.85 blocks, add extra space above
            float yOffset = renderState.isSitting ? 2.0f : 2.0f;
            poseStack.translate(0.0, yOffset, 0.0);
            
            // Billboard - face camera
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
            
            // Scale for proper text size
            poseStack.scale(-SCALE, -SCALE, SCALE);
            
            float currentY = 0.0f;
            
            // Line 1: Custom name (if set)
            if (customName != null && !customName.getString().isEmpty()) {
                FormattedCharSequence nameSequence = customName.getVisualOrderText();
                int nameWidth = font.width(nameSequence);
                float nameX = -nameWidth / 2.0f;
                
                nodeCollector.submitText(
                    poseStack,
                    nameX, currentY,
                    nameSequence,
                    true, // Drop shadow for readability
                    Font.DisplayMode.NORMAL,
                    packedLight,
                    0xFFFFFFFF, // White text
                    0, // No background (doesn't work correctly)
                    0
                );
                currentY += LINE_HEIGHT;
            }
            
            // Line 2: Health with color coding
            float healthPercent = health / maxHealth;
            int healthColor = getHealthColor(healthPercent);
            Component healthComponent = Component.literal(String.format("♥ %.1f/%.1f", health, maxHealth));
            FormattedCharSequence healthSequence = healthComponent.getVisualOrderText();
            int healthWidth = font.width(healthSequence);
            float healthX = -healthWidth / 2.0f;
            
            nodeCollector.submitText(
                poseStack,
                healthX, currentY,
                healthSequence,
                true, // Drop shadow for readability
                Font.DisplayMode.NORMAL,
                packedLight,
                healthColor,
                0, // No background (doesn't work correctly)
                0
            );
            currentY += LINE_HEIGHT;
            
            // Line 3: Armor (if equipped)
            if (armorValue > 0) {
                int armorColor = getDurabilityColor(armorDurability);
                Component armorComponent = Component.literal(String.format("⚔ %d (%.0f%%)", armorValue, armorDurability * 100));
                FormattedCharSequence armorSequence = armorComponent.getVisualOrderText();
                int armorWidth = font.width(armorSequence);
                float armorX = -armorWidth / 2.0f;
                
                nodeCollector.submitText(
                    poseStack,
                    armorX, currentY,
                    armorSequence,
                    true, // Drop shadow for readability
                    Font.DisplayMode.NORMAL,
                    packedLight,
                    armorColor,
                    0, // No background (doesn't work correctly)
                    0
                );
                currentY += LINE_HEIGHT;
            }
            
            // Line 4: Chest (if equipped)
            if (hasChest) {
                Component chestComponent = Component.literal(String.format("⌘ %d/15 items", usedSlots));
                FormattedCharSequence chestSequence = chestComponent.getVisualOrderText();
                int chestWidth = font.width(chestSequence);
                float chestX = -chestWidth / 2.0f;
                
                nodeCollector.submitText(
                    poseStack,
                    chestX, currentY,
                    chestSequence,
                    true, // Drop shadow for readability
                    Font.DisplayMode.NORMAL,
                    packedLight,
                    0xFFFFAA00, // Orange text
                    0, // No background (doesn't work correctly)
                    0
                );
            }
            
            poseStack.popPose();
        }
        
        private static int getHealthColor(float percent) {
            if (percent > 0.75f) return 0xFF00FF00; // Green
            if (percent > 0.5f)  return 0xFFFFFF00; // Yellow
            if (percent > 0.25f) return 0xFFFFAA00; // Orange
            return 0xFFFF0000; // Red
        }
        
        private static int getDurabilityColor(float percent) {
            if (percent > 0.75f) return 0xFF00FF00; // Green
            if (percent > 0.5f)  return 0xFFFFFF00; // Yellow
            if (percent > 0.25f) return 0xFFFFAA00; // Orange
            return 0xFFFF0000; // Red
        }
    }
}
