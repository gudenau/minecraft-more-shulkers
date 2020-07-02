package net.gudenau.minecraft.moreshulkers.mixin.client;

import net.gudenau.minecraft.moreshulkers.MoreShulkersClient;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin{
    @Inject(
        method = "create",
        at = @At("TAIL"),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void create(CallbackInfoReturnable<BlockColors> info, BlockColors colors){
        MoreShulkersClient.Blocks.initClient(colors);
    }
}
