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
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.DamageTint;
import io.github.axolotlclient.oldanimations.util.IDamageTint;
import io.github.axolotlclient.oldanimations.util.PlayerUtil;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.CreeperEntity;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> extends EntityRenderer<T> implements IDamageTint {

	protected LivingEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	@Shadow
	protected abstract boolean setupOverlayColor(LivingEntity entity, float tickDelta, boolean bl);

	@Shadow
	protected abstract void tearDownOverlayColor();

	@Shadow
	protected abstract int getOverlayColor(LivingEntity entity, float f, float timeDelta);

	@Shadow
	protected FloatBuffer tintBuffer;

	@Unique
	private float axolotlclient$h = 0.0F;

	@Inject(method = "render(Lnet/minecraft/entity/living/LivingEntity;DDDFF)V", at = @At("HEAD"))
	private void axolotlclient$capturePartialTicks(LivingEntity livingEntity, double d, double e, double f, float g, float h, CallbackInfo ci) {
		axolotlclient$h = h;
	}

	@WrapOperation(method = "render(Lnet/minecraft/entity/living/LivingEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderHand(Lnet/minecraft/entity/living/LivingEntity;FFFFFF)V", ordinal = 1))
	private void axolotlclient$cancelDamageBrightness(LivingEntityRenderer<?> instance, LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, Operation<Void> original) {
		original.call(instance, livingEntity, f, g, h, i, j, k);

		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.damageTintColor.get()) {
			return;
		}

		if (axolotlclient$setupOverlayColor(livingEntity, axolotlclient$h)) {
			original.call(instance, livingEntity, f, g, h, i, j, k);
			DamageTint.unsetDamageTint();
		}
	}

	@WrapOperation(method = "render(Lnet/minecraft/entity/living/LivingEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupOverlayColor(Lnet/minecraft/entity/living/LivingEntity;F)Z"))
	private boolean axolotlclient$cancelDamageBrightness(LivingEntityRenderer<?> instance, LivingEntity livingEntity, float f, Operation<Boolean> original) {
		/* cancel model damage tint */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.damageTintColor.get()) {
			return false;
		}
		return original.call(instance, livingEntity, f);
	}

	@WrapOperation(method = "renderLayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupOverlayColor(Lnet/minecraft/entity/living/LivingEntity;FZ)Z"))
	private boolean axolotlclient$cancelDamageBrightness2(LivingEntityRenderer<?> instance, LivingEntity livingEntity, float f, boolean bl, Operation<Boolean> original) {
		/* cancel layer damage tint */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.damageTintColor.get()) {
			return false;
		}
		return original.call(instance, livingEntity, f, bl);
	}

	//TODO: this can probably be moved somewhere else!
	@Inject(method = "render(Lnet/minecraft/entity/living/LivingEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;translatef(FFF)V"))
    private void axolotlclient$addTranslation(LivingEntity livingEntity, double d, double e, double f, float g, float h, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled()) {
			if (OldAnimationsConfig.instance.thirdPersonSneaking.get() && PlayerUtil.INSTANCE.isSelf(livingEntity)) {
				/* in order to match 1.7, we need to elevate the player model while sneaking */
				/* the elevation will be the difference between the player's sneaking eyeheight and their actual eyeheight (1.62 meters) */
				/* the player model should now move 1:1 with the crosshair */
				GlStateManager.translatef(0.0F, 1.62F - PlayerUtil.INSTANCE.getEyeHeight(), 0.0F);
			}

			if (OldAnimationsConfig.instance.creeperOffset.get() && livingEntity instanceof CreeperEntity) {
				/* vro was floating in 1.7 fr fr */
				/* this is not where the actual modification came from. in 1.7, */
				/* CreeperModel#<init> has an integer local holding the value of 4, whereas in 1.8, it's 6 */
				/* a 2 pixel difference. we can convert this value by doing 2 / 16 (texture width) to get the value 0.125 of a block */
				/* MC-3631 */
				GlStateManager.translatef(0.0F, -0.125F, 0.0F);
			}
		}
    }

	@ModifyExpressionValue(method = "setupOverlayColor(Lnet/minecraft/entity/living/LivingEntity;FZ)Z", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/living/LivingEntity;hurtTime:I"))
	private int axolotlclient$oldDamageTick(int original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDamageTick.get()) {
			return Math.max(original - 1, 0);
		}
		return original;
	}

	@ModifyExpressionValue(method = "setupOverlayColor(Lnet/minecraft/entity/living/LivingEntity;FZ)Z", at = @At(value = "CONSTANT", args = "floatValue=1.0", ordinal = 0))
	private float axolotlclient$damageTintLighting(float original, @Local(index = 4, ordinal = 1) float g) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDamageTintLighting.get()) {
			return g; /* this will basically make the tint influenced by lighting */
		}
		return original;
	}

	@ModifyArg(method = "renderNameTag(Lnet/minecraft/entity/living/LivingEntity;DDD)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;translatef(FFF)V", ordinal = 0), index = 1)
	private float axolotlclient$syncNameTag(float original, @Local(argsOnly = true) LivingEntity livingEntity) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSneaking.get() && PlayerUtil.INSTANCE.isSelf(livingEntity)) {
			/* we must ensurethe nametag is synced with the interpolated player model position */
			original += PlayerUtil.INSTANCE.getEyeHeight() - 1.62F;
		}
		return original;
	}

	@ModifyArg(method = "renderNameTag(Lnet/minecraft/entity/living/LivingEntity;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/entity/Entity;DDDLjava/lang/String;FD)V"), index = 2)
	private double axolotlclient$syncNameTag2(double original, @Local(argsOnly = true) LivingEntity livingEntity) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSneaking.get() && PlayerUtil.INSTANCE.isSelf(livingEntity)) {
			/* we must ensure the nametag is synced with the interpolated player model position once again */
			original = original + PlayerUtil.INSTANCE.getEyeHeight() - 1.62F;
		}
		return original;
	}

	@Override
	public boolean axolotlclient$setupOverlayColor(@NotNull LivingEntity livingEntity, float partialTicks) {
		/* trick to ensure the brightnessBuffer is updated */
		if (setupOverlayColor(livingEntity, partialTicks, true)) tearDownOverlayColor();
		/* if there are any performance issues, blame this */

		final float f = livingEntity.getBrightness(partialTicks);
		final int i = getOverlayColor(livingEntity, f, partialTicks);
		final boolean flag = (i >> 24 & 0xFF) > 0;

		int hurtTime = livingEntity.hurtTime;
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDamageTick.get()) {
			hurtTime = Math.max(hurtTime - 1, 0);
		}
		final boolean flag1 = hurtTime > 0 || livingEntity.deathTime > 0;

		if (!flag && !flag1) {
			return false;
		} else {
			DamageTint.setDamageTint(tintBuffer);
			return true;
		}
	}
}
