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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.world.BlockMiningProgress;
import net.minecraft.client.render.world.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@WrapWithCondition(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/world/WorldRenderer;setupMiningProgressState()V"))
	private boolean axolotlclient$removeMiningProgressGLState(WorldRenderer instance) {
		/* we should stop setting these GL states */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.blockEntityMiningProgress.get();
	}

	@WrapOperation(method = "renderEntities", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
	private <V extends BlockMiningProgress> Collection<V> axolotlclient$doNotRenderMiningProgress(Map<Integer, BlockMiningProgress> instance, Operation<Collection<V>> original) {
		/* goodbye france. hello paris */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.blockEntityMiningProgress.get()) {
			return Collections.emptyList();
		}
		return original.call(instance);
	}

	@WrapWithCondition(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/world/WorldRenderer;restoreMiningProgressState()V"))
	private boolean axolotlclient$removeMiningProgressGLState2(WorldRenderer instance) {
		/* we should stop setting these GL states again */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.blockEntityMiningProgress.get();
	}

	@WrapOperation(method = "renderMiningProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0))
	private Block axolotlclient$skipBlockEntityCheck(BlockState instance, Operation<Block> original) {
		/* this should remove the blockentity check stopping the mining progress from showing on them. */
		/* i definitely don't think this will do anything tho just due to how the block entity rendering works */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.blockEntityMiningProgress.get()) {
			if (instance.getBlock() instanceof BeaconBlock) {
				/* little trick to stop the mining animation from showing on beacons */
				//TODO: figure out why there even isnt a mining progress on 1.7 beacons???
				return Blocks.CHEST;
			}
			return Blocks.AIR;
		}
		return original.call(instance);
	}
}
