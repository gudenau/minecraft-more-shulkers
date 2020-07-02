package net.gudenau.minecraft.moreshulkers.item;

import java.util.Collections;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.gudenau.minecraft.moreshulkers.block.AbstractShulkerBlock;
import net.gudenau.minecraft.moreshulkers.block.ShulkerBlock;
import net.gudenau.minecraft.moreshulkers.block.entity.LargeShulkerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CustomShulkerItem<T extends Block & ShulkerBlock> extends BlockItem implements DyeableItem{
    public CustomShulkerItem(T block, Settings settings){
        super(block, settings);
    }
    
    @Override
    public boolean hasColor(ItemStack stack){
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        return compoundTag != null && compoundTag.contains("Color", NbtType.INT);
    }
    
    @Override
    public int getColor(ItemStack stack){
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        return compoundTag != null && compoundTag.contains("Color", NbtType.INT) ? compoundTag.getInt("Color") : LargeShulkerBlockEntity.DEFAULT_COLOR;
    }
    
    @Override
    public void removeColor(ItemStack stack){
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if(compoundTag != null && compoundTag.contains("Color")){
            compoundTag.remove("Color");
        }
    }
    
    @Override
    public void setColor(ItemStack stack, int color){
        stack.getOrCreateSubTag("BlockEntityTag").putInt("Color", color);
    }
    
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks){
        if(isIn(group)){
            ItemStack stack = new ItemStack(this);
            stacks.add(stack.copy());
            for(DyeColor dyeColor : DyeColor.values()){
                stacks.add(DyeableItem.blendAndSetColor(stack, Collections.singletonList(DyeItem.byColor(dyeColor))));
            }
        }
    }
    
    //FIXME
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        CompoundTag tag = stack.getOrCreateSubTag("BlockEntityTag");
        if(tag.contains("Wearable", NbtType.BYTE)){
            if(tag.getBoolean("Wearable")){
                stack.addEnchantment(MoreShulkers.Enchantments.WEARABLE, 1);
            }
            tag.remove("Wearable");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Environment(EnvType.CLIENT)
    public SpriteIdentifier getTexture(ItemStack stack){
        return ((T)getBlock()).getTexture(stack);
    }
    
    public void openInventory(ItemStack stack, PlayerEntity player){
        Block block = getBlock();
        if(block instanceof AbstractShulkerBlock){
            ((AbstractShulkerBlock)block).openPlayerInventory(stack, player);
        }
    }
}
