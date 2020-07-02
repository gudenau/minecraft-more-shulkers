package net.gudenau.minecraft.moreshulkers.gui;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import static net.gudenau.minecraft.moreshulkers.MoreShulkers.MOD_ID;

public class FoodSlot extends Slot{
    @Environment(EnvType.CLIENT)
    public static final Identifier EMPTY_SLOT_FOOD = new Identifier(MOD_ID, "item/empty_slot_food");
    
    public FoodSlot(Inventory inventory, int id, int x, int y){
        super(inventory, id, x, y);
    }
    
    @Override
    public boolean canInsert(ItemStack stack){
        return stack.isFood() && super.canInsert(stack);
    }
    
    @Environment(EnvType.CLIENT)
    public Pair<Identifier, Identifier> getBackgroundSprite(){
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_SLOT_FOOD);
    }
}
