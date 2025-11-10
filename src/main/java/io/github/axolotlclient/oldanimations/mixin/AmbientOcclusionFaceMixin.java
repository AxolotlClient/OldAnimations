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
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.BlockModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.BitSet;

@Mixin(BlockModelRenderer.AmbientOcclusionFace.class)
public class AmbientOcclusionFaceMixin {

	@WrapOperation(method = "compute", at = @At(value = "INVOKE", target = "Ljava/util/BitSet;get(I)Z", ordinal = 3))
	private boolean axolotlclient$oldMinAmbientOcclusion(BitSet instance, int bitIndex, Operation<Boolean> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fastSmoothLighting.get() &&
			/* only show when fast smooth lighting is active */
			Minecraft.getInstance().options.ambientOcclusion == 1) {
			/* use simple vertex lightmap and brightness assignment */
			return false;
		}
		return original.call(instance, bitIndex);
	}
}
