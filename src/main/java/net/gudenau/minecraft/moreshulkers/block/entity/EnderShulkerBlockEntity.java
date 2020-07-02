package net.gudenau.minecraft.moreshulkers.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.gudenau.minecraft.moreshulkers.duck.EnderChestInventoryDuck;
import net.gudenau.minecraft.moreshulkers.gui.EnderShulkerWorldScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class EnderShulkerBlockEntity extends AbstractShulkerBlockEntity implements NamedScreenHandlerFactory, ExtendedScreenHandlerFactory{
    public EnderShulkerBlockEntity(){
        super(MoreShulkers.BlockEntities.ENDER_SHULKER);
    }
    
    @Override
    public Text getContainerName(){
        return new TranslatableText("container.gud_moreshulkers.enderShulker");
    }
    
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player){
        EnderChestInventory enderInventory = player.getEnderChestInventory();
        ((EnderChestInventoryDuck)enderInventory).gud_moreshulkers$setActiveBlockEntity(this);
        return new EnderShulkerWorldScreenHandler(syncId, inv, enderInventory);
    }
    
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf){
        packetByteBuf.writeBlockPos(pos);
    }
    
    @Override
    public Text getDisplayName(){
        return getName();
    }
    
    public boolean canPlayerUse(PlayerEntity playerEntity){
        if(world.getBlockEntity(pos) != this) {
            return false;
        }else{
            return playerEntity.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
        }
    }
}
