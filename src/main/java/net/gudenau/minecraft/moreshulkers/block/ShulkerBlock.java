package net.gudenau.minecraft.moreshulkers.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;

public interface ShulkerBlock{
    @Environment(EnvType.CLIENT)
    SpriteIdentifier getTexture(ItemStack stack);
}
