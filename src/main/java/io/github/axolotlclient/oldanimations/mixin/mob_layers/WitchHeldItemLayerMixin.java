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
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ItemUtil;
import net.minecraft.client.render.ItemInHandRenderer;
import net.minecraft.client.render.entity.layer.WitchItemInHandLayer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WitchItemInHandLayer.class)
public class WitchHeldItemLayerMixin {
	/* each modified value was adapted painstakingly from 1.7 */

	@WrapOperation(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;translatef(FFF)V", ordinal = 2))
	private void axolotlclient$oldWitchLayerTransform(float f, float g, float h, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			g = 0.1875F;
			h = -0.3125F;
		}
		original.call(f, g, h);
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=30.0"))
	private float axolotlclient$oldWitchLayerTransform2(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return 20.0F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=-5.0"))
	private float axolotlclient$oldWitchLayerTransform3(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return 45.0F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=-0.125", ordinal = 0))
	private float axolotlclient$oldWitchLayerTransform4(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return 0.3125F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=-45.0"))
	private float axolotlclient$oldWitchLayerTransform5(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return -20.0F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=-20.0", ordinal = 1))
	private float axolotlclient$oldWitchLayerTransform6(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return 45.0F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=-0.0625", ordinal = 1))
	private float axolotlclient$oldWitchLayerTransform7(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return -0.125F;
		}
		return original;
	}

	@WrapWithCondition(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;translatef(FFF)V", ordinal = 5))
	private boolean axolotlclient$wrapWitchLayerTranslate(float f, float g, float h) {
		return false;
	}

	@ModifyArg(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;rotatef(FFFF)V", ordinal = 7), index = 1)
	private float axolotlclient$oldWitchLayerTransform8(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return -100.0F;
		}
		return original;
	}

	@ModifyArg(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;rotatef(FFFF)V", ordinal = 8), index = 1)
	private float axolotlclient$oldWitchLayerTransform9(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return -45.0F;
		}
		return original;
	}

	@WrapOperation(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;translatef(FFF)V", ordinal = 6))
	private void axolotlclient$oldWitchLayerTransform10(float f, float g, float h, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			f = 0.25F;
			h = -0.1875F;
		}
		original.call(f, g, h);
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=0.875"))
	private float axolotlclient$oldWitchLayerTransform11(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return 0.375F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=-20.0", ordinal = 2))
	private float axolotlclient$oldWitchLayerTransform12(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return 60.0F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=-60.0"))
	private float axolotlclient$oldWitchLayerTransform13(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return -90.0F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "CONSTANT", args = "floatValue=-30.0"))
	private float axolotlclient$oldWitchLayerTransform14(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get()) {
			return 20.0F;
		}
		return original;
	}

	@WrapOperation(method = "render(Lnet/minecraft/entity/living/mob/monster/WitchEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ItemInHandRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$disableResourcePackTransformations(ItemInHandRenderer instance, LivingEntity entity, ItemStack item, ModelTransformations.Type transform, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get() && !ItemUtil.isCustomRenderer(item)) {
			transform = ModelTransformations.Type.NONE;
		}
		original.call(instance, entity, item, transform);
	}
}
