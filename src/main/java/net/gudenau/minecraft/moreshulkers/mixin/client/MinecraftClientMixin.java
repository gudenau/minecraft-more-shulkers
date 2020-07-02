package net.gudenau.minecraft.moreshulkers.mixin.client;

import net.gudenau.minecraft.moreshulkers.MoreShulkers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin{
    @Shadow public ClientPlayerEntity player;
    
    @Redirect(
        method = "handleInputEvents",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/tutorial/TutorialManager;onInventoryOpened()V"
            ),
            to = @At(
                value = "FIELD",
                opcode = Opcodes.GETFIELD,
                target = "Lnet/minecraft/client/options/GameOptions;keyAdvancements:Lnet/minecraft/client/options/KeyBinding;"
            )
        )
    )
    private void handleInputEvents$InventoryScreen$init(MinecraftClient client, Screen screen){
        ItemStack stack = player.inventory.armor.get(2);
        if(EnchantmentHelper.getLevel(MoreShulkers.Enchantments.WEARABLE, stack) > 0){
            //FIXME
            client.openScreen(screen);
        }else{
            client.openScreen(screen);
        }
    }
}
