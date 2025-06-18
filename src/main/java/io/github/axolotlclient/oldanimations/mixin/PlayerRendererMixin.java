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

import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.oldanimations.OldAnimations;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.entity.PlayerModel;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {

	@Shadow
	public abstract PlayerModel getModel();

	@Inject(method = "setModelStatus", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/render/model/entity/PlayerModel;leftHandItemId:I"))
	private void axolotlclient$reAssignShownLayer(ClientPlayerEntity entity, CallbackInfo ci) {
		if (OldAnimations.isEnabled() && OldAnimations.getInstance().disableSkinLayers.get()) {
			/* 1.7 doesn't have any skin layers except for the headwear */
			PlayerModel playerModel = getModel();
			ModelPart[] wearLayers = {
				playerModel.jacket,
				playerModel.leftPants,
				playerModel.rightPants,
				playerModel.leftSleeve,
				playerModel.rightSleeve
			};
			for (ModelPart layer : wearLayers) {
				layer.visible = false;
			}
		}
	}

	@Inject(method = {"renderPlayerLeftHandModel", "renderPlayerRightHandModel"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerRenderer;setModelStatus(Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;)V", shift = At.Shift.AFTER))
	private void axolotlclient$dontSetModelStatus(ClientPlayerEntity player, CallbackInfo ci, @Local PlayerModel playerModel) {
		if (OldAnimations.isEnabled() && OldAnimations.getInstance().oldPickupArm.get()) {
			/* don't apply third person arm rotation to first person */
			playerModel.rightHandItemId = 0;
		}
	}
}
