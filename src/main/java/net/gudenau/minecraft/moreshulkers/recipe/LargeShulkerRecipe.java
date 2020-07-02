package net.gudenau.minecraft.moreshulkers.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class LargeShulkerRecipe extends SpecialCraftingRecipe{
    public LargeShulkerRecipe(Identifier id){
        super(id);
    }
    
    @Override
    public boolean matches(CraftingInventory inv, World world){
        int shellCount = 0;
        Set<ItemStack> shulkers = new HashSet<>();
        
        int size = inv.size();
        for(int i = 0; i < size; i++){
            ItemStack stack = inv.getStack(i);
            if(stack.isEmpty()){
                continue;
            }
            if(stack.getItem() == Items.SHULKER_SHELL){
                if(shellCount >= 2){
                    return false;
                }
                shellCount++;
            }else if(checkShulker(stack)){
                if(shulkers.size() >= 2){
                    return false;
                }
                shulkers.add(stack);
            }
        }
        
        return shellCount == 2 && shulkers.size() == 2;
    }
    
    private boolean checkShulker(ItemStack stack){
        Item item = stack.getItem();
        if(item instanceof BlockItem){
            return ((BlockItem)item).getBlock() instanceof ShulkerBoxBlock;
        }
        return false;
    }
    
    @Override
    public ItemStack craft(CraftingInventory inv){
        int shellCount = 0;
        List<ItemStack> shulkers = new ArrayList<>();
    
        int size = inv.size();
        for(int i = 0; i < size; i++){
            ItemStack stack = inv.getStack(i);
            if(stack.isEmpty()){
                continue;
            }
            if(stack.getItem() == Items.SHULKER_SHELL){
                if(shellCount >= 2){
                    return ItemStack.EMPTY;
                }
                shellCount++;
            }else if(checkShulker(stack)){
                if(shulkers.size() >= 2){
                    return ItemStack.EMPTY;
                }
                shulkers.add(stack);
            }
        }
        
        if(shellCount != 2 || shulkers.size() != 2){
            return ItemStack.EMPTY;
        }
        
        ItemStack result = new ItemStack(MoreShulkers.Items.LARGE_SHULKER);
        DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6 * 9, ItemStack.EMPTY);
        copy(inventory, shulkers.get(0), 0);
        copy(inventory, shulkers.get(1), 3 * 9);
        boolean notEmpty = false;
        for(ItemStack stack : inventory){
            if(!stack.isEmpty()){
                notEmpty = true;
                break;
            }
        }
        if(notEmpty){
            CompoundTag tag = new CompoundTag();
            Inventories.toTag(tag, inventory, false);
            if(!tag.isEmpty()){
                result.putSubTag("BlockEntityTag", tag);
            }
        }
        return result;
    }
    
    private void copy(DefaultedList<ItemStack> inventory, ItemStack source, int off){
        if(!source.hasTag()){
            return;
        }
    
        DefaultedList<ItemStack> sourceInv = DefaultedList.ofSize(3 * 9, ItemStack.EMPTY);
        Inventories.fromTag(source.getOrCreateSubTag("BlockEntityTag"), sourceInv);
        for(int i = 0; i < 3 * 9; i++){
            inventory.set(i + off, sourceInv.get(i));
        }
    }
    
    @Override
    public boolean fits(int width, int height){
        return width * height >= 4;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer(){
        return MoreShulkers.Recipes.LARGE_SHULKER_BOX;
    }
}
