package com.o9wizardguy.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin {

    @Shadow
    protected SaplingGenerator generator;

    private static final TagKey<Block> GREENHOUSE_BLOCKS =
            TagKey.of(RegistryKeys.BLOCK, new Identifier("demandinggreenhouses", "greenhouse_blocks"));

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void demandingGreenhouses$allowGrowthInGreenhouses(ServerWorld world, BlockPos pos, BlockState state, Random random, CallbackInfo ci) {
        if (!world.isSkyVisible(pos.up())) {
            for (int i = 25; i <= 50; i++) {
                BlockPos checkPos = pos.up(i);
                BlockState checkState = world.getBlockState(checkPos);
                if (checkState.isIn(GREENHOUSE_BLOCKS)) {
                    System.out.println("[DemandingGreenhouses] Greenhouse block detected above " + pos + ". Preventing sapling death and forcing growth.");

                    if (state.get(SaplingBlock.STAGE) == 0) {
                        world.setBlockState(pos, state.cycle(SaplingBlock.STAGE), 4);
                    } else {
                        generator.generate(world, world.getChunkManager().getChunkGenerator(), pos, state, random);
                    }

                    ci.cancel();
                    return;
                }
            }
        }
    }
}
