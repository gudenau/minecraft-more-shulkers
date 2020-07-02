package net.gudenau.minecraft.moreshulkers.mixin;

import net.gudenau.minecraft.moreshulkers.block.ShulkerBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxSlot.class)
public abstract class ShulkerBoxSlotMixin extends Slot{
    private ShulkerBoxSlotMixin(){
        super(null, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        throw new RuntimeException("Stop. Get some help.");
    }
    
    @Inject(
        method = "canInsert",
        at = @At("HEAD"),
        cancellable = true
    )
    private void canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> info){
        if(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBlock){
            info.setReturnValue(false);
        }
    }
}
