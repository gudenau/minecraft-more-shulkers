package net.gudenau.minecraft.moreshulkers.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.gudenau.minecraft.moreshulkers.block.entity.CustomShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.item.CustomShulkerItem;
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
public class CustomShulkerItemRenderer implements BuiltinItemRenderer{
    private final ShulkerEntityModel<?> model = new ShulkerEntityModel<>();
    
    private final SpriteIdentifier normal;
    private final SpriteIdentifier colored;
    
    public CustomShulkerItemRenderer(SpriteIdentifier normal, SpriteIdentifier colored){
        this.normal = normal;
        this.colored = colored;
    }
    
    @Override
    public void render(ItemStack itemStack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        Item item = itemStack.getItem();
        if(!(item instanceof CustomShulkerItem)){
            return;
        }
        
        SpriteIdentifier textureId;
        int color = ((CustomShulkerItem)item).getColor(itemStack);
        boolean colored = color != CustomShulkerBlockEntity.DEFAULT_COLOR;
    
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
    
        Direction direction = Direction.UP;
        matrices.push();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.scale(0.9995F, 0.9995F, 0.9995F);
        matrices.multiply(direction.getRotationQuaternion());
        matrices.scale(1.0F, -1.0F, -1.0F);
        matrices.translate(0.0D, -1.0D, 0.0D);
        VertexConsumer vertexConsumer = textureId.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutoutNoCull);
        if(colored){
            model.getBottomShell().render(matrices, vertexConsumer, light, overlay, red, green, blue, 1);
            model.getTopShell().render(matrices, vertexConsumer, light, overlay, red, green, blue, 1);
        }else{
            model.getBottomShell().render(matrices, vertexConsumer, light, overlay);
            model.getTopShell().render(matrices, vertexConsumer, light, overlay);
        }
        matrices.pop();
    }
}
