package net.gudenau.minecraft.moreshulkers.mixin;

import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.gudenau.minecraft.moreshulkers.item.CustomShulkerItem;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity{
    @SuppressWarnings("ConstantConditions")
    private MobEntityMixin(){
        super(null, null);
        throw new RuntimeException("Stop it you silly goose!");
    }
    
    @Inject(
        method = "getPreferredEquipmentSlot",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void getPreferredEquipmentSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> info, Item item){
        if(!(item instanceof BlockItem)){
            return;
        }
        if(!(item instanceof CustomShulkerItem)){
            BlockItem blockItem = (BlockItem)item;
            if(!(blockItem.getBlock() instanceof ShulkerBoxBlock)){
                return;
            }
        }
    
        if(EnchantmentHelper.getLevel(MoreShulkers.Enchantments.WEARABLE, stack) > 0){
            info.setReturnValue(EquipmentSlot.CHEST);
        }
    }
}
