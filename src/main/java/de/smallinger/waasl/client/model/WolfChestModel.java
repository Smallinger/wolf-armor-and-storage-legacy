package de.smallinger.waasl.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.WolfRenderState;

/**
 * Model für Wolf Backpack/Chest
 * 2 Taschen (links und rechts) mit je Top und Bottom Teil
 * Basiert auf dem alten ModelWolfBackpack Design
 */
public class WolfChestModel extends EntityModel<WolfRenderState> {
    
    private final ModelPart root;
    private final ModelPart backpackRightTop;
    private final ModelPart backpackRightBottom;
    private final ModelPart backpackLeftTop;
    private final ModelPart backpackLeftBottom;
    
    private static final float INITIAL_Z_ROTATION = 0.139626F; // ~8 Grad Neigung nach außen
    
    public WolfChestModel(ModelPart root) {
        super(root);
        this.root = root;
        
        // Hole die Backpack-Parts aus dem Root (nur die 4 Taschen, kein Strap)
        this.backpackRightTop = root.getChild("backpack_right_top");
        this.backpackRightBottom = root.getChild("backpack_right_bottom");
        this.backpackLeftTop = root.getChild("backpack_left_top");
        this.backpackLeftBottom = root.getChild("backpack_left_bottom");
    }
    
    /**
     * Erstellt die Layer Definition für das Backpack Model
     * Texture: 16x32 Pixel
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        // Rechte Tasche - Oben (schmaler Teil)
        // UV: (0, 0), Size: 2x2x5, Position: rechts vom Wolf-Körper
        partdefinition.addOrReplaceChild("backpack_right_top",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(3.0F, -2.0F, 0.0F, 2.0F, 2.0F, 5.0F),
            PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 0.0F, 0.0F, -INITIAL_Z_ROTATION));
        
        // Rechte Tasche - Unten (breiterer Teil)
        // UV: (0, 7), Size: 3x4x5
        partdefinition.addOrReplaceChild("backpack_right_bottom",
            CubeListBuilder.create()
                .texOffs(0, 7)
                .addBox(3.0F, 0.0F, 0.0F, 3.0F, 4.0F, 5.0F),
            PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 0.0F, 0.0F, -INITIAL_Z_ROTATION));
        
        // Linke Tasche - Oben (NICHT gespiegelt - verwende gleiche UV wie rechts)
        // UV: (0, 16), Size: 2x2x5, Position: links vom Wolf-Körper
        partdefinition.addOrReplaceChild("backpack_left_top",
            CubeListBuilder.create()
                .texOffs(0, 16)
                .addBox(-5.0F, -2.0F, 0.0F, 2.0F, 2.0F, 5.0F),
            PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 0.0F, 0.0F, INITIAL_Z_ROTATION));
        
        // Linke Tasche - Unten (NICHT gespiegelt)
        // UV: (0, 23), Size: 3x4x5
        partdefinition.addOrReplaceChild("backpack_left_bottom",
            CubeListBuilder.create()
                .texOffs(0, 23)
                .addBox(-6.0F, 0.0F, 0.0F, 3.0F, 4.0F, 5.0F),
            PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 0.0F, 0.0F, INITIAL_Z_ROTATION));
        
        return LayerDefinition.create(meshdefinition, 16, 32); // Texture: 16x32
    }
    
    /**
     * Setup der Animationen basierend auf dem Wolf-Status
     * Angepasst für sitzende Wölfe und Bewegungsanimationen
     */
    @Override
    public void setupAnim(WolfRenderState renderState) {
        // Reset root
        this.root.getAllParts().forEach(ModelPart::resetPose);
        
        float rotationPointY = 14.0F;
        float rotationPointZ = 2.0F;
        float rotateAngleX = 0.0F;
        
        // Anpassung für sitzende Wölfe
        if (renderState.isSitting) {
            rotationPointY = 18.0F;
            rotationPointZ = 0.0F;
            rotateAngleX = (float) (-Math.PI / 4); // -45 Grad
        }
        
        // Setze Rotation Points
        this.backpackRightTop.setPos(0.0F, rotationPointY, rotationPointZ);
        this.backpackRightBottom.setPos(0.0F, rotationPointY, rotationPointZ);
        this.backpackLeftTop.setPos(0.0F, rotationPointY, rotationPointZ);
        this.backpackLeftBottom.setPos(0.0F, rotationPointY, rotationPointZ);
        
        // Animationen basierend auf Bewegung
        // walkAnimationPos und walkAnimationSpeed kommen vom RenderState
        float limbSwing = renderState.walkAnimationPos;
        float limbSwingAmount = renderState.walkAnimationSpeed;
        
        // Rechte Taschen - bewegen sich mit cos
        float rightSwing = (float) (Math.cos(limbSwing * 1.2F) * 0.15F * limbSwingAmount);
        this.backpackRightTop.zRot = -INITIAL_Z_ROTATION + rightSwing;
        this.backpackRightBottom.zRot = -INITIAL_Z_ROTATION + rightSwing;
        
        // Linke Taschen - bewegen sich mit sin (entgegengesetzt)
        float leftSwing = (float) (Math.sin(limbSwing * 1.2F) * 0.15F * limbSwingAmount);
        this.backpackLeftTop.zRot = INITIAL_Z_ROTATION + leftSwing;
        this.backpackLeftBottom.zRot = INITIAL_Z_ROTATION + leftSwing;
        
        // X-Rotation für alle Parts (wichtig beim Sitzen - kippt die Taschen nach vorne)
        this.backpackRightTop.xRot = rotateAngleX;
        this.backpackRightBottom.xRot = rotateAngleX;
        this.backpackLeftTop.xRot = rotateAngleX;
        this.backpackLeftBottom.xRot = rotateAngleX;
    }
}
