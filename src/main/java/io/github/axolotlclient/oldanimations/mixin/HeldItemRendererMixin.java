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
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ItemUtil;
import io.github.axolotlclient.oldanimations.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.ItemInHandRenderer;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class /* C_7203066 */)
public abstract class HeldItemRendererMixin {
	@Shadow
	@Final
	private ItemRenderer renderer;

	@Shadow
	private ItemStack itemInHand;

	@Shadow
	private int selectedSlot;

	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyArg(method = "renderInFirstPerson",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ItemInHandRenderer;applyFirstPersonTransform(FF)V"),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ItemInHandRenderer;applyConsuming(Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;F)V"),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ItemInHandRenderer;applyBowNocking(FLnet/minecraft/client/entity/living/player/ClientPlayerEntity;)V")
		),
		index = 1
	)
	public float axolotlclient$allowUseAndSwing(float g, @Local(index = 1, argsOnly = true) float f) {
		/* we are using the swing progress. the local is culled (?) before we can use it */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.blockHitting.get() ? minecraft.player.getAttackAnimationProgress(f) : g;
	}

	@WrapOperation(method = "applyBowNocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;scalef(FFF)V"))
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

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemRenderer;renderItemInHand(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$applyHeldItemTransforms(LivingEntity livingEntity, ItemStack itemStack, ModelTransformations.Type type, CallbackInfo ci) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.itemPositions.get() || ItemUtil.isCustomRenderer(itemStack)) {
			return;
		}
		if (renderer.isGui3d(itemStack)) {
//			Item item = itemStack.getItem();
//			if (item instanceof BlockItem) {
//				Block block = Block.byItem(item);
//				if (block instanceof FenceBlock) {
//					GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
//				}
//			}
		} else {
			/* original transformations from 1.7 */
			GlStateManager.translatef(0.0F, -0.3F, 0.0F);
			GlStateManager.scalef(1.5F, 1.5F, 1.5F);
			GlStateManager.rotatef(50.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(335.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translatef(-0.9375F, -0.0625F, 0.0F);
			/* we need to adapt the 1.7 transformations to fit in 1.8 */
			/* these translations are taken from snapshot 14w25a which introduced item/block models */
			GlStateManager.translatef(0.5F, 0.5F, 0.25F);
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(0.0F, 0.0F, 0.28125F);
		}
	}

	@Inject(method = "renderInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ItemInHandRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$applyRodRotation(float partialTicks, CallbackInfo ci) {
		/* original transformation from 1.7 */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get() && itemInHand.getItem().shouldRotate()) {
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		}
	}

	//TODO: We can simplify this by moving it to the source
	@WrapOperation(method = "renderInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ItemInHandRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$disableResourcePackTransformations(ItemInHandRenderer instance, LivingEntity livingEntity, ItemStack itemStack, ModelTransformations.Type type, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get() && !ItemUtil.isCustomRenderer(itemStack)) {
			type = ModelTransformations.Type.NONE;
		}
		original.call(instance, livingEntity, itemStack, type);
	}

	@Expression("? != null")
	@ModifyExpressionValue(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
	private boolean axolotlclient$compareDamage(boolean original, @Local /* go away :( */ ItemStack itemStack) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.equipLogic.get()) {
			/* adapted from 1.7 */
			return original && itemStack != itemInHand && itemStack.getItem() == itemInHand.getItem() && itemStack.getDamage() == itemInHand.getDamage();
		}
		return original;
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEqualForHoldAnimation(Lnet/minecraft/item/ItemStack;)Z"))
	private boolean axolotlclient$disableStackEquality(boolean original, @Local ItemStack itemStack) {
		/* adapted from 1.7 */
		return (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.equipLogic.get()) && original;
	}

	@ModifyVariable(method = "tick", at = @At(value = "STORE", ordinal = 1), index = 3)
	private boolean axolotlclient$updateItemStack(boolean original, @Local ItemStack itemStack) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.equipLogic.get()) {
			/* adapted from 1.7 */
			itemInHand = itemStack;
			return false;
		}
		return original;
	}

	@ModifyVariable(method = "tick", at = @At(value = "STORE", ordinal = 3), index = 3)
	private boolean axolotlclient$makeAssignmentRedundant(boolean original, @Local PlayerEntity playerEntity, @Local ItemStack itemStack) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.equipLogic.get()) {
			/* adapted from 1.7 */
			return selectedSlot != playerEntity.inventory.selectedSlot || itemStack != itemInHand;
		}
		return original;
	}

	@WrapOperation(method = "setHandLightColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;getEyeHeight()F"))
	private float axolotlclient$useLerpEyeHeight_Hand(ClientPlayerEntity instance, Operation<Float> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.smoothSneaking.get()) {
			/* not sure if this will even do anything significant lol */
			return PlayerUtil.INSTANCE.getEyeHeight();
		}
		return original.call(instance);
	}

	@WrapMethod(method = "renderLeftArm")
	private void axolotlclient$wrapLeftMapArm(PlayerRenderer playerRenderer, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldMapArms.get()) {
			renderMapArm(playerRenderer, 0);
		} else {
			original.call(playerRenderer);
		}
	}

	@WrapMethod(method = "renderRightArm")
	private void axolotlclient$wrapRightMapArm(PlayerRenderer playerRenderer, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldMapArms.get()) {
			renderMapArm(playerRenderer, 1);
		} else {
			original.call(playerRenderer);
		}
	}

	@Unique
	private void renderMapArm(PlayerRenderer playerRenderer, int ordinal) {
		/* in 1.7, for some reason, the arms are oriented incorrectly... they're mirrored compared to 1.8 lol */
		int side = ordinal * 2 - 1;
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, -0.6F, 1.1F * side);
		GlStateManager.rotatef(-45 * side, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotatef(59.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotatef(-65 * side, 0.0F, 1.0F, 0.0F);
		playerRenderer.renderRightHand(minecraft.player);
		GlStateManager.popMatrix();
	}
}
