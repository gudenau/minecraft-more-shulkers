package net.gudenau.minecraft.moreshulkers.enchantment;

import net.gudenau.minecraft.moreshulkers.item.CustomShulkerItem;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WearableEnchantment extends Enchantment{
    public WearableEnchantment(Rarity weight, EquipmentSlot... slotTypes){
        super(weight, EnchantmentTarget.BREAKABLE, slotTypes);
    }
    
    public int getMinPower(int level) {
        return level * 25;
    }
    
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 50;
    }
    
    public boolean isTreasure() {
        return true;
    }
    
    public int getMaxLevel() {
        return 1;
    }
    
    @Override
    public boolean isAcceptableItem(ItemStack stack){
        Item item = stack.getItem();
        if(item instanceof CustomShulkerItem){
            return true;
        }else if(item instanceof BlockItem){
            //return ((BlockItem)item).getBlock() instanceof ShulkerBoxBlock;
            return false;
        }
        return false;
    }
}
