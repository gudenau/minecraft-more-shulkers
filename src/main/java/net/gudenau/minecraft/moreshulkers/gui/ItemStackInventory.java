package net.gudenau.minecraft.moreshulkers.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;

public class ItemStackInventory implements Inventory{
    private final ItemStack stack;
    private final DefaultedList<ItemStack> inventory;
    private final int size;
    private final CompoundTag tag;
    
    public ItemStackInventory(ItemStack stack, int expectedSize){
        this.stack = stack;
        tag = stack.getOrCreateSubTag("BlockEntityTag");
        int size = tag.getInt("Size");
        if(size == 0){
            size = expectedSize;
        }
        this.size = size;
        inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
        Inventories.fromTag(tag, inventory);
    }
    
    @Override
    public int size(){
        return size;
    }
    
    @Override
    public boolean isEmpty(){
        for(ItemStack stack : inventory){
            if(!stack.isEmpty()){
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getStack(int slot){
        return inventory.get(slot);
    }
    
    @Override
    public ItemStack removeStack(int slot, int amount){
        ItemStack itemStack = Inventories.splitStack(inventory, slot, amount);
        if(!itemStack.isEmpty()){
            markDirty(slot);
        }
        
        return itemStack;
    }
    
    @Override
    public ItemStack removeStack(int slot){
        try{
            return Inventories.removeStack(inventory, slot);
        }finally{
            markDirty(slot);
        }
    }
    
    @Override
    public void setStack(int slot, ItemStack stack){
        inventory.set(slot, stack);
        if(stack.getCount() > getMaxCountPerStack()){
            stack.setCount(getMaxCountPerStack());
        }
        
        markDirty(slot);
    }
    
    private void markDirty(int slot){
        //TODO
        markDirty();
    }
    
    @Override
    public void markDirty(){
        Inventories.toTag(tag, inventory);
        stack.putSubTag("BlockEntityTag", tag);
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player){
        return true;
    }
    
    @Override
    public void clear(){
        inventory.clear();
        markDirty();
    }
}
