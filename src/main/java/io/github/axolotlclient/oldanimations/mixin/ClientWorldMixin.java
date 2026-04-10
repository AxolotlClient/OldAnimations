/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.oldanimations.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.storage.WorldStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {

	protected ClientWorldMixin(WorldStorage worldStorage, WorldData worldData, Dimension dimension, Profiler profiler, boolean bl) {
		super(worldStorage, worldData, dimension, profiler, bl);
	}

	@WrapOperation(method = "doRandomDisplayTicks", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;randomDisplayTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;Ljava/util/Random;)V"))
	private void axolotlclient$showDepthSuspendParticle(Block instance, World world, BlockPos blockPos, BlockState blockState, Random random, Operation<Void> original) {
		/* renders the depth suspend particle */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.voidFog.get()) {
			if (world.isAir(blockPos)) {
				if (random.nextInt(8) > blockPos.getY() &&
					/* 1.7's hasFog() method */
					((DimensionAccessor) dimension).getGeneratorType() != WorldGeneratorType.FLAT && !dimension.hasNoSky()) {
					addParticle(ParticleType.SUSPENDED_DEPTH, blockPos.getX() + random.nextFloat(), blockPos.getY() + random.nextFloat(), blockPos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0);
				}
			} else {
				original.call(instance, world, blockPos, blockState, random);
			}
		} else {
			original.call(instance, world, blockPos, blockState, random);
		}
	}
}
