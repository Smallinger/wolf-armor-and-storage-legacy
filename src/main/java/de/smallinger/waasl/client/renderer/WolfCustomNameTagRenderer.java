package de.smallinger.waasl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.smallinger.waasl.client.event.RenderStateModifierHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import org.joml.Matrix4f;

public class WolfCustomNameTagRenderer {
    
    private static final double MAX_RENDER_DISTANCE = 64.0; // Max distance to render (in blocks)
    
    // Minecraft GUI sprite locations for icons
    private static final ResourceLocation HEART_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/full");
    private static final ResourceLocation HEART_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/half");
    private static final ResourceLocation HEART_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/container");
    
    private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_full");
    private static final ResourceLocation ARMOR_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_half");
    private static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_empty");
    
    private static final ResourceLocation CHEST_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/slot");
    
    private static final int ICON_SIZE = 9;
    
    /**
     * Hides the original vanilla nametag for the wolf we're looking at,
     * so only our custom info display is shown.
     */
    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent.DoRender event) {
        // Only process wolf render states
        if (!(event.getEntityRenderState() instanceof WolfRenderState renderState)) {
            return;
        }
        
        // Only for tamed wolves
        Boolean isTamed = renderState.getRenderData(RenderStateModifierHandler.IS_TAMED_CONTEXT);
        if (isTamed == null || !isTamed) {
            return;
        }
        
        // Check if player is looking at this specific wolf
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        
        Entity targetEntity = mc.crosshairPickEntity;
        if (!(targetEntity instanceof Wolf targetWolf) || !targetWolf.isTame()) {
            return; // Not looking at a tamed wolf
        }
        
        // Check if THIS wolf is the one being looked at
        Integer renderStateEntityId = renderState.getRenderData(RenderStateModifierHandler.ENTITY_ID_CONTEXT);
        if (renderStateEntityId == null || renderStateEntityId != targetWolf.getId()) {
            return; // This wolf is not the one being looked at
        }
        
        // Cancel the vanilla nametag rendering for this wolf
        event.setCanceled(true);
    }
    
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
        
        // Get camera and check distance
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        
        // Calculate distance to wolf
        double distanceSq = renderState.distanceToCameraSq;
        if (distanceSq > MAX_RENDER_DISTANCE * MAX_RENDER_DISTANCE) {
            return; // Too far away
        }
        
        // Check if player is looking at THIS specific wolf with crosshair (like vanilla nametags)
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        
        // Get the entity the player is currently looking at (crosshair target)
        Entity targetEntity = mc.crosshairPickEntity;
        
        // Only show info if looking at a tamed wolf
        if (!(targetEntity instanceof Wolf targetWolf) || !targetWolf.isTame()) {
            return; // Not looking at a tamed wolf
        }
        
        // IMPORTANT: Check if THIS wolf is the one being looked at
        Integer renderStateEntityId = renderState.getRenderData(RenderStateModifierHandler.ENTITY_ID_CONTEXT);
        if (renderStateEntityId == null || renderStateEntityId != targetWolf.getId()) {
            return; // This wolf is not the one being looked at
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
        
        // Now render each line as a separate nametag
        PoseStack poseStack = event.getPoseStack();
        SubmitNodeCollector nodeCollector = event.getSubmitNodeCollector();
        
        // Create CameraRenderState
        CameraRenderState cameraRenderState = new CameraRenderState();
        cameraRenderState.pos = cameraPos;
        cameraRenderState.orientation = camera.rotation().get(new org.joml.Quaternionf());
        cameraRenderState.initialized = true;
        
        // Position above wolf (similar to nametag)
        float baseYOffset = renderState.isSitting ? 1.3f : 1.8f;
        Vec3 basePos = new Vec3(0, baseYOffset, 0);
        
        int lineIndex = 0;
        
        // Line 1: Custom name (if set)
        if (customName != null && !customName.getString().isEmpty()) {
            submitNameTagLine(
                poseStack, nodeCollector, cameraRenderState,
                basePos, lineIndex++,
                customName,
                renderState.lightCoords,
                distanceSq
            );
        }
        
        // Line 2: Health with heart icon
        float healthPercent = health / maxHealth;
        int healthColor = getHealthColor(healthPercent);
        Component healthComponent = Component.literal(String.format("%.1f/%.1f", health, maxHealth))
            .withColor(healthColor);
        submitNameTagLineWithIcon(
            poseStack, nodeCollector, cameraRenderState,
            basePos, lineIndex++,
            HEART_FULL_SPRITE,
            healthComponent,
            renderState.lightCoords,
            distanceSq
        );
        
        // Line 3: Armor (if equipped)
        if (armorValue > 0) {
            int armorColor = getDurabilityColor(armorDurability);
            Component armorComponent = Component.literal(String.format("%d (%.0f%%)", armorValue, armorDurability * 100))
                .withColor(armorColor);
            submitNameTagLineWithIcon(
                poseStack, nodeCollector, cameraRenderState,
                basePos, lineIndex++,
                ARMOR_FULL_SPRITE,
                armorComponent,
                renderState.lightCoords,
                distanceSq
            );
        }
        
        // Line 4: Chest (if equipped)
        if (hasChest) {
            Component chestComponent = Component.literal(String.format("%d/15 items", usedSlots))
                .withColor(0xFFAA00); // Orange
            submitNameTagLineWithIcon(
                poseStack, nodeCollector, cameraRenderState,
                basePos, lineIndex++,
                CHEST_SPRITE,
                chestComponent,
                renderState.lightCoords,
                distanceSq
            );
        }
    }
    
    /**
     * Submits a nametag line with an icon sprite rendered before the text.
     * Uses Minecraft's font renderer with Unicode characters for icons.
     */
    private static void submitNameTagLineWithIcon(
        PoseStack poseStack,
        SubmitNodeCollector nodeCollector,
        CameraRenderState cameraRenderState,
        Vec3 basePos,
        int lineIndex,
        ResourceLocation iconSprite,
        Component text,
        int packedLight,
        double distanceSq
    ) {
        // Use Unicode characters that look like Minecraft icons
        // These are rendered by Minecraft's font renderer
        String iconChar;
        int iconColor;
        
        if (iconSprite.equals(HEART_FULL_SPRITE)) {
            iconChar = "♥"; // Heart symbol (looks like Minecraft health)
            iconColor = 0xFF0000; // Red
        } else if (iconSprite.equals(ARMOR_FULL_SPRITE)) {
            iconChar = "◈"; // Diamond shape (looks like Minecraft armor icon)
            iconColor = 0xAAAAAA; // Light gray/silver
        } else if (iconSprite.equals(CHEST_SPRITE)) {
            iconChar = "■"; // Filled square (represents chest/container)
            iconColor = 0xC4A66A; // Brown/chest color
        } else {
            iconChar = "•";
            iconColor = 0xFFFFFF;
        }
        
        // Create icon component with appropriate color
        Component iconComponent = Component.literal(iconChar).withColor(iconColor);
        
        // Combine icon with text (text keeps its own color)
        Component combinedText = iconComponent.copy()
            .append(Component.literal(" "))
            .append(text);
        
        // Each line offset by 10 pixels (scaled)
        float lineSpacing = 0.25f; // In world units (after scaling)
        Vec3 linePos = basePos.add(0, -lineIndex * lineSpacing, 0);
        
        nodeCollector.submitNameTag(
            poseStack,
            linePos,
            0, // yOffset handled by linePos
            combinedText,
            true, // seethrough = true for better visibility
            packedLight,
            distanceSq,
            cameraRenderState
        );
    }
    
    private static void submitNameTagLine(
        PoseStack poseStack,
        SubmitNodeCollector nodeCollector,
        CameraRenderState cameraRenderState,
        Vec3 basePos,
        int lineIndex,
        Component text,
        int packedLight,
        double distanceSq
    ) {
        // Each line offset by 10 pixels (scaled)
        float lineSpacing = 0.25f; // In world units (after scaling)
        Vec3 linePos = basePos.add(0, -lineIndex * lineSpacing, 0);
        
        nodeCollector.submitNameTag(
            poseStack,
            linePos,
            0, // yOffset handled by linePos
            text,
            true, // seethrough = true for better visibility
            packedLight,
            distanceSq,
            cameraRenderState
        );
    }
    
    private static int getHealthColor(float percent) {
        if (percent > 0.75f) return 0x00FF00; // Green
        if (percent > 0.5f)  return 0xFFFF00; // Yellow
        if (percent > 0.25f) return 0xFFAA00; // Orange
        return 0xFF0000; // Red
    }
    
    private static int getDurabilityColor(float percent) {
        if (percent > 0.75f) return 0x00FF00; // Green
        if (percent > 0.5f)  return 0xFFFF00; // Yellow
        if (percent > 0.25f) return 0xFFAA00; // Orange
        return 0xFF0000; // Red
    }
}
