package net.gudenau.minecraft.moreshulkers.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.gudenau.minecraft.moreshulkers.gui.LargeShulkerWorldScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class LargeShulkerBlockEntity extends AbstractShulkerBlockEntity.WithInventory implements NamedScreenHandlerFactory, ExtendedScreenHandlerFactory{
    public LargeShulkerBlockEntity(){
        super(6 * 9, MoreShulkers.BlockEntities.LARGE_SHULKER);
    }
    
    @Override
    public Text getContainerName(){
        return new TranslatableText("container.gud_moreshulkers.largeShulker");
    }
    
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player){
        return new LargeShulkerWorldScreenHandler(syncId, inv, this);
    }
    
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf){
        packetByteBuf.writeBlockPos(pos);
    }
    
    @Override
    public Text getDisplayName(){
        return getName();
    }
}
