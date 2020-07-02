package net.gudenau.minecraft.moreshulkers.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.Box;

public interface CustomShulkerBlockEntity extends Nameable{
    int DEFAULT_COLOR = 0x876087;
    
    ShulkerBoxBlockEntity.AnimationStage getAnimationStage();
    Box getBoundingBox(BlockState state);
    float getAnimationProgress(float tickDelta);
    int getColor();
    void onOpen(PlayerEntity player);
    void onClose(PlayerEntity player);
    boolean isWearable();
    void setWearable(boolean wearable);
    void setCustomName(Text name);
    @Override
    default Text getName(){
        return hasCustomName() ? getCustomName() : getContainerName();
    }
    Text getContainerName();
    
    interface WithInventory extends CustomShulkerBlockEntity{
        CompoundTag serializeInventory(CompoundTag tag);
        boolean isEmpty();
    }
}
