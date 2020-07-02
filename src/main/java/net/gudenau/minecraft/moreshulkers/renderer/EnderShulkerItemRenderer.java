package net.gudenau.minecraft.moreshulkers.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.gudenau.minecraft.moreshulkers.MoreShulkersClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class EnderShulkerItemRenderer implements BuiltinItemRenderer{
    private final ShulkerEntityModel<?> model = new ShulkerEntityModel<>();
    
    @Override
    public void render(ItemStack itemStack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        Item item = itemStack.getItem();
        if(item != MoreShulkers.Items.ENDER_SHULKER){
            return;
        }
        
        SpriteIdentifier textureId = MoreShulkersClient.Textures.ENDER_SHULKER;
        Direction direction = Direction.UP;
        matrices.push();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.scale(0.9995F, 0.9995F, 0.9995F);
        matrices.multiply(direction.getRotationQuaternion());
        matrices.scale(1.0F, -1.0F, -1.0F);
        matrices.translate(0.0D, -1.0D, 0.0D);
        VertexConsumer vertexConsumer = textureId.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutoutNoCull);
        model.getBottomShell().render(matrices, vertexConsumer, light, overlay);
        model.getTopShell().render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }
}
