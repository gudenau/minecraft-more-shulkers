package net.gudenau.minecraft.moreshulkers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.gudenau.minecraft.moreshulkers.block.CraftyShulkerBlock;
import net.gudenau.minecraft.moreshulkers.block.EnderShulkerBlock;
import net.gudenau.minecraft.moreshulkers.block.LargeShulkerBlock;
import net.gudenau.minecraft.moreshulkers.block.entity.CraftyShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.block.entity.EnderShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.block.entity.LargeShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.duck.EnderChestInventoryDuck;
import net.gudenau.minecraft.moreshulkers.enchantment.WearableEnchantment;
import net.gudenau.minecraft.moreshulkers.gui.*;
import net.gudenau.minecraft.moreshulkers.item.CustomShulkerItem;
import net.gudenau.minecraft.moreshulkers.recipe.CustomShulkerRecipeColor;
import net.gudenau.minecraft.moreshulkers.recipe.LargeShulkerRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

import static net.minecraft.block.Blocks.SHULKER_BOX;

public class MoreShulkers implements ModInitializer{
    public static final String MOD_ID = "gud_moreshulkers";
    
    @Override
    public void onInitialize(){
        Enchantments.init();
        BlockEntities.init();
        Blocks.init();
        Items.init();
        Containers.init();
        Recipes.init();
        Packets.init();
    }

    public static final class Packets{
        public static final Identifier REQUEST_GUI = new Identifier(MOD_ID, "request_gui");
        public static final Identifier CLOSE_GUI = new Identifier(MOD_ID, "close_gui");
    
        private static void init(){
            ServerSidePacketRegistry.INSTANCE.register(REQUEST_GUI, (context, data)->
                context.getTaskQueue().execute(()->{
                    PlayerEntity player = context.getPlayer();
                    ItemStack chestPieceStack = player.inventory.armor.get(2);
                    Item chestPiece = chestPieceStack.getItem();
                    if(chestPiece instanceof CustomShulkerItem){
                        ((CustomShulkerItem<?>)chestPiece).openInventory(chestPieceStack, player);
                    }else if(chestPiece instanceof BlockItem){
                        Block chestPieceBlock = ((BlockItem)chestPiece).getBlock();
                        if(chestPieceBlock instanceof ShulkerBoxBlock){
                            //TODO
                        }
                    }
                })
            );
            ServerSidePacketRegistry.INSTANCE.register(CLOSE_GUI, (context, data)->
                context.getTaskQueue().execute(()->{
                    PlayerEntity player = context.getPlayer();
                    player.currentScreenHandler = player.playerScreenHandler;
                })
            );
        }
    }
    
    public static final class Containers{
        public static final Identifier VANILLA_WORLD_ID = new Identifier(MOD_ID, "vanilla_world");
        public static final Identifier LARGE_WORLD_ID = new Identifier(MOD_ID, "large_world");
        public static final Identifier ENDER_WORLD_ID = new Identifier(MOD_ID, "ender_world");
        public static final Identifier CRAFTY_WORLD_ID = new Identifier(MOD_ID, "crafty_world");
    
        public static final Identifier SMALL_PLAYER_ID = new Identifier(MOD_ID, "small_player");
        public static final Identifier LARGE_PLAYER_ID = new Identifier(MOD_ID, "large_player");
        public static final Identifier ENDER_PLAYER_ID = new Identifier(MOD_ID, "ender_player");
        public static final Identifier CRAFTY_PLAYER_ID = new Identifier(MOD_ID, "crafty_player");

        //public static ScreenHandlerType<SmallShulkerPlayerDescription> SMALL_PLAYER;
        public static ScreenHandlerType<LargeShulkerPlayerScreenHandler> LARGE_PLAYER;
        public static ScreenHandlerType<EnderShulkerPlayerScreenHandler> ENDER_PLAYER;
        public static ScreenHandlerType<CraftyShulkerPlayerScreenHandler> CRAFTY_PLAYER;
    
        public static ScreenHandlerType<LargeShulkerWorldScreenHandler> LARGE_WORLD;
        public static ScreenHandlerType<EnderShulkerWorldScreenHandler> ENDER_WORLD;
        public static ScreenHandlerType<CraftyShulkerWorldScreenHandler> CRAFTY_WORLD;
        
        private static void init(){
            /*
            SMALL_PLAYER = ScreenHandlerRegistry.registerExtended(SMALL_PLAYER_ID, (syncId, inventory, buffer)->
                    new SmallShulkerPlayerDescription(syncId, inventory.player, inventory.armor.get(2))
            );
            */
            LARGE_PLAYER = ScreenHandlerRegistry.registerExtended(LARGE_PLAYER_ID, (syncId, inventory, buffer)->
                new LargeShulkerPlayerScreenHandler(syncId, inventory, inventory.armor.get(2))
            );
            ENDER_PLAYER = ScreenHandlerRegistry.registerExtended(ENDER_PLAYER_ID, (syncId, inventory, buffer)->
                new EnderShulkerPlayerScreenHandler(syncId, inventory)
            );
            CRAFTY_PLAYER = ScreenHandlerRegistry.registerExtended(CRAFTY_PLAYER_ID, (syncId, inventory, buffer)->
                new CraftyShulkerPlayerScreenHandler(syncId, inventory, inventory.armor.get(2))
            );
            
            LARGE_WORLD = ScreenHandlerRegistry.registerExtended(LARGE_WORLD_ID, (syncId, inventory, buffer)->{
                BlockEntity entity = inventory.player.getEntityWorld().getBlockEntity(buffer.readBlockPos());
                if(entity instanceof LargeShulkerBlockEntity){
                    return new LargeShulkerWorldScreenHandler(syncId, inventory, (LargeShulkerBlockEntity)entity);
                }else{
                    return null;
                }
            });
            ENDER_WORLD = ScreenHandlerRegistry.registerExtended(ENDER_WORLD_ID, (syncId, inventory, buffer)->{
                BlockEntity entity = inventory.player.getEntityWorld().getBlockEntity(buffer.readBlockPos());
                if(entity instanceof EnderShulkerBlockEntity){
                    EnderChestInventory enderInventory = inventory.player.getEnderChestInventory();
                    ((EnderChestInventoryDuck)enderInventory).gud_moreshulkers$setActiveBlockEntity((EnderShulkerBlockEntity)entity);
                    return new EnderShulkerWorldScreenHandler(syncId, inventory, enderInventory);
                }else{
                    return null;
                }
            });
            CRAFTY_WORLD = ScreenHandlerRegistry.registerExtended(CRAFTY_WORLD_ID, (syncId, inventory, buffer)->{
                BlockEntity entity = inventory.player.getEntityWorld().getBlockEntity(buffer.readBlockPos());
                if(entity instanceof CraftyShulkerBlockEntity){
                    return new CraftyShulkerWorldScreenHandler(syncId, inventory, (CraftyShulkerBlockEntity)entity);
                }else{
                    return null;
                }
            });
        }
    }
    
    public static final class Blocks{
        public static final LargeShulkerBlock LARGE_SHULKER = new LargeShulkerBlock(
            FabricBlockSettings.copyOf(SHULKER_BOX)
                .breakByTool(FabricToolTags.PICKAXES)
        );
        public static final EnderShulkerBlock ENDER_SHULKER = new EnderShulkerBlock(
            FabricBlockSettings.copyOf(SHULKER_BOX)
                .breakByTool(FabricToolTags.PICKAXES)
        );
        public static final CraftyShulkerBlock CRAFTY_SHULKER = new CraftyShulkerBlock(
            FabricBlockSettings.copyOf(SHULKER_BOX)
                .breakByTool(FabricToolTags.PICKAXES)
        );
        
        private static void init(){
            register("large_shulker", LARGE_SHULKER);
            register("ender_shulker", ENDER_SHULKER);
            register("crafty_shulker", CRAFTY_SHULKER);
        }
    
        private static void register(String name, Block block){
            Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
        }
    }
    
    public static final class BlockEntities{
        public static final BlockEntityType<LargeShulkerBlockEntity> LARGE_SHULKER = create(LargeShulkerBlockEntity::new, Blocks.LARGE_SHULKER);
        public static final BlockEntityType<EnderShulkerBlockEntity> ENDER_SHULKER = create(EnderShulkerBlockEntity::new, Blocks.ENDER_SHULKER);
        public static final BlockEntityType<CraftyShulkerBlockEntity> CRAFTY_SHULKER = create(CraftyShulkerBlockEntity::new, Blocks.CRAFTY_SHULKER);
    
        private static <T extends BlockEntity> BlockEntityType<T> create(Supplier<T> supplier, Block... blocks){
            return BlockEntityType.Builder.create(supplier, blocks).build(null);
        }
    
        private static void init(){
            register("large_shulker", LARGE_SHULKER);
            register("ender_shulker", ENDER_SHULKER);
            register("crafty_shulker", CRAFTY_SHULKER);
        }

        private static void register(String name, BlockEntityType<? extends BlockEntity> type){
            Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, name), type);
        }
    }
    
    public static final class Items{
        public static final CustomShulkerItem<LargeShulkerBlock> LARGE_SHULKER = new CustomShulkerItem<>(
            Blocks.LARGE_SHULKER,
            new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)
        );
        public static final CustomShulkerItem<EnderShulkerBlock> ENDER_SHULKER = new CustomShulkerItem<>(
            Blocks.ENDER_SHULKER,
            new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)
        );
        public static final CustomShulkerItem<CraftyShulkerBlock> CRAFTY_SHULKER = new CustomShulkerItem<>(
            Blocks.CRAFTY_SHULKER,
            new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)
        );
    
        private static void init(){
            register(LARGE_SHULKER);
            register(ENDER_SHULKER);
            register(CRAFTY_SHULKER);
        }

        private static void register(BlockItem item){
            Registry.register(Registry.ITEM, Registry.BLOCK.getId(item.getBlock()), item);
        }
    
        private static void register(String name, Item item){
            Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), item);
        }
    }
    
    public static final class Enchantments{
        public static final Enchantment WEARABLE = new WearableEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.CHEST);
        
        private static void init(){
            Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "wearable"), WEARABLE);
        }
    }
    
    public static final class Recipes{
        public static final SpecialRecipeSerializer<LargeShulkerRecipe> LARGE_SHULKER_BOX = new SpecialRecipeSerializer<>(LargeShulkerRecipe::new);
        public static final SpecialRecipeSerializer<CustomShulkerRecipeColor> CUSTOM_SHULKER_BOX_COLOR = new SpecialRecipeSerializer<>(CustomShulkerRecipeColor::new);
        
        private static void init(){
            register("crafting_special_large_shulker", LARGE_SHULKER_BOX);
            register("crafting_special_custom_shulker_color", CUSTOM_SHULKER_BOX_COLOR);
        }
        
        private static void register(String name, RecipeSerializer<? extends Recipe<?>> recipe){
            Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MOD_ID, name), recipe);
        }
    }
}
