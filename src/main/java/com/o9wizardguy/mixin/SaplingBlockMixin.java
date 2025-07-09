package com.o9wizardguy.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SaplingBlock.class, priority = 2000)
public abstract class SaplingBlockMixin {

    private static final TagKey<Block> GREENHOUSE_BLOCKS =
            TagKey.of(RegistryKeys.BLOCK, new Identifier("demandinggreenhouses", "greenhouse_blocks"));

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void allowGrowthUnderGreenhouse(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        // If the sky is blocked directly above
        if (!world.isSkyVisible(pos.up())) {
            // Check for greenhouse blocks 25–50 blocks above
            for (int i = 25; i <= 50; i++) {
                BlockPos checkPos = pos.up(i);
                BlockState checkState = world.getBlockState(checkPos);
                if (checkState.isIn(GREENHOUSE_BLOCKS)) {
                    // Cancel the tick to prevent Demanding Saplings from killing the sapling
                    ci.cancel();

                    // Force sapling growth even though the sky is blocked
                    SaplingBlock sapling = (SaplingBlock)(Object)this;
                    if (world.isAir(pos.up()) && sapling.canGrow(world, random, pos, state)) {
                        sapling.grow(world, random, pos, state);
                    }
                    return;
                }
            }
        }
    }
}
