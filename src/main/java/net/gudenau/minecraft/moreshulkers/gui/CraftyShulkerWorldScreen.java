package net.gudenau.minecraft.moreshulkers.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.gudenau.minecraft.moreshulkers.MoreShulkers.MOD_ID;
import static net.gudenau.minecraft.moreshulkers.gui.CraftyShulkerWorldScreenHandler.PROPERTY_FOOD;
import static net.gudenau.minecraft.moreshulkers.gui.CraftyShulkerWorldScreenHandler.PROPERTY_PROGRESS;

public class CraftyShulkerWorldScreen extends HandledScreen<CraftyShulkerWorldScreenHandler>{
    private static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/gui/container/crafty_shulker_world.png");
    private final PropertyDelegate propertyDelegate;
    
    public CraftyShulkerWorldScreen(CraftyShulkerWorldScreenHandler handler, PlayerInventory inventory, Text title){
        super(handler, inventory, title);
        propertyDelegate = handler.propertyDelegate;
        height = 197;
        backgroundHeight = 197;
        playerInventoryTitleY = backgroundHeight - 94;
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
    
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        client.getTextureManager().bindTexture(TEXTURE);
        int x = (width - backgroundWidth) >> 1;
        int y = (height - backgroundHeight) >> 1;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        
        int progress = propertyDelegate.get(PROPERTY_PROGRESS);
        drawTexture(matrices, x + 89, y + 35, 0, 197, (int)(1 + (23 * (progress / 400f))), 16);
        
        float food = propertyDelegate.get(PROPERTY_FOOD) / 100F;
        drawTexture(matrices, x + 7, y + 96, 24, 197, (int)(162 * (Math.min(20, food) / 20)), 16);
    }
}
