package net.gudenau.minecraft.moreshulkers.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.moreshulkers.block.entity.CustomShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.item.CustomShulkerItem;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

@Environment(EnvType.CLIENT)
public class ShulkerBoxFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M>{
    private final ShulkerEntityModel<?> model = new ShulkerEntityModel<>();
    
    public ShulkerBoxFeatureRenderer(FeatureRendererContext<T, M> context){
        super(context);
    }
    
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch){
        ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.CHEST);
        
        SpriteIdentifier textureId = null;
        
        Item item = itemStack.getItem();
        if(item instanceof CustomShulkerItem){
            textureId = ((CustomShulkerItem<?>)item).getTexture(itemStack);
        }else if(item instanceof BlockItem){
            Block rawBlock = ((BlockItem)item).getBlock();
            if(rawBlock instanceof ShulkerBoxBlock){
                DyeColor dyeColor = ((ShulkerBoxBlock)rawBlock).getColor();
                if(dyeColor == null){
                    textureId = TexturedRenderLayers.SHULKER_TEXTURE_ID;
                }else{
                    textureId = TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(dyeColor.getId());
                }
            }
        }
        
        if(textureId != null){
            int color = 0;
            boolean colored = false;
            
            if(item instanceof CustomShulkerItem){
                color = ((CustomShulkerItem)item).getColor(itemStack);
                if(color != CustomShulkerBlockEntity.DEFAULT_COLOR){
                    colored = true;
                }
            }
            
            matrices.push();
            try{
                matrices.scale(0.49F, 0.49F, 0.49F);
    
                BipedEntityModel model = (BipedEntityModel)getContextModel();
                ModelPart torso = model.torso;
    
                torso.rotate(matrices);
    
                matrices.translate(0, 0.6, 1+2/3.0);
                // -90 Deg
                matrices.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(-1.5707964f));
                
                VertexConsumer vertexConsumer = textureId.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutoutNoCull);
                int overlay = LivingEntityRenderer.getOverlay(entity, 0.0F);
                if(colored){
                    float red = ((color >>> 16) & 0xFF) * 0.00390625f;
                    float green = ((color >>> 8) & 0xFF) * 0.00390625f;
                    float blue = (color & 0xFF) * 0.00390625f;
                    this.model.getBottomShell().render(matrices, vertexConsumer, light, overlay, red, green, blue, 1);
                    this.model.getTopShell().render(matrices, vertexConsumer, light, overlay, red, green, blue, 1);
                }else{
                    this.model.getBottomShell().render(matrices, vertexConsumer, light, overlay);
                    this.model.getTopShell().render(matrices, vertexConsumer, light, overlay);
                }
            }finally{
                matrices.pop();
            }
        }
    }
}
