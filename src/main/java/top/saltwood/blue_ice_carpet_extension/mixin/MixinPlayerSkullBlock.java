package top.saltwood.blue_ice_carpet_extension.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.PlayerSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import top.saltwood.blue_ice_carpet_extension.util.DeathHandle;
import top.saltwood.blue_ice_carpet_extension.util.DeathInfo;
import top.saltwood.blue_ice_carpet_extension.util.DeathSkullInterface;

import java.util.UUID;

@Mixin(PlayerSkullBlock.class)
public abstract class MixinPlayerSkullBlock extends SkullBlock {
    protected MixinPlayerSkullBlock(Type skullType, Settings settings) {
        super(skullType, settings);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            return;
        }

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            BlockEntity blockEntity = serverWorld.getBlockEntity(pos);
            if (!(blockEntity instanceof DeathSkullInterface skull)) return;

            DeathInfo deathInfo = skull.deathInfo$get();
            if (deathInfo == null) return;

            // 1. Items
            ItemScatterer.spawn(serverWorld, pos, deathInfo.inventory());

            // 2. Experience orbs
            int xp = deathInfo.exp();
            while (xp > 0) {
                int spawnedXp = ExperienceOrbEntity.roundToOrbSize(xp);
                xp -= spawnedXp;
                serverWorld.spawnEntity(new ExperienceOrbEntity(serverWorld, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spawnedXp));
            }

            // 3. TextDisplay
            if (deathInfo.display() != null) {
                Entity display = serverWorld.getEntity(deathInfo.display());
                if (display != null) {
                    display.discard();
                }
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void afterBreak(@NotNull World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        if (blockEntity instanceof DeathSkullInterface skull && skull.deathInfo$get() != null) {
            player.incrementStat(Stats.MINED.getOrCreateStat(this));
            player.addExhaustion(0.005F);
            skull.deathInfo$set(null);
            return;
        }

        super.afterBreak(world, player, pos, state, blockEntity, stack);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        // Ensure the block entity implements interface
        if (!(blockEntity instanceof DeathSkullInterface death)) {
            return super.onUse(state, world, pos, player, hit);
        }

        DeathInfo deathInfo = death.deathInfo$get();
        // Validate death info
        if (deathInfo == null || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return super.onUse(state, world, pos, player, hit);
        }

        // Cast to SkullBlockEntity to check the owner profile
        SkullBlockEntity skull = (SkullBlockEntity) blockEntity;
        if (skull.getOwner() == null) return ActionResult.PASS;

        java.util.Optional<UUID> ownerProfile = skull.getOwner().id();

        // Check for permission
        if (ownerProfile.isPresent() && !player.getUuid().equals(ownerProfile.get()) && !player.hasPermissionLevel(2)) {
            player.sendMessage(Text.literal("You are not the owner of this grave!").formatted(Formatting.RED), true);
            return ActionResult.SUCCESS;
        }

        // Restore items and experience to the player
        DeathHandle.restore(serverPlayer, deathInfo);

        // Remove the associated Text Display entity if it exists
        if (world instanceof ServerWorld serverWorld && deathInfo.display() != null) {
            Entity display = serverWorld.getEntity(deathInfo.display());
            if (display != null) {
                display.discard();
            }
        }

        death.deathInfo$set(null);
        world.breakBlock(pos, false, player);

        return ActionResult.SUCCESS;
    }
}
