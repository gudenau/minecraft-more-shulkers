package net.gudenau.minecraft.moreshulkers.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.moreshulkers.MoreShulkersClient;
import net.gudenau.minecraft.moreshulkers.block.ShulkerBlock;
import net.gudenau.minecraft.moreshulkers.block.entity.EnderShulkerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class EnderShulkerBlockEntityRenderer extends BlockEntityRenderer<EnderShulkerBlockEntity>{
    private final ShulkerEntityModel<?> model = new ShulkerEntityModel<>();
    
    public EnderShulkerBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher){
        super(dispatcher);
    }
    
    @Override
    public void render(EnderShulkerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        Direction direction = Direction.UP;
        if(entity.hasWorld()){
            BlockState blockState = entity.getWorld().getBlockState(entity.getPos());
            if(blockState.getBlock() instanceof ShulkerBlock){
                direction = blockState.get(ShulkerBoxBlock.FACING);
            }
        }
    
        SpriteIdentifier textureId = MoreShulkersClient.Textures.ENDER_SHULKER;
        
        matrices.push();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.scale(0.9995F, 0.9995F, 0.9995F);
        matrices.multiply(direction.getRotationQuaternion());
        matrices.scale(1.0F, -1.0F, -1.0F);
        matrices.translate(0.0D, -1.0D, 0.0D);
        VertexConsumer vertexConsumer = textureId.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutoutNoCull);
        model.getBottomShell().render(matrices, vertexConsumer, light, overlay);
        matrices.translate(0.0D, -entity.getAnimationProgress(tickDelta) * 0.5F, 0.0D);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F * entity.getAnimationProgress(tickDelta)));
        model.getTopShell().render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }
}
