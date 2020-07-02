package net.gudenau.minecraft.moreshulkers.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.gudenau.minecraft.moreshulkers.MoreShulkersClient;
import net.gudenau.minecraft.moreshulkers.block.entity.CraftyShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.gui.CraftyShulkerPlayerScreenHandler;
import net.gudenau.minecraft.moreshulkers.item.CustomShulkerItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.BlockView;

public class CraftyShulkerBlock extends AbstractShulkerBlock{
    public CraftyShulkerBlock(Settings settings){
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public SpriteIdentifier getTexture(ItemStack stack){
        return ((CustomShulkerItem<?>)stack.getItem()).hasColor(stack) ? MoreShulkersClient.Textures.CRAFTY_SHULKER_COLORED : MoreShulkersClient.Textures.CRAFTY_SHULKER;
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockView world){
        return new CraftyShulkerBlockEntity();
    }
    
    @Override
    public void openPlayerInventory(ItemStack stack, PlayerEntity player){
        player.openHandledScreen(new ExtendedScreenHandlerFactory(){
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf){}
    
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player){
                return new CraftyShulkerPlayerScreenHandler(syncId, inv, stack);
            }
            
            @Override
            public Text getDisplayName(){
                return stack.getName();
            }
        });
    }
}
