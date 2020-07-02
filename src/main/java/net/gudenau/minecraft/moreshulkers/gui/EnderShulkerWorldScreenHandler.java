package net.gudenau.minecraft.moreshulkers.gui;

import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;

public class EnderShulkerWorldScreenHandler extends ScreenHandler{
    private final Inventory inventory;
    
    public EnderShulkerWorldScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory){
        super(MoreShulkers.Containers.ENDER_WORLD, syncId);
        this.inventory = inventory;
        
        this.inventory.onOpen(playerInventory.player);
    
        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new ShulkerBoxSlot(inventory, x + y * 3, 8 + x * 18, 18 + y * 18));
            }
        }
    
        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 85 + y * 18));
            }
        }
    
        for(int y = 0; y < 9; ++y) {
            addSlot(new Slot(playerInventory, y, 8 + y * 18, 143));
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player){
        return inventory.canPlayerUse(player);
    }
    
    @Override
    public void close(PlayerEntity player){
        super.close(player);
        inventory.onClose(player);
    }
    
    @Override
    public ItemStack transferSlot(PlayerEntity player, int index){
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if(slot != null && slot.hasStack()){
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if(index < 6 * 9){
                if(!insertItem(itemStack2, 6 * 9, slots.size(), true)){
                    return ItemStack.EMPTY;
                }
            }else if(!insertItem(itemStack2, 0, 6 * 9, false)){
                return ItemStack.EMPTY;
            }
            
            if(itemStack2.isEmpty()){
                slot.setStack(ItemStack.EMPTY);
            }else{
                slot.markDirty();
            }
        }
        
        return itemStack;
    }
}
