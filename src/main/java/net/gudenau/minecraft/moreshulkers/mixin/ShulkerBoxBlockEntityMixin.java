package net.gudenau.minecraft.moreshulkers.mixin;

import net.gudenau.minecraft.moreshulkers.block.ShulkerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin{
    @Inject(
        method = "canInsert",
        at = @At("HEAD"),
        cancellable = true
    )
    private void canInsert(int slot, ItemStack stack, Direction dir, CallbackInfoReturnable<Boolean> info){
        if(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBlock){
            info.setReturnValue(false);
        }
    }
}
