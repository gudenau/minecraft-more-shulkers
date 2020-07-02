package net.gudenau.minecraft.moreshulkers.recipe;

import java.util.ArrayList;
import java.util.List;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.gudenau.minecraft.moreshulkers.item.CustomShulkerItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CustomShulkerRecipeColor extends SpecialCraftingRecipe{
    public CustomShulkerRecipeColor(Identifier id){
        super(id);
    }
    
    @Override
    public boolean matches(CraftingInventory inv, World world){
        int dyeCount = 0;
        int shulkerCount = 0;
        
        int size = inv.size();
        for(int i = 0; i < size; i++){
            ItemStack stack = inv.getStack(i);
            if(!stack.isEmpty()){
                Item item = stack.getItem();
                if(item instanceof DyeItem){
                    dyeCount++;
                }else if(item instanceof CustomShulkerItem){
                    if(shulkerCount >= 1){
                        return false;
                    }
                    shulkerCount++;
                }
            }
        }
        
        return dyeCount >= 1 && shulkerCount == 1;
    }
    
    @Override
    public ItemStack craft(CraftingInventory inv){
        List<DyeItem> dyes = new ArrayList<>();
        ItemStack shulker = ItemStack.EMPTY;
    
        int size = inv.size();
        for(int i = 0; i < size; i++){
            ItemStack stack = inv.getStack(i);
            if(!stack.isEmpty()){
                Item item = stack.getItem();
                if(item instanceof DyeItem){
                    dyes.add((DyeItem)item);
                }else if(item instanceof CustomShulkerItem){
                    if(!shulker.isEmpty()){
                        return ItemStack.EMPTY;
                    }
                    shulker = stack;
                }
            }
        }
    
        if(!dyes.isEmpty() && !shulker.isEmpty()){
            return DyeableItem.blendAndSetColor(shulker, dyes);
        }
        
        return shulker;
    }
    
    @Override
    public boolean fits(int width, int height){
        return width * height >= 2;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer(){
        return MoreShulkers.Recipes.CUSTOM_SHULKER_BOX_COLOR;
    }
}
