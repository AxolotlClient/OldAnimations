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

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.minecraft.client.render.model.entity.PlayerModel;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin extends HumanoidModel {

	@Shadow
	private ModelPart cape;

	@Inject(method = "setAngles", at = @At("HEAD"))
	private void axolotlclient$copyCapePivot(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity, CallbackInfo ci, @Share("pivotY") LocalFloatRef pivotY) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonCapePosition.get()) {
			/* capturing the initial value of the cape's pivot */
			pivotY.set(cape.pivotY);
		}
	}

	@Inject(method = "setAngles", at = @At("TAIL"))
	private void axolotlclient$disableSneakCapeTranslations(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity, CallbackInfo ci, @Share("pivotY") LocalFloatRef pivotY) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonCapePosition.get()) {
			/* in order to completely cancel out the cape pivot changes in 1.8, we're gonna re-assign it! */
			cape.pivotY = pivotY.get();
		}
	}
}
