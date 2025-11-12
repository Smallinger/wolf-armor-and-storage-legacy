package de.smallinger.waasl.client.gui;

import de.smallinger.waasl.WolfArmorandStorageLegacy;
import de.smallinger.waasl.menu.WolfInventoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * GUI Screen für Wolf Inventory
 * Layout:
 * - Slot 0 (8,18): Armor Slot
 * - Slot 1 (8,36): Chest Upgrade Slot  
 * - Slots 2-15 (33,18): Storage Grid 7x2 (14 Slots)
 * - Player Inventory (8,84)
 * - Hotbar (8,142)
 */
public class WolfInventoryScreen extends AbstractContainerScreen<WolfInventoryMenu> {
    
    private static final ResourceLocation TEXTURE = 
            ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, 
                    "textures/gui/container/wolf_inventory.png");

    public WolfInventoryScreen(WolfInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        
        // GUI Größe anpassen für neues Layout
        this.imageHeight = 169; // Standard Container Height
        this.imageWidth = 176;  // Standard Container Width (9 Slots breit)
        this.inventoryLabelY = this.imageHeight - 96; // Position für "Inventory" Label (2px höher)
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // Render Background Texture mit RenderPipelines.GUI_TEXTURED (NeoForge 1.21+)
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.leftPos, 
                this.topPos, 
                0, 
                0, 
                this.imageWidth, 
                this.imageHeight,
                256,
                256
        );
        
        // Render graue Overlays für disabled Storage-Slots (wenn keine Chest equipped)
        if (!this.menu.hasChestEquipped()) {
            // Storage Slots: 7 Spalten x 2 Reihen, Start bei (33, 18)
            int startX = 44;
            int startY = 18;
            int slotSize = 18;
            
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 7; col++) {
                    int x = this.leftPos + startX + (col * slotSize);
                    int y = this.topPos + startY + (row * slotSize);
                    
                    // Halbtransparentes Schwarz (Alpha=128, RGB=0)
                    graphics.fill(x, y, x + 16, y + 16, 0x80000000);
                }
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Render Background (dunkel)
        super.render(graphics, mouseX, mouseY, partialTick);
        
        // Render Tooltips
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        // Render Title (z.B. "Wolf Chest")
        // 0xFF404040 = ARGB (FF=volle Deckkraft, 404040=dunkelgrau)
        graphics.drawString(
                this.font, 
                this.title, 
                this.titleLabelX, 
                this.titleLabelY, 
                0xFF404040, 
                false
        );
        
        // Render Player Inventory Label
        graphics.drawString(
                this.font, 
                this.playerInventoryTitle, 
                this.inventoryLabelX, 
                this.inventoryLabelY, 
                0xFF404040,
                false
        );
    }
}
