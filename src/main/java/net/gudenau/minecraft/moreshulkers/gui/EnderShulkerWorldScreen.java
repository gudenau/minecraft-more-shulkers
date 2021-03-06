package net.gudenau.minecraft.moreshulkers.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EnderShulkerWorldScreen extends HandledScreen<EnderShulkerWorldScreenHandler>{
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    
    public EnderShulkerWorldScreen(EnderShulkerWorldScreenHandler handler, PlayerInventory inventory, Text title){
        super(handler, inventory, title);
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
        this.drawTexture(matrices, x, y, 0, 0, backgroundWidth, 71);
        this.drawTexture(matrices, x, y + 3 * 18 + 17, 0, 126, backgroundWidth, 96);
    }
}
