package net.gudenau.minecraft.moreshulkers.mixin;

import net.gudenau.minecraft.moreshulkers.block.entity.EnderShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.duck.EnderChestInventoryDuck;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderChestInventory.class)
public abstract class EnderChestInventoryMixin implements EnderChestInventoryDuck{
    private EnderShulkerBlockEntity gud_moreshulkers$inventory;
    @Shadow private EnderChestBlockEntity activeBlockEntity;
    
    @Override
    public void gud_moreshulkers$setActiveBlockEntity(EnderShulkerBlockEntity entity){
        gud_moreshulkers$inventory = entity;
        activeBlockEntity = null;
    }
    
    @Inject(
        method = "setActiveBlockEntity",
        at = @At("HEAD")
    )
    private void setActiveBlockEntity(EnderChestBlockEntity entity, CallbackInfo info){
        gud_moreshulkers$inventory = null;
    }
    
    @Inject(
        method = "canPlayerUse",
        at = @At("HEAD"),
        cancellable = true
    )
    public void canPlayerUse(PlayerEntity player, CallbackInfoReturnable<Boolean> info){
        if(gud_moreshulkers$inventory != null){
            info.setReturnValue(gud_moreshulkers$inventory.canPlayerUse(player));
        }
    }
    
    @Inject(
        method = "onOpen",
        at = @At("HEAD")
    )
    public void onOpen(PlayerEntity player, CallbackInfo info){
        if(gud_moreshulkers$inventory != null){
            gud_moreshulkers$inventory.onOpen(player);
        }
    }

    @Inject(
        method = "onClose",
        at = @At("HEAD")
    )
    public void onClose(PlayerEntity player, CallbackInfo info){
        if(gud_moreshulkers$inventory != null) {
            gud_moreshulkers$inventory.onClose(player);
        }
    
        gud_moreshulkers$inventory = null;
    }
}
