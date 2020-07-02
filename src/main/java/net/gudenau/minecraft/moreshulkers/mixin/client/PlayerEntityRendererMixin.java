package net.gudenau.minecraft.moreshulkers.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.moreshulkers.renderer.ShulkerBoxFeatureRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"rawtypes", "unchecked"})
@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer{
    private PlayerEntityRendererMixin(){
        super(null, null, Float.NaN);
        throw new RuntimeException("Stop it, get some help.");
    }
    
    @Inject(
        method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V",
        at = @At("TAIL")
    )
    private void init(EntityRenderDispatcher dispatcher, boolean alex, CallbackInfo info){
        addFeature(new ShulkerBoxFeatureRenderer((PlayerEntityRenderer)(Object)this));
    }
}
