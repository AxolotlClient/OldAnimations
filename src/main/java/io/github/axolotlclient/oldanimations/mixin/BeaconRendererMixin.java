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
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.block.entity.BeaconRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BeaconRenderer.class)
public class BeaconRendererMixin {

	@Unique
	private final float[] axolotlclient$oldBeaconColor = { 1.0F, 1.0F, 1.0F };

	@WrapOperation(method = "render(Lnet/minecraft/block/entity/BeaconBlockEntity;DDDFI)V", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
	private int axolotlclient$renderBeaconOnce(List<?> instance, Operation<Integer> original) {
		/* no need to iterate through the beam sections. just rendering one singular beacon beam like 1.7 */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.beaconRendering.get() ? 1 : original.call(instance);
	}

	@WrapOperation(method = "render(Lnet/minecraft/block/entity/BeaconBlockEntity;DDDFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BeaconBlockEntity$BeamSection;getHeight()I"))
	private int axolotlclient$oldBeaconHeight(BeaconBlockEntity.BeamSection instance, Operation<Integer> original) {
		/* old height limit for the beam */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.beaconRendering.get() ? 256 : original.call(instance);
	}

	@WrapOperation(method = "render(Lnet/minecraft/block/entity/BeaconBlockEntity;DDDFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BeaconBlockEntity$BeamSection;getColor()[F"))
	private float[] axolotlclient$oldBeaconColor(BeaconBlockEntity.BeamSection instance, Operation<float[]> original) {
		/* the one color to rule them all */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.beaconRendering.get() ? axolotlclient$oldBeaconColor : original.call(instance);
	}
}
