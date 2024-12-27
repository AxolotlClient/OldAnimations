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

import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.ducks.Sneaky;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.FishingBobberRenderer;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FishingBobberRenderer.class)
public class FishingBobberEntityRendererMixin {

	@ModifyArgs(method = "render(Lnet/minecraft/entity/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"))
	private void axolotlclient$modifyLinePosition(Args args) {
		if (isRodEnabled()) {
			/* original values from 1.7 */
			args.set(0, (double) args.get(0) - 0.24D);
			args.set(2, (double) args.get(2) + 0.45D);
		}
	}

	@ModifyConstant(method = "render(Lnet/minecraft/entity/FishingBobberEntity;DDDFF)V", constant = @Constant(doubleValue = 0.8D))
	public double axolotlclient$moveLinePosition(double constant) {
		/* original value from 1.7 */
		if (isRodEnabled()) constant += 0.05D;
		return constant;
	}

	@Redirect(method = "render(Lnet/minecraft/entity/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;isSneaking()Z"))
	public boolean axolotlclient$removeSneakTranslation(PlayerEntity instance) {
		return !isRodEnabled() && instance.isSneaking();
	}

	@Redirect(method = "render(Lnet/minecraft/entity/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;getEyeHeight()F"))
	public float axolotlclient$useLerpEyeHeight_Fish(PlayerEntity instance) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().rod.get()) {
			return ((Sneaky) Minecraft.getInstance().gameRenderer).axolotlclient$getEyeHeight();
		}
		return instance.getEyeHeight();
	}

	@Unique
	private static boolean isRodEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().rod.get();
	}
}
