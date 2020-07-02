package net.gudenau.minecraft.moreshulkers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.gudenau.minecraft.moreshulkers.gui.*;
import net.gudenau.minecraft.moreshulkers.renderer.CustomShulkerBlockEntityRenderer;
import net.gudenau.minecraft.moreshulkers.renderer.CustomShulkerItemRenderer;
import net.gudenau.minecraft.moreshulkers.renderer.EnderShulkerBlockEntityRenderer;
import net.gudenau.minecraft.moreshulkers.renderer.EnderShulkerItemRenderer;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static net.gudenau.minecraft.moreshulkers.MoreShulkers.MOD_ID;
import static net.minecraft.client.render.TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE;

@Environment(EnvType.CLIENT)
public class MoreShulkersClient implements ClientModInitializer{
    @Override
    public void onInitializeClient(){
        BlockEntities.initClient();
        Containers.initClient();
        Items.initClient();
    }

    @Environment(EnvType.CLIENT)
    public static final class Textures{
        public static final SpriteIdentifier LARGE_SHULKER = new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier(MOD_ID, "entity/shulker/large_shulker"));
        public static final SpriteIdentifier LARGE_SHULKER_COLORED = new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier(MOD_ID, "entity/shulker/large_shulker_colored"));
        public static final SpriteIdentifier ENDER_SHULKER = new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier(MOD_ID, "entity/shulker/ender_shulker"));
        public static final SpriteIdentifier CRAFTY_SHULKER = new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier(MOD_ID, "entity/shulker/crafty_shulker"));
        public static final SpriteIdentifier CRAFTY_SHULKER_COLORED = new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier(MOD_ID, "entity/shulker/crafty_shulker_colored"));

        public static void addTextures(Consumer<SpriteIdentifier> adder){
            adder.accept(LARGE_SHULKER);
            adder.accept(LARGE_SHULKER_COLORED);
            adder.accept(ENDER_SHULKER);
            adder.accept(CRAFTY_SHULKER);
            adder.accept(CRAFTY_SHULKER_COLORED);
        }
    }

    public static class Blocks{
        @Environment(EnvType.CLIENT)
        public static void initClient(BlockColors colors){
            colors.registerColorProvider(
                (state, world, pos, tintIndex)->
                    ((BlockColorProvider)state.getBlock()).getColor(state, world, pos, tintIndex),
                MoreShulkers.Blocks.LARGE_SHULKER
            );
        }
    }

    private static class BlockEntities{
        @Environment(EnvType.CLIENT)
        public static void initClient(){
            BlockEntityRendererRegistry blockEntityRegistry = BlockEntityRendererRegistry.INSTANCE;
            blockEntityRegistry.register(
                MoreShulkers.BlockEntities.LARGE_SHULKER,
                (dispatcher)->
                    new CustomShulkerBlockEntityRenderer<>(
                        dispatcher,
                        Textures.LARGE_SHULKER,
                        Textures.LARGE_SHULKER_COLORED
                    )
            );
            blockEntityRegistry.register(MoreShulkers.BlockEntities.ENDER_SHULKER, EnderShulkerBlockEntityRenderer::new);
            blockEntityRegistry.register(
                MoreShulkers.BlockEntities.CRAFTY_SHULKER,
                (dispatcher)->
                    new CustomShulkerBlockEntityRenderer<>(
                        dispatcher,
                        Textures.CRAFTY_SHULKER,
                        Textures.CRAFTY_SHULKER_COLORED
                    )
            );
        }

    }

    private static class Containers{
        @Environment(EnvType.CLIENT)
        private static void initClient(){
            /*TODO
            ScreenRegistry.register(MoreShulkers.Containers.SMALL_PLAYER, PlayerScreen<SmallShulkerPlayerDescription>::new);
             */
    
            ScreenRegistry.register(MoreShulkers.Containers.LARGE_PLAYER, LargeShulkerPlayerScreen::new);
            ScreenRegistry.register(MoreShulkers.Containers.ENDER_PLAYER, EnderShulkerPlayerScreen::new);
            ScreenRegistry.register(MoreShulkers.Containers.CRAFTY_PLAYER, CraftyShulkerPlayerScreen::new);
            
            ScreenRegistry.register(MoreShulkers.Containers.LARGE_WORLD, LargeShulkerWorldScreen::new);
            ScreenRegistry.register(MoreShulkers.Containers.ENDER_WORLD, EnderShulkerWorldScreen::new);
            ScreenRegistry.register(MoreShulkers.Containers.CRAFTY_WORLD, CraftyShulkerWorldScreen::new);
        }

    }

    private static class Items{
        @Environment(EnvType.CLIENT)
        public static void initClient(){
            BuiltinItemRendererRegistry itemRegistry = BuiltinItemRendererRegistry.INSTANCE;
            itemRegistry.register(MoreShulkers.Items.LARGE_SHULKER, new CustomShulkerItemRenderer(Textures.LARGE_SHULKER, Textures.LARGE_SHULKER_COLORED));
            itemRegistry.register(MoreShulkers.Items.ENDER_SHULKER, new EnderShulkerItemRenderer());
            itemRegistry.register(MoreShulkers.Items.CRAFTY_SHULKER, new CustomShulkerItemRenderer(Textures.CRAFTY_SHULKER, Textures.CRAFTY_SHULKER_COLORED));
        }
    }
}
