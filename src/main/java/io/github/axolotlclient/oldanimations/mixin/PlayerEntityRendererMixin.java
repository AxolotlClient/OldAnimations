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
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.ModelPart;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

	@Shadow
	public abstract PlayerEntityModel getModel();

	@Inject(method = "setModelPose", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/render/entity/model/PlayerEntityModel;leftArmPose:I"))
	private void axolotlclient$reAssignShownLayer(AbstractClientPlayerEntity abstractClientPlayerEntity, CallbackInfo ci) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().oldSkinRendering.get()) {
			PlayerEntityModel playerModel = getModel();
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

	@Inject(method = {"renderLeftArm", "renderRightArm"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;setModelPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)V", shift = At.Shift.AFTER))
	private void axolotlclient$dontSetModelStatus(AbstractClientPlayerEntity abstractClientPlayerEntity, CallbackInfo ci) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().fixArmItemRotation.get()) {
			getModel().rightArmPose = 0;
		}
	}
}
