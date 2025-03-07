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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements Sneaky {

	@Shadow
	private MinecraftClient client;

	@Unique
	private float lastCameraY;

	@Unique
	private float cameraY;

	@Unique
	private float eyeHeight;

	@Inject(method = "setupCamera", at = @At("HEAD"))
	protected void axolotlclient$lerpCamera(float partialTicks, int pass, CallbackInfo ci) {
		if (isSneakingEnabled()) eyeHeight = lerp(partialTicks, lastCameraY, cameraY);
	}

	@ModifyVariable(method = "transformCamera", at = @At(value = "STORE"), ordinal = 1)
	private float axolotlclient$useLerpEyeHeight(float eyeHeight) {
		return isSneakingEnabled() ? axolotlclient$getEyeHeight() : eyeHeight;
	}

	@ModifyArg(method = "renderDebugCrosshair", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;translate(FFF)V"), index = 1)
	public float axolotlclient$useLerpEyeHeight_Debug(float x) {
		return isSneakingEnabled() ? axolotlclient$getEyeHeight() : x;
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;updateHeldItems()V"))
	private void axolotlclient$updateCameraY(CallbackInfo ci) {
		if (!isSneakingEnabled()) { return; }
		Entity entity = client.getCameraEntity();
		float eyeHeight = entity.getEyeHeight();
		lastCameraY = cameraY;
		if (eyeHeight < cameraY)
			cameraY = eyeHeight;
		else
			cameraY += (eyeHeight - cameraY) * 0.5f;
	}

	@Redirect(
		method = "bobViewWhenHurt",
		at = @At(
			value = "FIELD",
			opcode = Opcodes.GETFIELD,
			target = "Lnet/minecraft/entity/LivingEntity;hurtTime:I"
		)
	)
	private int axolotlclient$oldTickDelay(LivingEntity instance) {
		int original = instance.hurtTime;
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().oldRenderTickDelay.get()) {
			return Math.max(original - 1, 0);
		}
		return original;
	}

	@Unique
	private static float lerp(float delta, float start, float end) {
		return start + delta * (end - start);
	}

	@Unique
	private static boolean isSneakingEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().sneaking.get();
	}

	@Override
	public float axolotlclient$getEyeHeight() {
		return eyeHeight;
	}
}
