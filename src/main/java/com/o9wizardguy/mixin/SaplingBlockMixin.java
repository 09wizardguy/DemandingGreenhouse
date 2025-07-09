package com.o9wizardguy.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin {
    private static final TagKey<Block> GREENHOUSE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("demandinggreenhouses", "greenhouse_blocks"));

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void allowGrowthUnderGreenhouse(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        boolean greenhouseAbove = false;

        for (int i = 25; i <= 50; i++) {
            BlockPos checkPos = pos.up(i);
            BlockState checkState = world.getBlockState(checkPos);
            if (checkState.isIn(GREENHOUSE_BLOCKS)) {
                greenhouseAbove = true;
                break;
            }
        }

        if (greenhouseAbove) {
            SaplingBlock sapling = (SaplingBlock)(Object)this;
            if (world.isAir(pos.up()) && sapling.canGrow(world, world.random, pos, state)) {
                sapling.grow(world, world.random, pos, state);
            }
        }
    }
}
