package net.gudenau.minecraft.moreshulkers.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.moreshulkers.renderer.ShulkerBoxFeatureRenderer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"rawtypes", "unchecked"})
@Environment(EnvType.CLIENT)
@Mixin(BipedEntityRenderer.class)
public abstract class BipedEntityRendererMixin extends MobEntityRenderer{
    private BipedEntityRendererMixin(){
        super(null, null, Float.NaN);
        throw new RuntimeException("Stop it, get some help.");
    }
    
    @Inject(
        method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Lnet/minecraft/client/render/entity/model/BipedEntityModel;FFFF)V",
        at = @At("TAIL")
    )
    private void init(EntityRenderDispatcher dispatcher, BipedEntityModel model, float f, float g, float h, float i, CallbackInfo info){
        addFeature(new ShulkerBoxFeatureRenderer((BipedEntityRenderer)(Object)this));
    }
}
