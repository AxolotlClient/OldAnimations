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

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.utils.ItemBlacklist;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class ItemRendererMixin {
	@Shadow
	@Final
	private ItemRenderer itemRenderer;

	@Shadow
	private ItemStack mainHand;

	@Unique
	private Float h;

	@ModifyVariable(method = "renderArmHoldingItem", at = @At("STORE"), index = 4)
	private float axolotlclient$captureLocalH(float value) {
		h = value; /* swing progress */
		return value;
	}

	@ModifyArg(method = "renderArmHoldingItem",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipAndSwingOffset(FF)V"),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEatOrDrinkTransformation(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)V"),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyBowTransformation(FLnet/minecraft/client/network/AbstractClientPlayerEntity;)V")
		),
		index = 1
	)
	public float axolotlclient$allowUseAndSwing(float g) {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().blocking.get() ? h : g;
	}

	@Inject(method = "renderArmHoldingItem", at = @At("TAIL"))
	private void axolotlclient$releaseCapturedLocal(float f, CallbackInfo ci) {
		h = null; /* big brain time */
	}

	@Inject(method = "applyBowTransformation", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;scale(FFF)V"))
	private void axolotlclient$preBowTransform(float f, AbstractClientPlayerEntity abstractClientPlayerEntity, CallbackInfo ci) {
		if (areItemPositionsEnabled()) {
			/* original transformations from 1.7 */
			GlStateManager.rotate(-335.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(-50.0F, 0.0F, 1.0F, 0.0F);
		}
	}

	@Inject(method = "applyBowTransformation", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;scale(FFF)V", shift = At.Shift.AFTER))
	private void axolotlclient$postBowTransform(float f, AbstractClientPlayerEntity abstractClientPlayerEntity, CallbackInfo ci) {
		if (areItemPositionsEnabled()) {
			/* original transformations from 1.7 */
			GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(335.0F, 0.0F, 0.0F, 1.0F);
		}
	}

	@Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;)V"))
	private void axolotlclient$applyHeldItemTransforms(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Mode mode, CallbackInfo ci) {
		if (!areItemPositionsEnabled()) return;
		if (itemRenderer.hasDepth(itemStack) || ItemBlacklist.isPresent(itemStack)) return;
		/* original transformations from 1.7 */
		GlStateManager.translate(0.0F, -0.3F, 0.0F);
		GlStateManager.scale(1.5F, 1.5F, 1.5F);
		GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(335.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(-0.9375F, -0.0625F, 0.0F);
		/* we need to adapt the 1.7 transformations to fit in 1.8 */
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, 0.5F, 0.03125F);
	}

	@Inject(method = "renderArmHoldingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;)V"))
	private void axolotlclient$applyRodRotation(float partialTicks, CallbackInfo ci) {
		/* original transformation from 1.7 */
		if (areItemPositionsEnabled() && mainHand.getItem().shouldRotate())
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
	}

	@ModifyArg(method = "renderArmHoldingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;)V"), index = 2)
	private ModelTransformation.Mode axolotlclient$changeTransformType(ModelTransformation.Mode mode) {
		return areItemPositionsEnabled() && !ItemBlacklist.isPresent(mainHand) ? ModelTransformation.Mode.NONE : mode;
	}

	@Unique
	private static boolean areItemPositionsEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().itemPositions.get();
	}
}
