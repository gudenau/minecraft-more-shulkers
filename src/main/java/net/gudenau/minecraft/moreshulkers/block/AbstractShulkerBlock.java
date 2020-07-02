package net.gudenau.minecraft.moreshulkers.block;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.fabric.api.util.NbtType;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.gudenau.minecraft.moreshulkers.block.entity.CustomShulkerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ShulkerLidCollisions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockColorProvider.class)
public abstract class AbstractShulkerBlock extends Block implements BlockEntityProvider, ShulkerBlock, BlockColorProvider{
    private static final EnumProperty<Direction> FACING = FacingBlock.FACING;
    
    public AbstractShulkerBlock(Settings settings){
        super(settings);
    
        setDefaultState(getDefaultState().with(FACING, Direction.UP));
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)blockEntity : null;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if(world.isClient){
            return ActionResult.SUCCESS;
        }else if(player.isSpectator()){
            return ActionResult.CONSUME;
        }else{
            BlockEntity rawEntity = world.getBlockEntity(pos);
            if(rawEntity instanceof CustomShulkerBlockEntity){
                CustomShulkerBlockEntity entity = (CustomShulkerBlockEntity)rawEntity;
                
                boolean canOpen = true;
                if(entity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED){
                    canOpen = world.doesNotCollide(ShulkerLidCollisions.getLidCollisionBox(pos, state.get(FACING)));
                }
                
                if(canOpen){
                    NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
                    if(factory != null){
                        player.openHandledScreen(factory);
                        player.incrementStat(Stats.OPEN_SHULKER_BOX);
                        PiglinBrain.onGoldBlockBroken(player, true);
                    }
                }
                return ActionResult.CONSUME;
            }else{
                return ActionResult.PASS;
            }
        }
    }
    
    private ItemStack createStack(ItemStack stack, CustomShulkerBlockEntity entity){
        if(entity.isWearable()){
            stack.addEnchantment(MoreShulkers.Enchantments.WEARABLE, 1);
        }
    
        if(entity instanceof CustomShulkerBlockEntity.WithInventory){
            CompoundTag compoundTag = ((CustomShulkerBlockEntity.WithInventory)entity).serializeInventory(new CompoundTag());
            if(!compoundTag.isEmpty()){
                stack.putSubTag("BlockEntityTag", compoundTag);
            }
        }
        
        if(entity.hasCustomName()){
            stack.setCustomName(entity.getCustomName());
        }
        
        Item item = stack.getItem();
        if(item instanceof DyeableItem){
            int color = entity.getColor();
            if(color != CustomShulkerBlockEntity.DEFAULT_COLOR){
                ((DyeableItem)item).setColor(stack, color);
            }
        }
        
        return stack;
    }
    
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player){
        BlockEntity rawEntity = world.getBlockEntity(pos);
        if(rawEntity instanceof CustomShulkerBlockEntity.WithInventory){
            CustomShulkerBlockEntity.WithInventory entity = (CustomShulkerBlockEntity.WithInventory)rawEntity;
            
            if(!world.isClient && player.isCreative() && !entity.isEmpty()){
                ItemStack itemStack = createStack(new ItemStack(asItem()), entity);
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }
        
        super.onBreak(world, pos, state, player);
    }
    
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack){
        boolean customName = itemStack.hasCustomName();
        boolean wearable = EnchantmentHelper.getLevel(MoreShulkers.Enchantments.WEARABLE, itemStack) > 0;
        if(customName || wearable){
            BlockEntity rawEntity = world.getBlockEntity(pos);
            if(rawEntity instanceof CustomShulkerBlockEntity){
                CustomShulkerBlockEntity entity = (CustomShulkerBlockEntity)rawEntity;
                if(customName){
                    entity.setCustomName(itemStack.getName());
                }
                if(wearable){
                    entity.setWearable(true);
                }
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved){
        if(!state.isOf(newState.getBlock())){
            BlockEntity entity = world.getBlockEntity(pos);
            if(entity instanceof CustomShulkerBlockEntity){
                world.updateComparators(pos, state.getBlock());
            }
            
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public void buildTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options){
        super.buildTooltip(stack, world, tooltip, options);
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if(compoundTag != null){
            if(compoundTag.contains("Items", 9)){
                DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(6 * 9, ItemStack.EMPTY);
                Inventories.fromTag(compoundTag, defaultedList);
                int displayedStacks = 0;
                int totalStacks = 0;
                
                for(ItemStack itemStack : defaultedList){
                    if(!itemStack.isEmpty()){
                        totalStacks++;
                        if(displayedStacks <= 4){
                            displayedStacks++;
                            MutableText mutableText = itemStack.getName().shallowCopy();
                            mutableText.append(" x").append(String.valueOf(itemStack.getCount()));
                            tooltip.add(mutableText);
                        }
                    }
                }
                
                if(totalStacks - displayedStacks > 0){
                    tooltip.add((new TranslatableText("container.shulkerBox.more", totalStacks - displayedStacks)).formatted(Formatting.ITALIC));
                }
            }
            
            if(compoundTag.contains("Color", NbtType.INT)){
                int color = compoundTag.getInt("Color");
                tooltip.add(new TranslatableText("container.gud_moreshulkers.shulker.color", String.format("0x%06X", color)));
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof CustomShulkerBlockEntity ? VoxelShapes.cuboid(((CustomShulkerBlockEntity)blockEntity).getBoundingBox(state)) : VoxelShapes.fullCube();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorOutput(BlockState state){
        return true;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos){
        BlockEntity rawEntity = world.getBlockEntity(pos);
        if(rawEntity instanceof Inventory){
            return ScreenHandler.calculateComparatorOutput((Inventory)world.getBlockEntity(pos));
        }else{
            return 0;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack itemStack = super.getPickStack(world, pos, state);
        BlockEntity rawEntity = world.getBlockEntity(pos);
        if(rawEntity instanceof CustomShulkerBlockEntity){
            return createStack(itemStack, (CustomShulkerBlockEntity)rawEntity);
        }
        
        return itemStack;
    }
    
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx){
        return getDefaultState().with(FACING, ctx.getSide());
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder){
        super.appendProperties(builder);
        builder.add(FACING);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public PistonBehavior getPistonBehavior(BlockState state){
        return PistonBehavior.DESTROY;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation){
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror){
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex){
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity instanceof CustomShulkerBlockEntity){
            return ((CustomShulkerBlockEntity)entity).getColor();
        }else{
            return CustomShulkerBlockEntity.DEFAULT_COLOR;
        }
    }
    
    public abstract void openPlayerInventory(ItemStack stack, PlayerEntity player);
}
