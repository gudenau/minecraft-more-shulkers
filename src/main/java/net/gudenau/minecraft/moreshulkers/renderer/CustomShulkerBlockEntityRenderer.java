package net.gudenau.minecraft.moreshulkers.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.moreshulkers.block.ShulkerBlock;
import net.gudenau.minecraft.moreshulkers.block.entity.CustomShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.block.entity.LargeShulkerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
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
public class CustomShulkerBlockEntityRenderer<T extends BlockEntity & CustomShulkerBlockEntity> extends BlockEntityRenderer<T>{
    private final ShulkerEntityModel<?> model = new ShulkerEntityModel<>();
    private final SpriteIdentifier normal;
    private final SpriteIdentifier colored;
    
    public CustomShulkerBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher, SpriteIdentifier normal, SpriteIdentifier colored){
        super(dispatcher);
        this.normal = normal;
        this.colored = colored;
    }
    
    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        Direction direction = Direction.UP;
        if(entity.hasWorld()){
            BlockState blockState = entity.getWorld().getBlockState(entity.getPos());
            if(blockState.getBlock() instanceof ShulkerBlock){
                direction = blockState.get(ShulkerBoxBlock.FACING);
            }
        }
    
        SpriteIdentifier textureId;
        int color = entity.getColor();
        boolean colored = color != LargeShulkerBlockEntity.DEFAULT_COLOR;
        
        float red = 0;
        float green = 0;
        float blue = 0;
        
        if(colored){
            textureId = this.colored;
            
            red = ((color >> 16) & 0xFF) * 0.00390625F;
            green = ((color >> 8) & 0xFF) * 0.00390625F;
            blue = (color & 0xFF) * 0.00390625F;
        }else{
            textureId = normal;
        }
        
        matrices.push();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.scale(0.9995F, 0.9995F, 0.9995F);
        matrices.multiply(direction.getRotationQuaternion());
        matrices.scale(1.0F, -1.0F, -1.0F);
        matrices.translate(0.0D, -1.0D, 0.0D);
        VertexConsumer vertexConsumer = textureId.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutoutNoCull);
        if(colored){
            model.getBottomShell().render(matrices, vertexConsumer, light, overlay, red, green, blue, 1);
        }else{
            model.getBottomShell().render(matrices, vertexConsumer, light, overlay);
        }
        matrices.translate(0.0D, -entity.getAnimationProgress(tickDelta) * 0.5F, 0.0D);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F * entity.getAnimationProgress(tickDelta)));
        if(colored){
            model.getTopShell().render(matrices, vertexConsumer, light, overlay, red, green, blue, 1);
        }else{
            model.getTopShell().render(matrices, vertexConsumer, light, overlay);
        }
        matrices.pop();
    }
}
