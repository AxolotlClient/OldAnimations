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

package io.github.axolotlclient.oldanimations.mixin.mob_layers;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.render.entity.layer.WornSkullLayer;
import net.minecraft.entity.living.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WornSkullLayer.class)
public class WornSkullLayerMixin {

	//TODO: Figure out how to color the skull layer with the 1.7 logic??
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;isSneaking()Z"))
	private boolean axolotlclient$disableSneakTranslation(LivingEntity instance, Operation<Boolean> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSneaking.get()) {
			/* we need to remove the sneaking offset since we will be using our own */
			return false;
		}
		return original.call(instance);
	}

	@ModifyVariable(method = "render", at = @At("STORE"))
	private boolean axolotlclient$disableHumanoidCheck(boolean value) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.skullLayerRendering.get()) {
			/* goodbye villager-specific worn skull rendering :p */
			return false;
		}
		return value;
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;isBaby()Z"))
	private boolean axolotlclient$disableBabyCheck(LivingEntity instance, Operation<Boolean> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.skullLayerRendering.get()) {
			/* the baby will NOT be getting special treatment */
			return false;
		}
		return original.call(instance);
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;rotatef(FFFF)V"), index = 0)
	private float axolotlclient$oldBlockRotation(float f) {
		/* taken from 1.7 */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.skullLayerRendering.get()) {
			return 90;
		}
		return f;
	}

	@ModifyExpressionValue(method = "render", at = @At(value = "CONSTANT", args = "floatValue=1.1875"))
	private float axolotlclient$useOldScale(float original) {
		/* taken from 1.7 */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.skullLayerRendering.get()) {
			return 1.0625F;
		}
		return original;
	}

	@Inject(method = "colorsWhenDamaged", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$removeDamageColor(CallbackInfoReturnable<Boolean> callback) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.noSkullLayerDamageTint.get()) {
			/* disables coloring the second layer just like 1.7 */
			callback.setReturnValue(false);
		}
	}
}
