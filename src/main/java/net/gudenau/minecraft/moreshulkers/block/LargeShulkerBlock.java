package net.gudenau.minecraft.moreshulkers.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.gudenau.minecraft.moreshulkers.MoreShulkersClient;
import net.gudenau.minecraft.moreshulkers.block.entity.LargeShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.gui.LargeShulkerPlayerScreenHandler;
import net.gudenau.minecraft.moreshulkers.item.CustomShulkerItem;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.BlockView;

public class LargeShulkerBlock extends AbstractShulkerBlock implements BlockEntityProvider, ShulkerBlock{
    public LargeShulkerBlock(Settings settings){
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockView world){
        return new LargeShulkerBlockEntity();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public SpriteIdentifier getTexture(ItemStack stack){
        return ((CustomShulkerItem<?>)stack.getItem()).hasColor(stack) ? MoreShulkersClient.Textures.LARGE_SHULKER_COLORED : MoreShulkersClient.Textures.LARGE_SHULKER;
    }
    
    @Override
    public void openPlayerInventory(ItemStack stack, PlayerEntity player){
        player.openHandledScreen(new ExtendedScreenHandlerFactory(){
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf){}
    
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player){
                return new LargeShulkerPlayerScreenHandler(syncId, inv, stack);
            }
    
            @Override
            public Text getDisplayName(){
                return stack.getName();
            }
        });
    }
}
