package net.gudenau.minecraft.moreshulkers.block.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.stream.IntStream;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.util.NbtType;
import net.gudenau.minecraft.moreshulkers.block.ShulkerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public abstract class AbstractShulkerBlockEntity extends BlockEntity implements CustomShulkerBlockEntity, Tickable, BlockEntityClientSerializable{
    private int color = DEFAULT_COLOR;
    
    private float animationProgress;
    private float prevAnimationProgress;
    private ShulkerBoxBlockEntity.AnimationStage animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
    private int viewerCount = 0;
    private boolean wearable = false;
    private Text customName = null;
    
    public AbstractShulkerBlockEntity(BlockEntityType<?> type){
        super(type);
    }
    
    @Override
    public ShulkerBoxBlockEntity.AnimationStage getAnimationStage(){
        return animationStage;
    }
    
    @Override
    public float getAnimationProgress(float tickDelta){
        return MathHelper.lerp(tickDelta, prevAnimationProgress, animationProgress);
    }
    
    @Override
    public int getColor(){
        return color;
    }
    
    void setColor(int color){
        this.color = color;
    }
    
    @Override
    public void tick(){
        updateAnimation();
        if(animationStage == ShulkerBoxBlockEntity.AnimationStage.OPENING || animationStage == ShulkerBoxBlockEntity.AnimationStage.CLOSING){
            pushEntities();
        }
    }
    
    private void updateAnimation(){
        prevAnimationProgress = animationProgress;
        switch(animationStage){
            case CLOSED:{
                animationProgress = 0.0F;
            } break;
            case OPENING:{
                animationProgress += 0.1F;
                if(animationProgress >= 1.0F){
                    pushEntities();
                    animationStage = ShulkerBoxBlockEntity.AnimationStage.OPENED;
                    animationProgress = 1.0F;
                    updateNeighborStates();
                }
            } break;
            case CLOSING:{
                animationProgress -= 0.1F;
                if(animationProgress <= 0.0F){
                    animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
                    animationProgress = 0.0F;
                    updateNeighborStates();
                }
            } break;
            case OPENED:{
                animationProgress = 1.0F;
            } break;
        }
    }
    
    private void updateNeighborStates(){
        getCachedState().method_30101(getWorld(), getPos(), 3);
    }
    
    @Override
    public Box getBoundingBox(BlockState state) {
        return getBoundingBox(state.get(ShulkerBoxBlock.FACING));
    }
    
    public Box getBoundingBox(Direction openDirection) {
        float progress = getAnimationProgress(1.0F);
        return VoxelShapes.fullCube().getBoundingBox().stretch(
            0.5F * progress * openDirection.getOffsetX(),
            0.5F * progress * openDirection.getOffsetY(),
            0.5F * progress * openDirection.getOffsetZ()
        );
    }
    
    private Box getCollisionBox(Direction facing) {
        Direction direction = facing.getOpposite();
        return getBoundingBox(facing).shrink(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }
    
    private void pushEntities(){
        BlockState blockState = world.getBlockState(getPos());
        if(blockState.getBlock() instanceof ShulkerBlock){
            Direction direction = blockState.get(ShulkerBoxBlock.FACING);
            Box box = getCollisionBox(direction).offset(pos);
            List<Entity> list = world.getEntities(null, box);
            if(!list.isEmpty()){
                for(Entity value : list){
                    if(value.getPistonBehavior() != PistonBehavior.IGNORE){
                        double d = 0.0D;
                        double e = 0.0D;
                        double f = 0.0D;
                        Box box2 = value.getBoundingBox();
                        switch(direction.getAxis()){
                            case X:{
                                if(direction.getDirection() == Direction.AxisDirection.POSITIVE){
                                    d = box.maxX - box2.minX;
                                }else{
                                    d = box2.maxX - box.minX;
                                }
                                
                                d += 0.01D;
                            } break;
                            case Y:{
                                if(direction.getDirection() == Direction.AxisDirection.POSITIVE){
                                    e = box.maxY - box2.minY;
                                }else{
                                    e = box2.maxY - box.minY;
                                }
                                
                                e += 0.01D;
                            } break;
                            case Z:{
                                if(direction.getDirection() == Direction.AxisDirection.POSITIVE){
                                    f = box.maxZ - box2.minZ;
                                }else{
                                    f = box2.maxZ - box.minZ;
                                }
                                
                                f += 0.01D;
                            } break;
                        }
                        
                        value.move(
                            MovementType.SHULKER_BOX,
                            new Vec3d(d * direction.getOffsetX(), e * direction.getOffsetY(), f * direction.getOffsetZ())
                        );
                    }
                }
            }
        }
    }
    
    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if(type == 1){
            viewerCount = data;
            if(data == 0){
                animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSING;
                updateNeighborStates();
            }
            
            if(data == 1){
                animationStage = ShulkerBoxBlockEntity.AnimationStage.OPENING;
                updateNeighborStates();
            }
            
            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }
    }
    
    @Override
    public void onOpen(PlayerEntity player){
        if(!player.isSpectator()){
            if(viewerCount < 0){
                viewerCount = 0;
            }
            
            viewerCount++;
            world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 1, viewerCount);
            if(viewerCount == 1){
                world.playSound(null, pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            }
        }
        
    }
    
    @Override
    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            viewerCount--;
            world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 1, viewerCount);
            if(viewerCount <= 0){
                world.playSound(null, pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag){
        tag = super.toTag(tag);
        if(customName != null){
            tag.putString("CustomName", Text.Serializer.toJson(customName));
        }
        if(color != DEFAULT_COLOR){
            tag.putInt("Color", color);
        }
        return tag;
    }
    
    @Override
    public void fromTag(BlockState state, CompoundTag tag){
        super.fromTag(state, tag);
        if(tag.contains("CustomName", NbtType.STRING)) {
            customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        if(tag.contains("Color", NbtType.INT)){
            color = tag.getInt("Color");
        }
    }
    
    @Override
    public void fromClientTag(CompoundTag compoundTag){
        color = compoundTag.contains("Color", NbtType.INT) ? compoundTag.getInt("Color") : DEFAULT_COLOR;
    }
    
    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag){
        if(color != DEFAULT_COLOR){
            compoundTag.putInt("Color", color);
        }
        return compoundTag;
    }
    
    @Override
    public boolean isWearable(){
        return wearable;
    }
    
    @Override
    public void setWearable(boolean wearable){
        if(this.wearable != wearable){
            this.wearable = wearable;
            markDirty();
        }
    }
    
    @Override
    public void setCustomName(Text customName){
        this.customName = customName;
    }
    
    @Override
    public Text getCustomName(){
        return customName;
    }
    
    public static abstract class WithInventory extends AbstractShulkerBlockEntity implements SidedInventory, CustomShulkerBlockEntity.WithInventory{
        private static final Int2ObjectMap<int[]> AVAILABLE_SLOTS = new Int2ObjectOpenHashMap<>();
        
        private final DefaultedList<ItemStack> inventory;
        private final int[] availableSlots;
    
        public WithInventory(int size, BlockEntityType<?> type){
            super(type);
            inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
            availableSlots = AVAILABLE_SLOTS.computeIfAbsent(size, (s)->IntStream.range(0, s).toArray());
        }
        
        @Override
        public CompoundTag toTag(CompoundTag tag){
            return serializeInventory(super.toTag(tag));
        }
    
        public CompoundTag serializeInventory(CompoundTag tag){
            Inventories.toTag(tag, inventory, false);
            tag.putInt("Size", 6*9);
            int color = getColor();
            if(color != DEFAULT_COLOR){
                tag.putInt("Color", color);
            }
            return tag;
        }
    
        @Override
        public void fromTag(BlockState state, CompoundTag tag){
            super.fromTag(state, tag);
            deserializeInventory(tag);
        }
    
        protected void deserializeInventory(CompoundTag tag){
            inventory.clear();
            if(tag.contains("Items", NbtType.LIST)) {
                Inventories.fromTag(tag, inventory);
            }
            int color;
            if(tag.contains("Color", NbtType.INT)){
                color = tag.getInt("Color");
            }else{
                color = DEFAULT_COLOR;
            }
            setColor(color);
        }
    
        @Override
        public int size(){
            return inventory.size();
        }
    
        @Override
        public boolean isEmpty(){
            for(ItemStack stack : inventory){
                if(!stack.isEmpty()){
                    return false;
                }
            }
            return true;
        }
    
        @Override
        public ItemStack getStack(int slot){
            return inventory.get(slot);
        }
    
        @Override
        public ItemStack removeStack(int slot, int amount){
            ItemStack itemStack = Inventories.splitStack(inventory, slot, amount);
            if(!itemStack.isEmpty()){
                markDirty();
            }
        
            return itemStack;
        }
    
        @Override
        public ItemStack removeStack(int slot){
            return Inventories.removeStack(inventory, slot);
        }
    
        @Override
        public void setStack(int slot, ItemStack stack){
            inventory.set(slot, stack);
            if(stack.getCount() > getMaxCountPerStack()){
                stack.setCount(getMaxCountPerStack());
            }
        
            markDirty();
        }
    
        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean canPlayerUse(PlayerEntity player){
            if(world.getBlockEntity(pos) != this){
                return false;
            }else{
                return player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
            }
        }
    
        @Override
        public void clear(){
            inventory.clear();
        }
    
        @Override
        public int[] getAvailableSlots(Direction side){
            return availableSlots;
        }
    
        @Override
        public boolean isValid(int slot, ItemStack stack){
            Block block = Block.getBlockFromItem(stack.getItem());
            return !(block instanceof ShulkerBlock || block instanceof ShulkerBoxBlock);
        }
    
        @Override
        public boolean canInsert(int slot, ItemStack stack, Direction dir){
            return isValid(slot, stack);
        }
    
        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction dir){
            return true;
        }
        
        protected DefaultedList<ItemStack> getInventory(){
            return inventory;
        }
    }
}
