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
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.ducks.Sneaky;
import io.github.axolotlclient.oldanimations.util.DamageTint;
import io.github.axolotlclient.oldanimations.util.IDamageTint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin implements IDamageTint {

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

		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.damageColor.get()) {
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
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.damageColor.get()) {
			return false;
		}
		return original.call(instance, livingEntity, f);
	}

	@WrapOperation(method = "renderLayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupOverlayColor(Lnet/minecraft/entity/living/LivingEntity;FZ)Z"))
	private boolean axolotlclient$cancelDamageBrightness2(LivingEntityRenderer<?> instance, LivingEntity livingEntity, float f, boolean bl, Operation<Boolean> original) {
		/* cancel layer damage tint */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.damageColor.get()) {
			return false;
		}
		return original.call(instance, livingEntity, f, bl);
	}

	@Inject(method = "render(Lnet/minecraft/entity/living/LivingEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;translatef(FFF)V"))
    private void axolotlclient$addSneakingTranslation(LivingEntity livingEntity, double d, double e, double f, float g, float h, CallbackInfo ci) {
        /* in order to match 1.7, we need to elevate the player model while sneaking */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSmoothSneaking.get() &&
			livingEntity instanceof PlayerEntity && livingEntity.getName().equals(Minecraft.getInstance().player.getName())) {
			if (livingEntity.isSneaking()) {
				/* we need to remove the already existing sneaking offset */
				/* which is present in BiPedModel#render, PlayerEntityModel#render, and related classes */
				GlStateManager.translatef(0.0F, -0.2F, 0.0F);
			}
			float eyeHeightOffset = 1.62F - ((Sneaky) Minecraft.getInstance().gameRenderer).axolotlclient$getEyeHeight();
			/* the elevation will be the difference between the player's sneaking eyeheight and their actual eyeheight (1.62 meters) */
			/* the player model should now move 1:1 with the crosshair */
			GlStateManager.translatef(0.0F, eyeHeightOffset, 0.0F);
		}
    }

	@ModifyExpressionValue(method = "setupOverlayColor(Lnet/minecraft/entity/living/LivingEntity;FZ)Z", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/living/LivingEntity;hurtTime:I"))
	private int axolotlclient$oldDamageTick(int original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDamageTick.get()) {
			return Math.max(original - 1, 0);
		}
		return original;
	}

	@Override
	public boolean axolotlclient$setupOverlayColor(@NotNull LivingEntity livingEntity, float partialTicks) {
		/* trick to ensure the brightnessBuffer is updated*/
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
