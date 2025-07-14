package com.o9wizardguy.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = SaplingBlock.class, priority = 900)
public abstract class SaplingBlockMixin {

    @Shadow @Final public static IntProperty STAGE;
    @Shadow @Final private SaplingGenerator generator;
    @Unique
    private static final TagKey<Block> GREENHOUSE_BLOCKS =
            TagKey.of(RegistryKeys.BLOCK, new Identifier("demandinggreenhouses", "greenhouse_blocks"));

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void allowGrowthUnderGreenhouse(ServerWorld world, BlockPos pos, BlockState state, net.minecraft.util.math.random.Random random, CallbackInfo ci) {
        // If the sky is blocked directly above
        if (!world.isSkyVisible(pos.up())) {
            // Check for greenhouse blocks 25–50 blocks above
            for (int i = 25; i <= 50; i++) {
                BlockPos checkPos = pos.up(i);
                BlockState checkState = world.getBlockState(checkPos);

                if (checkState.isIn(GREENHOUSE_BLOCKS)) {
                    if ((Integer)state.get(STAGE) == 0) {
                        world.setBlockState(pos, (BlockState)state.cycle(STAGE), 4);
                    } else {
                        this.generator.generate(world, world.getChunkManager().getChunkGenerator(), pos, state,random);
                    }
                    ci.cancel();
                }
            }
        }
    }
}