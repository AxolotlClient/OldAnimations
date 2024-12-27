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
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements Sneaky {

	@Shadow
	/* why you not final :( */
	private Minecraft minecraft;

	@Unique
	private float lastCameraY;

	@Unique
	private float cameraY;

	@Unique
	private float eyeHeight;

	@Inject(method = "setupCamera", at = @At("HEAD"))
	protected void axolotlclient$lerpCamera(float partialTicks, int pass, CallbackInfo ci) {
		/* eye height is interpolated between the last and current camera Y positions */
		if (isSneakingEnabled()) eyeHeight = lerp(partialTicks, lastCameraY, cameraY);
	}

	@ModifyVariable(method = "transformCamera", at = @At(value = "STORE"), ordinal = 1)
	private float axolotlclient$useLerpEyeHeight(float eyeHeight) {
		return isSneakingEnabled() ? axolotlclient$getEyeHeight() : eyeHeight; /* player eye height */
	}

	@ModifyArg(method = "renderAxisIndicators", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;translatef(FFF)V"), index = 1)
	public float axolotlclient$useLerpEyeHeight_Debug(float x) {
		return isSneakingEnabled() ? axolotlclient$getEyeHeight() : x; /* debug crosshair parity */
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;updateHeldItem()V")) /* placed below null check */
	private void axolotlclient$updateCameraY(CallbackInfo ci) {
		/* updates the current eye height */
		if (!isSneakingEnabled()) {
			return;
		}
		Entity entity = minecraft.getCamera();
		float eyeHeight = entity.getEyeHeight();
		lastCameraY = cameraY;
		if (eyeHeight < cameraY)
			cameraY = eyeHeight;
		else
			cameraY += (eyeHeight - cameraY) * 0.5f;
	}

	@Unique
	private static float lerp(float delta, float start, float end) { /* taken straight from modern minecraft */
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
