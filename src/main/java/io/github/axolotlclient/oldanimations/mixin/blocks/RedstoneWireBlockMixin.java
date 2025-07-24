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

package io.github.axolotlclient.oldanimations.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.PlayerUtil;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin {

	@WrapOperation(method = "getConnection", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;shouldConnectTo(Lnet/minecraft/block/state/BlockState;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean axolotlclient$disconnectBlock(BlockState blockState, Direction direction, Operation<Boolean> original, @Local(index = 4, ordinal = 1) BlockPos blockPos2) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.useAndMineDestroyVisual.get() &&
			PlayerUtil.INSTANCE.isFakeMinedBlock(blockPos2)) {
			return false;
		}
		return original.call(blockState, direction);
	}

	@WrapOperation(method = "getConnection", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;shouldConnectTo(Lnet/minecraft/block/state/BlockState;)Z"))
	private boolean axolotlclient$disconnectBlock2(BlockState blockState, Operation<Boolean> original, @Local(index = 4, ordinal = 1) BlockPos blockPos2) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.useAndMineDestroyVisual.get() &&
			PlayerUtil.INSTANCE.isFakeMinedBlock(blockPos2)) {
			return false;
		}
		return original.call(blockState);
	}

	@WrapOperation(method = "connectsTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;shouldConnectTo(Lnet/minecraft/block/state/BlockState;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean axolotlclient$disconnectBlock3(BlockState blockState, Direction direction, Operation<Boolean> original, @Local(index = 4, ordinal = 1) BlockPos blockPos2) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.useAndMineDestroyVisual.get() &&
			PlayerUtil.INSTANCE.isFakeMinedBlock(blockPos2)) {
			return false;
		}
		return original.call(blockState, direction);
	}

	@WrapOperation(method = "connectsTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;shouldConnectTo(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean axolotlclient$disconnectBlock4(WorldView worldView, BlockPos blockPos, Operation<Boolean> original, @Local(index = 4, ordinal = 1) BlockPos blockPos2) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.useAndMineDestroyVisual.get() &&
			PlayerUtil.INSTANCE.isFakeMinedBlock(blockPos2)) {
			return false;
		}
		return original.call(worldView, blockPos);
	}

	@WrapOperation(method = "getConnection", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;shouldConnectTo(Lnet/minecraft/block/state/BlockState;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean axolotlclient$disconnectBlock5(BlockState blockState, Direction direction, Operation<Boolean> original, @Local(index = 4, ordinal = 1) BlockPos blockPos2) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.useAndMineDestroyVisual.get() &&
			PlayerUtil.INSTANCE.isFakeMinedBlock(blockPos2)) {
			return false;
		}
		return original.call(blockState, direction);
	}

	@WrapOperation(method = "getConnection", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;shouldConnectTo(Lnet/minecraft/block/state/BlockState;)Z"))
	private boolean axolotlclient$disconnectBlock6(BlockState blockState, Operation<Boolean> original, @Local(index = 4, ordinal = 1) BlockPos blockPos2) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.useAndMineDestroyVisual.get() &&
			PlayerUtil.INSTANCE.isFakeMinedBlock(blockPos2)) {
			return false;
		}
		return original.call(blockState);
	}
}
