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
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.entity.layer.CapeLayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {

	@Dynamic("Translation added by OptiFine")
	@WrapWithCondition(method = "render(Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;translatef(FFF)V", ordinal = 1), require = 0)
	private boolean axolotlclient$disableOptiFineTranslation(float f, float g, float h) {
		/* optifine attemps to fix 1.8's weird cape position... that's all well and good, but let's just disable that when we use our own :p */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.thirdPersonSneaking.get();
	}

	@Dynamic("Clamping value added by OptiFine")
	@ModifyExpressionValue(method = "render(Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=165.0"), require = 0)
	private float axolotlclient$disableOptiFineClamp(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSneaking.get()) {
			/* optifine attemps to clamp the cape's physics... nuh uh */
			return Float.MAX_VALUE;
		}
		return original;
	}

	@Dynamic("Clamping value added by OptiFine")
	@ModifyExpressionValue(method = "render(Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=-5.0"), require = 0)
	private float axolotlclient$disableOptiFineClamp2(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSneaking.get()) {
			/* optifine attemps to clamp the cape's physics... nuh uh */
			return Float.MIN_VALUE;
		}
		return original;
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;FFFFFFF)V", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;prevY:D"))
	private double axolotlclient$includeSneakOffset$PrevY(double original, @Local(argsOnly = true) ClientPlayerEntity clientPlayerEntity) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSneaking.get()) {
			/* sneaking moves the eyeheight down by 0.08 units... we must also make sure this applies to other renderings */
			original += clientPlayerEntity.isSneaking() ? -0.08F : 0.0F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;FFFFFFF)V", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;y:D"))
	private double axolotlclient$includeSneakOffset$Y(double original, @Local(argsOnly = true) ClientPlayerEntity clientPlayerEntity) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSneaking.get()) {
			/* sneaking moves the eyeheight down by 0.08 units... we must also make sure this applies to other renderings */
			original += clientPlayerEntity.isSneaking() ? -0.08F : 0.0F;
		}
		return original;
	}
}
