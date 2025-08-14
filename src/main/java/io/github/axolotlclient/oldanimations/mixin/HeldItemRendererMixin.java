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

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ItemUtil;
import net.minecraft.client.render.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
	@Shadow
	@Final
	private ItemRenderer renderer;

	@Shadow
	private ItemStack item;

	@Shadow
	private int selectedSlot;

	@Unique
	private Float axolotlclient$h;

	@ModifyVariable(method = "renderInFirstPerson", at = @At("STORE"), index = 4)
	private float axolotlclient$captureLocalH(float value) {
		axolotlclient$h = value; /* swing progress */
		return value;
	}

	@ModifyArg(method = "renderInFirstPerson",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;applyFirstPersonTransform(FF)V"),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;applyConsuming(Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;F)V"),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;applyBowNocking(FLnet/minecraft/client/entity/living/player/ClientPlayerEntity;)V")
		),
		index = 1
	)
	public float axolotlclient$allowUseAndSwing(float g) {
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.blockHitting.get() ? axolotlclient$h : g;
	}

	@Inject(method = "renderInFirstPerson", at = @At("TAIL"))
	private void axolotlclient$releaseCapturedLocal(float f, CallbackInfo ci) {
		axolotlclient$h = null; /* big brain time */
	}

	@WrapOperation(method = "applyBowNocking", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;scalef(FFF)V"))
	private void axolotlclient$applyBowTransform(float f, float g, float h, Operation<Void> original) {
		boolean isEnabled = OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldBowRotation.get();
		/* original transformations from 1.7 */
		if (isEnabled) {
			GlStateManager.rotatef(-335.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotatef(-50.0F, 0.0F, 1.0F, 0.0F);
		}
		original.call(f, g, h);
		if (isEnabled) {
			GlStateManager.rotatef(50.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(335.0F, 0.0F, 0.0F, 1.0F);
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderHeldItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$applyHeldItemTransforms(LivingEntity livingEntity, ItemStack itemStack, ModelTransformations.Type type, CallbackInfo ci) {
		if (!areItemPositionsEnabled() || ItemUtil.isBlacklisted(itemStack)) return;
		if (renderer.isGui3d(itemStack)) {
			/* blocks */
			GlStateManager.rotatef(90.0F + (ItemUtil.shouldRotateBlock(itemStack) ? 180.0F : 0.0F), 0.0F, 1.0F, 0.0F);
		} else {
			/* original transformations from 1.7 */
			GlStateManager.translatef(0.0F, -0.3F, 0.0F);
			GlStateManager.scalef(1.5F, 1.5F, 1.5F);
			GlStateManager.rotatef(50.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(335.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translatef(-0.9375F, -0.0625F, 0.0F);
			/* we need to adapt the 1.7 transformations to fit in 1.8 */
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(-0.5F, 0.5F, 0.03125F);
		}
	}

	@Inject(method = "renderInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$applyRodRotation(float partialTicks, CallbackInfo ci) {
		/* original transformation from 1.7 */
		if (OldAnimationsConfig.isEnabled() && (OldAnimationsConfig.instance.oldRodRotation.get() || OldAnimationsConfig.instance.disableResourcePackItemTransformations.get()) && item.getItem().shouldRotate()) {
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		}
	}

	@ModifyArg(method = "renderInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"), index = 2)
	private ModelTransformations.Type axolotlclient$changeTransformType(ModelTransformations.Type mode) {
		return areItemPositionsEnabled() && OldAnimationsConfig.instance.disableResourcePackItemTransformations.get() && !ItemUtil.isBlacklisted(item) ? ModelTransformations.Type.NONE : mode;
	}

	@Expression("? != null")
	@ModifyExpressionValue(method = "updateHeldItem", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
	private boolean axolotlclient$compareDamage(boolean original, @Local /* go away :( */ ItemStack itemStack) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.equipLogic.get()) {
			/* adapted from 1.7 */
			return original && itemStack != item && itemStack.getItem() == item.getItem() && itemStack.getDamage() == item.getDamage();
		}
		return original;
	}

	@ModifyExpressionValue(method = "updateHeldItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEqualForHoldAnimation(Lnet/minecraft/item/ItemStack;)Z"))
	private boolean axolotlclient$disableStackEquality(boolean original, @Local ItemStack itemStack) {
		/* adapted from 1.7 */
		return (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.equipLogic.get()) && original;
	}

	@ModifyVariable(method = "updateHeldItem", at = @At(value = "STORE", ordinal = 1), index = 3)
	private boolean axolotlclient$updateItemStack(boolean original, @Local ItemStack itemStack) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.equipLogic.get()) {
			/* adapted from 1.7 */
			item = itemStack;
			return false;
		}
		return original;
	}

	@ModifyVariable(method = "updateHeldItem", at = @At(value = "STORE", ordinal = 3), index = 3)
	private boolean axolotlclient$makeAssignmentRedundant(boolean original, @Local PlayerEntity playerEntity, @Local ItemStack itemStack) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.equipLogic.get()) {
			/* adapted from 1.7 */
			return selectedSlot != playerEntity.inventory.selectedSlot || itemStack != item;
		}
		return original;
	}

	@Unique
	private static boolean areItemPositionsEnabled() {
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get();
	}
}
