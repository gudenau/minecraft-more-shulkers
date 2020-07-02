package net.gudenau.minecraft.moreshulkers.gui;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class EnderShulkerPlayerScreenHandler extends ScreenHandler{
    public EnderShulkerPlayerScreenHandler(int syncId, PlayerInventory playerInventory){
        super(MoreShulkers.Containers.ENDER_PLAYER, syncId);
        Inventory inventory = playerInventory.player.getEnderChestInventory();
        
        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new ShulkerBoxSlot(inventory, x + y * 3, 8 + x * 18, 18 + y * 18));
            }
        }
    
        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 85 + y * 18));
            }
        }
    
        for(int y = 0; y < 9; ++y) {
            addSlot(new Slot(playerInventory, y, 8 + y * 18, 143));
        }
    
        addSlot(new Slot(playerInventory, 4 * 9 + 3, 176, 72){
            @Environment(EnvType.CLIENT)
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite(){
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE);
            }
        });
        addSlot(new Slot(playerInventory, 4 * 9 + 2, 176, 90){
            @Environment(EnvType.CLIENT)
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite(){
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE);
            }
        });
        addSlot(new Slot(playerInventory, 4 * 9 + 1, 176, 108){
            @Environment(EnvType.CLIENT)
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite(){
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE);
            }
        });
        addSlot(new Slot(playerInventory, 4 * 9 + 0, 176, 126){
            @Environment(EnvType.CLIENT)
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite(){
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE);
            }
        });
        addSlot(new Slot(playerInventory, 4 * 9 + 4, 176, 144){
            @Environment(EnvType.CLIENT)
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite(){
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
    }
    
    @Override
    public boolean canUse(PlayerEntity player){
        return true;
    }
    
    @Override
    public ItemStack transferSlot(PlayerEntity player, int index){
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if(slot != null && slot.hasStack()){
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if(index < 6 * 9){
                if(!insertItem(itemStack2, 3 * 9, slots.size(), true)){
                    return ItemStack.EMPTY;
                }
            }else if(!insertItem(itemStack2, 0, 6 * 9, false)){
                return ItemStack.EMPTY;
            }
            
            if(itemStack2.isEmpty()){
                slot.setStack(ItemStack.EMPTY);
            }else{
                slot.markDirty();
            }
        }
        
        return itemStack;
    }
}
