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
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.vanilla.CrosshairHud;
import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ducks.Sneaky;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.WorldGeneratorType;
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

	@Shadow
	public abstract void renderWorld(float f, long l);

	@Shadow
	private long lastWorldRenderTime;

	@Shadow
	private float viewDistance;

	@Unique
	private float lastCameraY;

	@Unique
	private float cameraY;

	@Unique
	private float eyeHeight;

	@WrapOperation(method = "render(FJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderWorld(FJ)V"))
	private void axolotlclient$oldFramerateChunkRendering(GameRenderer instance, float f, long l, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldFramerateChunkRendering.get()) {
			/* the difference is most likely negligible here, but it's worth porting for accuracy */
			if (minecraft.isFramerateValid()) {
				renderWorld(f, lastWorldRenderTime + 1000000000 / minecraft.options.fpsLimit);
			} else {
				renderWorld(f, 0L);
			}
		} else {
			original.call(instance, f, l);
		}
	}

	@Inject(method = "setupCamera", at = @At("HEAD"))
	protected void axolotlclient$lerpCamera(float partialTicks, int pass, CallbackInfo ci) {
		/* WorldRenderer#setupRender is where the position of the player is applied handled */
		/* but we should apply everything here... it's easier :p */
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
		Entity camera = minecraft.getCamera();
		if ((OldAnimationsConfig.instance.smoothSneaking.get() || OldAnimationsConfig.instance.slowUpSneak.get())) {
			float eyeHeight = camera.isSneaking() ? 1.54F : 1.62F;
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
		BlockPos pos = OldAnimationsConfig.instance.oldFogGrayScale.get() ? new BlockPos(camera.getEyePosition(1.0F)) : new BlockPos(camera);
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

	@ModifyExpressionValue(method = "shouldRenderBlockOutline", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/player/PlayerAbilities;canModifyWorld:Z"))
	private boolean axolotlclient$alwaysShowOutline(boolean original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.alwaysShowOutline.get()) {
			return true;
		}
		return original;
	}

	@WrapOperation(method = "setupCamera", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V", remap = false))
	private void axolotlclient$increaseWorldDepth(float fovy, float aspect, float zNear, float zFar, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.skyAndCloudPerspective.get()) {
			/* 14w30b and MC-63179 */
			/* the following injections are all part of the same feature */
			/* for some reason, using a slice wasn't working so i had to manually inject with the ordinals... sigh... */
			/* this feature looks absolutely horrid, but that's just how early minecraft was... lmfao */
			zFar = viewDistance * 2.0F;
		}
		original.call(fovy, aspect, zNear, zFar);
	}

	@WrapWithCondition(method = "render(IFJ)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;matrixMode(I)V", ordinal = 0))
	private boolean axolotlclient$disableMatrixMode(int i) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skyAndCloudPerspective.get();
	}
	@WrapWithCondition(method = "render(IFJ)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;matrixMode(I)V", ordinal = 1))
	private boolean axolotlclient$disableMatrixMode2(int i) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skyAndCloudPerspective.get();
	}
	@WrapWithCondition(method = "render(IFJ)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;matrixMode(I)V", ordinal = 2))
	private boolean axolotlclient$disableMatrixMode3(int i) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skyAndCloudPerspective.get();
	}
	@WrapWithCondition(method = "render(IFJ)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;matrixMode(I)V", ordinal = 3))
	private boolean axolotlclient$disableMatrixMode4(int i) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skyAndCloudPerspective.get();
	}

	@WrapWithCondition(method = "render(IFJ)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;loadIdentity()V", ordinal = 0))
	private boolean axolotlclient$dontLoadIdentity() {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skyAndCloudPerspective.get();
	}
	@WrapWithCondition(method = "render(IFJ)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;loadIdentity()V", ordinal = 1))
	private boolean axolotlclient$dontLoadIdentity2() {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skyAndCloudPerspective.get();
	}

	@WrapWithCondition(method = "render(IFJ)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V", remap = false))
	private boolean axolotlclient$disablePerspective(float fovy, float aspect, float zNear, float zFar) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skyAndCloudPerspective.get();
	}

	@WrapWithCondition(method = "renderClouds", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;matrixMode(I)V"))
	private boolean axolotlclient$disableMatrixMode5(int i) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skyAndCloudPerspective.get();
	}

	@WrapWithCondition(method = "renderClouds", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;loadIdentity()V"))
	private boolean axolotlclient$dontLoadIdentity3() {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skyAndCloudPerspective.get();
	}

	@WrapWithCondition(method = "renderClouds", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V", remap = false))
	private boolean axolotlclient$disablePerspective2(float fovy, float aspect, float zNear, float zFar) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.skyAndCloudPerspective.get();
	}

	@ModifyExpressionValue(method = "renderFog", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/render/GameRenderer;viewDistance:F", ordinal = 1))
	private float axolotlclient$renderVoidFog(float original, @Local(argsOnly = true) int i, @Local(argsOnly = true) float f) {
		/* void fog logic taken straight from 1.7 */
		float gx = original;
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.voidFog.get()) {
			Entity entity = minecraft.getCamera();
			Dimension dimension = minecraft.world.dimension;
			if (i == 0 &&
				/* 1.7's hasFog() method */
				((DimensionAccessor) dimension).getGeneratorType() != WorldGeneratorType.FLAT && !dimension.isDark()) {
				double d = ((entity.getLightLevel(f) & 15728640) >> 20) / 16.0 + (entity.prevTickY + (entity.y - entity.prevTickY) * f + 4.0) / 32.0;
				if (d < 1.0) {
					if (d < 0.0) {
						d = 0.0;
					}
					d *= d;
					float h = 100.0F * (float) d;
					if (h < 5.0F) {
						h = 5.0F;
					}
					if (gx > h) {
						gx = h;
					}
				}
			}
		}
		/* welcome back my friend */
		return gx;
	}

	@Unique
	private static float lerp(float delta, float start, float end) { /* taken straight from modern minecraft */
		return start + delta * (end - start);
	}

	@Unique
	private boolean axolotlclient$isEitherSneakOptionEnabled() {
		/* if neither of the sneaking options are selected, we might as well just use the original eyeheight */
		return OldAnimationsConfig.isEnabled() && (OldAnimationsConfig.instance.smoothSneaking.get() || OldAnimationsConfig.instance.slowUpSneak.get());
	}

	@Override
	public float axolotlclient$getEyeHeight() {
		return eyeHeight;
	}
}
