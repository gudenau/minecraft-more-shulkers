package net.gudenau.minecraft.moreshulkers.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.gudenau.minecraft.moreshulkers.MoreShulkersClient;
import net.gudenau.minecraft.moreshulkers.block.entity.CustomShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.block.entity.EnderShulkerBlockEntity;
import net.gudenau.minecraft.moreshulkers.gui.EnderShulkerPlayerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EnderShulkerBlock extends AbstractShulkerBlock{
    public EnderShulkerBlock(Settings settings){
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockView world){
        return new EnderShulkerBlockEntity();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public SpriteIdentifier getTexture(ItemStack stack){
        return MoreShulkersClient.Textures.ENDER_SHULKER;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random){
        ParticleManager particleManager = MinecraftClient.getInstance().particleManager;
        
        boolean colored = false;
        float red = 0;
        float green = 0;
        float blue = 0;
        
        BlockEntity rawEntity = world.getBlockEntity(pos);
        if(rawEntity instanceof EnderShulkerBlockEntity){
            EnderShulkerBlockEntity entity = (EnderShulkerBlockEntity)rawEntity;
            int color = entity.getColor();
            if(color != CustomShulkerBlockEntity.DEFAULT_COLOR){
                colored = true;
                red = ((color >>> 16) & 0xFF) * 0.00390625f;
                green = ((color >>> 8) & 0xFF) * 0.00390625f;
                blue = (color & 0xFF) * 0.00390625f;
            }
        }
        
        for(int i = 0; i < 3; ++i){
            int xOff = random.nextInt(2) * 2 - 1;
            int zOff = random.nextInt(2) * 2 - 1;
            double posX = pos.getX() + 0.5D + 0.25D * xOff;
            double posY = pos.getY() + random.nextFloat();
            double posZ = pos.getZ() + 0.5D + 0.25D * zOff;
            double velX = random.nextFloat() * xOff;
            double velY = (random.nextFloat() - 0.5D) * 0.125D;
            double velZ = random.nextFloat() * zOff;
            Particle particle = particleManager.addParticle(ParticleTypes.PORTAL, posX, posY, posZ, velX, velY, velZ);
            if(colored && particle != null){
                particle.setColor(red, green, blue);
            }
        }
    }
    
    @Override
    public void openPlayerInventory(ItemStack stack, PlayerEntity player){
        player.openHandledScreen(new ExtendedScreenHandlerFactory(){
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf){}
    
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player){
                return new EnderShulkerPlayerScreenHandler(syncId, inv);
            }
            
            @Override
            public Text getDisplayName(){
                return stack.getName();
            }
        });
    }
}
