package net.gudenau.minecraft.moreshulkers.mixin.client;

import java.util.HashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.moreshulkers.gui.FoodSlot;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin{
    @Inject(
        method = "method_24150",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/TexturedRenderLayers;addDefaultTextures(Ljava/util/function/Consumer;)V"
        )
    )
    private static void addTextures(HashSet<SpriteIdentifier> textures, CallbackInfo info){
        textures.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, FoodSlot.EMPTY_SLOT_FOOD));
    }
}
