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
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.world.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderChunk.class)
public class RenderChunkMixin {

	@WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;shouldRenderOffScreen()Z"))
	private boolean axolotlclient$dontRenderBeamTwice(BlockEntityRenderer<?> instance, Operation<Boolean> original) {
		/* since MC-112730 and MC-68247, mojang decided to render the beam twice. */
		/* the second time is to supposedly ensure that the beam is not culled, but, */
		/* this implementation sucks. either way, 1.7 has the bug so ima just revert this change lol. */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.beaconRendering.get()) {
			return false;
		}
		return original.call(instance);
	}
}
