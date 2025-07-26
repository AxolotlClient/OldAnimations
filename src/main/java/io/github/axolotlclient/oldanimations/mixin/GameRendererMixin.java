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
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.vanilla.CrosshairHud;
import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.ducks.Sneaky;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.math.BlockPos;
import org.objectweb.asm.Opcodes;
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

	@Shadow
	private float oldFogGrayScale;

	@Unique
	private float lastCameraY;

	@Unique
	private float cameraY;

	@Unique
	private float eyeHeight;

	@Inject(method = "setupCamera", at = @At("HEAD"))
	protected void axolotlclient$lerpCamera(float partialTicks, int pass, CallbackInfo ci) {
		/* eye height is interpolated between the last and current camera Y positions */
		if (!OldAnimationsConfig.isEnabled()) return;
		if (OldAnimationsConfig.instance.smoothSneaking.get()) {
			eyeHeight = lerp(partialTicks, lastCameraY, cameraY);
		} else if (OldAnimationsConfig.instance.slowUpSneak.get()) {
			eyeHeight = cameraY;
		}
	}

	@ModifyVariable(method = "transformCamera", at = @At(value = "STORE"), ordinal = 1)
	private float axolotlclient$useLerpEyeHeight(float eyeHeight, @Local Entity entity) {
		if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
			/* just use the 1.8 eyeheight while sleeping :p */
			return eyeHeight;
		}
		return axolotlclient$isEitherSneakOptionEnabled() ? axolotlclient$getEyeHeight() : eyeHeight;
	}

	@ModifyArg(method = "renderAxisIndicators", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;translatef(FFF)V"), index = 1)
	private float axolotlclient$useLerpEyeHeight_Debug(float x) {
		return axolotlclient$isEitherSneakOptionEnabled() ? axolotlclient$getEyeHeight() : x; /* debug crosshair parity */
	}

	@WrapOperation(method = "renderAxisIndicators", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;hasReducedDebugInfo()Z"))
	private boolean axolotlclient$disableAxisIndicator(LocalClientPlayerEntity instance, Operation<Boolean> original) {
		boolean isCustomCrosshair = OldAnimations.AXOLOTLCLIENT && HudManager.getInstance().get(CrosshairHud.ID).isEnabled();
		return (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.alwaysShowCrosshair.get() && !isCustomCrosshair) || original.call(instance);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;updateHeldItem()V")) /* placed below null check */
	private void axolotlclient$onTick(CallbackInfo ci) {
		/* updates the current eye height */
		if (!OldAnimationsConfig.isEnabled()) {
			return;
		}
		Entity entity = minecraft.getCamera();
		if ((OldAnimationsConfig.instance.smoothSneaking.get() || OldAnimationsConfig.instance.slowUpSneak.get())) {
			float eyeHeight = entity.getEyeHeight();
			lastCameraY = cameraY;
			if (OldAnimationsConfig.instance.slowUpSneak.get() && eyeHeight > cameraY) {
				/* the value is 0.4f in 1.7, however the math that is applied, when rearranged, */
				/* will yield 0.6f when adapted to 1.13+ sneaking logic */
				/* that being said, 1.13 uses 0.5f which is a tiny bit slower than 1.7! */
				/* turns out TheKodeToad was right the whole time... damn */
				cameraY += (eyeHeight - cameraY) * 0.6f;
			} else {
				cameraY = eyeHeight;
			}
		}

		/* MC-51150 is already fixed by optifine lmfaoo... */
		/* in order to actually give players an option to toggle it, */
		/* i think this overwrite is warranted :)  */
		BlockPos pos = OldAnimationsConfig.instance.oldFogGrayScale.get() ? new BlockPos(minecraft.getCamera().getEyePosition(1.0F)) : new BlockPos(minecraft.getCamera());
		float f = minecraft.world.getBrightness(pos);
		float g = (float) minecraft.options.viewDistance / 16.0F;
		float h = f * (1.0F - g) + g;
		oldFogGrayScale = oldFogGrayScale + (h - oldFogGrayScale) * 0.1F;
	}

	@ModifyExpressionValue(method = "applyHurtCam", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/living/LivingEntity;hurtTime:I"))
	private int axolotlclient$oldDamageTick(int original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDamageTick.get()) {
			return Math.max(original - 1, 0);
		}
		return original;
	}

	@Unique
	private static float lerp(float delta, float start, float end) { /* taken straight from modern minecraft */
		return start + delta * (end - start);
	}

	@Unique
	private boolean axolotlclient$isEitherSneakOptionEnabled() {
		/* if neither of the sneaking options are selection, we might as well just use the original eyeheight */
		return OldAnimationsConfig.isEnabled() && (OldAnimationsConfig.instance.smoothSneaking.get() || OldAnimationsConfig.instance.slowUpSneak.get());
	}

	@Override
	public float axolotlclient$getEyeHeight() {
		return eyeHeight;
	}
}
