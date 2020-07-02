package net.gudenau.minecraft.moreshulkers.block.entity;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.util.NbtType;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.gudenau.minecraft.moreshulkers.gui.CraftyShulkerWorldScreenHandler;
import net.gudenau.minecraft.moreshulkers.mixin.CraftingInventoryAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class CraftyShulkerBlockEntity extends AbstractShulkerBlockEntity.WithInventory implements ExtendedScreenHandlerFactory, NamedScreenHandlerFactory{
    public static final int SLOT_RESULT = 9;
    public static final int SLOT_FOOD = 10;
    
    private Identifier recipeId = null;
    private Recipe<CraftingInventory> recipe = null;
    
    private final CraftingInventory craftingInventory = new CraftyInventory();
    private boolean checkRecipe = false;
    
    private int craftingProgress = 0;
    private float food = 0;
    
    public CraftyShulkerBlockEntity(){
        super(3*3 + 2, MoreShulkers.BlockEntities.CRAFTY_SHULKER);
    }
    
    @Override
    public Text getContainerName(){
        return new TranslatableText("container.gud_moreshulkers.craftyShulker");
    }
    
    @Override
    public void tick(){
        super.tick();
    
        DefaultedList<ItemStack> inventory = getInventory();
        
        boolean makeSound = false;
        
        float oldFood = food;
        
        if(food <= 1){
            ItemStack stack = inventory.get(SLOT_FOOD);
            if(!stack.isEmpty() && stack.isFood()){
                FoodComponent food = stack.getItem().getFoodComponent();
                this.food += food.getHunger() + food.getHunger() * food.getSaturationModifier() * 2.0F;
                stack.decrement(1);
                makeSound = true;
                markDirty();
    
                List<Pair<StatusEffectInstance, Float>> statusEffects = food.getStatusEffects();
                if(statusEffects != null && !statusEffects.isEmpty()){
                    List<LivingEntity> entities = world.getNonSpectatingEntities(LivingEntity.class, new Box(pos).expand(4));
                    for(LivingEntity entity : entities){
                        if(!entity.isAffectedBySplashPotions()){
                            continue;
                        }
                        
                        double distance = entity.squaredDistanceTo(
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5
                        );
                        
                        if(distance >= 16){
                            continue;
                        }
    
                        double proximity = 1.0D - Math.sqrt(distance) / 4.0D;
                        
                        for(Pair<StatusEffectInstance, Float> effectPair : statusEffects){
                            StatusEffectInstance statusEffectInstance = effectPair.getFirst();
                            StatusEffect statusEffect = statusEffectInstance.getEffectType();
                            if(statusEffect.isInstant()){
                                statusEffect.applyInstantEffect(null, null, entity, statusEffectInstance.getAmplifier(), proximity);
                            }else{
                                int duration = (int)(proximity * statusEffectInstance.getDuration() + 0.5D);
                                if(duration > 20){
                                    entity.addStatusEffect(new StatusEffectInstance(statusEffect, duration, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if(checkRecipe){
            checkRecipe = false;
            
            RecipeManager recipeManager = world.getRecipeManager();
            Optional<CraftingRecipe> foundRecipe = recipeManager.getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
            if(foundRecipe.isPresent()){
                recipe = foundRecipe.get();
                recipeId = recipe.getId();
            }else{
                recipe = null;
                recipeId = null;
            }
        }
        
        ItemStack outputStack = inventory.get(SLOT_RESULT);
        int oldCraftingProgress = craftingProgress;
        if(
            recipe != null &&
            recipe.matches(craftingInventory, world) &&
            (outputStack.isEmpty() || recipe.getOutput().isItemEqual(outputStack))
        ){
            if(food > 1){
                craftingProgress++;
                food -= 0.0025F;
                if(food < 0){
                    food = 0;
                }
    
                if(craftingProgress > 400){
                    craftingProgress = 0;
                    ItemStack result = recipe.craft(craftingInventory);
                    boolean crafted = false;
                    if(outputStack.isEmpty()){
                        inventory.set(SLOT_RESULT, result.copy());
                        crafted = true;
                    }else{
                        if(result.getCount() + outputStack.getCount() <= outputStack.getMaxCount()){
                            outputStack.setCount(outputStack.getCount() + result.getCount());
                            crafted = true;
                        }
                    }
        
                    if(crafted){
                        for(int i = 0; i < 9; i++){
                            ItemStack stack = inventory.get(i);
                            if(!stack.isEmpty()){
                                stack.decrement(1);
                            }
                        }
            
                        makeSound = true;
                    }
                }
            }
        }else{
            craftingProgress = 0;
        }
        if(oldCraftingProgress != craftingProgress || oldFood != food){
            markDirty();
        }
    
        if(makeSound){
            world.playSound(
                null,
                pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ENTITY_SHULKER_AMBIENT,
                SoundCategory.BLOCKS,
                1.0F,
                (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F
            );
        }
    }
    
    @Override
    public void markDirty(){
        super.markDirty();
        checkRecipe = true;
    }
    
    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir){
        if(slot == SLOT_FOOD){
            return stack.getItem().isFood() && super.canInsert(slot, stack, dir);
        }
        return super.canInsert(slot, stack, dir);
    }
    
    @Override
    public CompoundTag serializeInventory(CompoundTag tag){
        tag = super.serializeInventory(tag);
        if(recipeId != null){
            tag.putString("Recipe", recipeId.toString());
        }
        if(craftingProgress > 0){
            tag.putInt("Progress", craftingProgress);
        }
        if(food > 0){
            tag.putFloat("Food", food);
        }
        return tag;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void deserializeInventory(CompoundTag tag){
        super.deserializeInventory(tag);
        recipeId = null;
        recipe = null;
        if(tag.contains("Recipe", NbtType.STRING)){
            recipeId = new Identifier(tag.getString("Recipe"));
            Optional<? extends Recipe<?>> optionalRecipe = world.getRecipeManager().get(recipeId);
            if(optionalRecipe.isPresent()){
                Recipe<?> recipe = optionalRecipe.get();
                if(recipe.getType() == RecipeType.CRAFTING){
                    this.recipe = (Recipe<CraftingInventory>)recipe;
                }else{
                    recipeId = null;
                }
            }else{
                recipeId = null;
            }
        }
        if(tag.contains("Progress", NbtType.INT)){
            craftingProgress = tag.getInt("Progress");
        }else{
            craftingProgress = 0;
        }
        if(tag.contains("Food", NbtType.FLOAT)){
            food = tag.getFloat("Food");
        }else{
            food = 0;
        }
    }
    
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player){
        return new CraftyShulkerWorldScreenHandler(syncId, inv, this);
    }
    
    @Override
    public Text getDisplayName(){
        return getName();
    }
    
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf){
        packetByteBuf.writeBlockPos(pos);
    }
    
    private class CraftyInventory extends CraftingInventory{
        public CraftyInventory(){
            super(null, 0, 0);
            ((CraftingInventoryAccessor)this).setStacks(getInventory());
            ((CraftingInventoryAccessor)this).setWidth(3);
            ((CraftingInventoryAccessor)this).setHeight(3);
        }
    
        @Override
        public int size(){
            return 9;
        }
    
        @Override
        public ItemStack getStack(int slot){
            ItemStack stack = super.getStack(slot);
            if(stack.isEmpty()){
                return ItemStack.EMPTY;
            }
            stack = stack.copy();
            stack.decrement(1);
            return stack;
        }
    }
    
    public int getCraftingProgress(){
        return craftingProgress;
    }
    
    public float getFoodAmount(){
        return food;
    }
    
    public void setCraftingProgress(int craftingProgress){
        this.craftingProgress = craftingProgress;
    }
    
    public void setFoodAmount(float food){
        this.food = food;
    }
}
