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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.PlayerUtil;
import net.minecraft.client.render.entity.FishingBobberRenderer;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FishingBobberRenderer.class)
public abstract class FishingBobberEntityRendererMixin {

	@ModifyArgs(method = "render(Lnet/minecraft/entity/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"))
	private void axolotlclient$modifyLinePosition(Args args) {
		if (areItemPositionsEnabled()) {
			/* original values from 1.7 */
			args.set(0, (double) args.get(0) - 0.24D);
			args.set(2, (double) args.get(2) + 0.45D);
		}
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/FishingBobberEntity;DDDFF)V", at = @At(value = "CONSTANT", args = "doubleValue=0.8D"))
	private double axolotlclient$moveLinePosition(double constant) {
		/* original values from 1.7 */
		return constant + (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.stickRod.get() ? 0.05D : 0.0D);
	}

	@WrapOperation(method = "render(Lnet/minecraft/entity/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;isSneaking()Z"))
	private boolean axolotlclient$removeSneakTranslation(PlayerEntity instance, Operation<Boolean> original) {
		return (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.stopLineTranslateSneak.get()) && original.call(instance);
	}

	@WrapOperation(method = "render(Lnet/minecraft/entity/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;getEyeHeight()F"))
	private float axolotlclient$useLerpEyeHeight_Fish(PlayerEntity instance, Operation<Float> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.smoothSneaking.get()) {
			return PlayerUtil.INSTANCE.getEyeHeightSneakOffset();
		}
		return original.call(instance);
	}

	@Unique
	private static boolean areItemPositionsEnabled() {
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get();
	}
}
