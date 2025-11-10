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

import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceGateBlock.class)
public class FenceGateBlockMixin {

	@Shadow
	@Final
	public static BooleanProperty IN_WALL;

	@Inject(method = "resolveVirtualProperties", at = @At("HEAD"), cancellable = true)
	public void axolotlclient$cobbleWallFenceGateLogic(BlockState blockState, WorldView worldView, BlockPos blockPos, CallbackInfoReturnable<BlockState> cir) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fenceGateWallMode.get()) {
			/* directly ported from 1.7 */
			/* the actual bug fix was changing || to &&  */
			Direction direction = blockState.get(HorizontalFacingBlock.FACING);
			if ((direction == Direction.NORTH || direction == Direction.SOUTH) &&
				worldView.getBlockState(blockPos.west()).getBlock() == Blocks.COBBLESTONE_WALL && worldView.getBlockState(blockPos.east()).getBlock() == Blocks.COBBLESTONE_WALL ||
				(direction == Direction.EAST || direction == Direction.WEST) &&
					worldView.getBlockState(blockPos.north()).getBlock() == Blocks.COBBLESTONE_WALL && worldView.getBlockState(blockPos.south()).getBlock() == Blocks.COBBLESTONE_WALL) {
				blockState = blockState.set(IN_WALL, true);
			}
			cir.setReturnValue(blockState);
		}
	}
}
