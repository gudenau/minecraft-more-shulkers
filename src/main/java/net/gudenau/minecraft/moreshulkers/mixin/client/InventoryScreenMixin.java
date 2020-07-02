package net.gudenau.minecraft.moreshulkers.mixin.client;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.gudenau.minecraft.moreshulkers.item.CustomShulkerItem;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider{
    private InventoryScreenMixin(){
        super(null, null, null);
        throw new RuntimeException("Don't do that, please.");
    }
    
    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;update()V"
        )
    )
    private void tick(CallbackInfo info){
        ItemStack chestPieceStack = playerInventory.armor.get(2);
        Item chestPiece = chestPieceStack.getItem();
        if(chestPiece instanceof CustomShulkerItem){
            ClientSidePacketRegistry.INSTANCE.sendToServer(MoreShulkers.Packets.REQUEST_GUI, new PacketByteBuf(Unpooled.buffer(0)));
        }else if(chestPiece instanceof BlockItem){
            Block chestPieceBlock = ((BlockItem)chestPiece).getBlock();
            if(chestPieceBlock instanceof ShulkerBoxBlock){
                //TODO
            }
        }
    }
}
