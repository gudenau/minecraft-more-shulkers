package net.gudenau.minecraft.moreshulkers.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.gudenau.minecraft.moreshulkers.MoreShulkers.MOD_ID;

public class LargeShulkerPlayerScreen extends HandledScreen<LargeShulkerPlayerScreenHandler>{
    private static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/gui/container/large_shulker.png");
    private final PlayerEntity player;
    
    public LargeShulkerPlayerScreen(LargeShulkerPlayerScreenHandler handler, PlayerInventory inventory, Text title){
        super(handler, inventory, title);
        this.player = inventory.player;
        backgroundHeight = 222;
        backgroundWidth = 200;
        playerInventoryTitleY = 128;
    }
    
    @Override
    public void removed(){}
    
    @Override
    public void tick(){
        ItemStack stack = player.inventory.armor.get(2);
        if(stack.isEmpty() || stack.getItem() != MoreShulkers.Items.LARGE_SHULKER){
            MinecraftClient.getInstance().openScreen(new InventoryScreen(player));
            ClientSidePacketRegistry.INSTANCE.sendToServer(MoreShulkers.Packets.CLOSE_GUI, new PacketByteBuf(Unpooled.buffer(0)));
        }
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
    }
}
