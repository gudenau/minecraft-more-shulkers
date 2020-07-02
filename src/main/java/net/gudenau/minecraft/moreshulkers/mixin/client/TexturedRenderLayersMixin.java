package net.gudenau.minecraft.moreshulkers.mixin.client;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.moreshulkers.MoreShulkersClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(TexturedRenderLayers.class)
public abstract class TexturedRenderLayersMixin{
    @Inject(
        method = "addDefaultTextures",
        at = @At("TAIL")
    )
    private static void addDefaultTextures(Consumer<SpriteIdentifier> adder, CallbackInfo info){
        MoreShulkersClient.Textures.addTextures(adder);
    }
}
