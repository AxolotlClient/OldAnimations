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
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.vertex.BufferBuilder;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelRenderer.class)
public class BlockModelRendererMixin {

	@Inject(method = "render(Lnet/minecraft/world/WorldView;Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/block/state/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/vertex/BufferBuilder;Z)Z", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$fixDoubleBlockDestroyModelDelay(WorldView worldView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl, CallbackInfoReturnable<Boolean> cir) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.tallBlockBreakSync.get()) {
			/* finally, i have fixed MC-53439 */
			Block block = blockState.getBlock();
			int metadata = block.getMetadataFromState(blockState);
			boolean isUpperHalf = (metadata & 8) != 0;
			boolean cancel = false;

			if (block instanceof DoorBlock) {
				cancel = worldView.isAir(isUpperHalf ? blockPos.down() : blockPos.up());
			}

			if (block instanceof DoublePlantBlock) {
				cancel = isUpperHalf && worldView.isAir(blockPos.down());
			}

			if (cancel) {
				cir.setReturnValue(false);
			}
		}
	}
}
