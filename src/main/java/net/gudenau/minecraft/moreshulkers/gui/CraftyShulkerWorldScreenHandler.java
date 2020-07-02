package net.gudenau.minecraft.moreshulkers.gui;

import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.gudenau.minecraft.moreshulkers.block.entity.CraftyShulkerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;

public class CraftyShulkerWorldScreenHandler extends ScreenHandler{
    public static final int PROPERTY_PROGRESS = 0;
    public static final int PROPERTY_FOOD = 1;
    
    private final CraftyShulkerBlockEntity inventory;
    final PropertyDelegate propertyDelegate;
    
    public CraftyShulkerWorldScreenHandler(int syncId, PlayerInventory playerInventory, CraftyShulkerBlockEntity inventory){
        super(MoreShulkers.Containers.CRAFTY_WORLD, syncId);
        this.inventory = inventory;
        propertyDelegate = new PropertyDelegate(){
            @Override
            public int get(int index){
                switch(index){
                    case PROPERTY_PROGRESS: return inventory.getCraftingProgress();
                    case PROPERTY_FOOD: return (int)(inventory.getFoodAmount() * 100);
                }
                return 0;
            }
    
            @Override
            public void set(int index, int value){
                switch(index){
                    case PROPERTY_PROGRESS:{
                        inventory.setCraftingProgress(value);
                    } break;
                    case PROPERTY_FOOD:{
                        inventory.setFoodAmount(value / 100F);
                    } break;
                }
            }
    
            @Override
            public int size(){
                return 2;
            }
        };
        
        this.inventory.onOpen(playerInventory.player);
    
        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 3; ++x) {
                addSlot(new ShulkerBoxSlot(inventory, x + y * 3, 30 + x * 18, 17 + y * 18));
            }
        }
        
        addSlot(new Slot(inventory, 9, 124, 35));
        addSlot(new FoodSlot(inventory, 10, 124, 71));
    
        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 115 + y * 18));
            }
        }
    
        for(int y = 0; y < 9; ++y) {
            addSlot(new Slot(playerInventory, y, 8 + y * 18, 173));
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
