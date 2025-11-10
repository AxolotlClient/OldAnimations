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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.HeldItemRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.layer.HeldItemLayer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemLayer.class)
public abstract class HeldItemLayerMixin {

	@Shadow
	@Final
	private LivingEntityRenderer<?> parent;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/entity/HumanoidModel;translateRightArm(F)V"))
	private void axolotlclient$addSneakTranslation(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fixThirdPersonHeldItemSneakDeSync.get() &&
			!OldAnimationsConfig.instance.thirdPersonSneaking.get() && livingEntity.isSneaking())
			GlStateManager.translatef(0.0F, 0.2F, 0.0F);
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;isSneaking()Z"))
	private boolean axolotlclient$disableSneakTranslation(LivingEntity instance, Operation<Boolean> original) {
		if (OldAnimationsConfig.isEnabled() &&
			(OldAnimationsConfig.instance.fixThirdPersonHeldItemSneakDeSync.get() || OldAnimationsConfig.instance.thirdPersonSneaking.get())) {
			/* we need to remove the sneaking offset since we will be using our own */
			return false;
		}
		return original.call(instance);
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;I)V"), index = 0)
	private Item axolotlclient$changeToStick(Item item) {
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.stickRod.get() ? Items.STICK : item;
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRenderType()I"))
	private int axolotlclient$disableBlockTypeCheck(Block instance, Operation<Integer> original) {
		/* we need to stop these transformations from applying  */
		//TODO: Fix this. we can possibly use this code instead of skipping over it
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get() ? 3 : original.call(instance);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$applyHeldItemLayerTransforms(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci, @Local ItemStack itemStack) {
		if (!OldAnimationsConfig.isEnabled() || ItemUtil.isCustomRenderer(itemStack)) return;
		Item item = itemStack.getItem();
		float var7;
		/* original transformations from 1.7 */
		if (OldAnimationsConfig.instance.swordBlockThirdPerson.get() && livingEntity instanceof PlayerEntity &&
			((PlayerEntity) livingEntity).getItemUseTimer() > 0 && ((PlayerEntity) livingEntity).isSwordBlocking()) {
			GlStateManager.translatef(0.05F, 0.0F, -0.1F);
			GlStateManager.rotatef(-50.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(-10.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(-60.0F, 0.0F, 0.0F, 1.0F);
		}
		if (OldAnimationsConfig.instance.itemPositions.get()) {
			if (item instanceof BlockItem && Minecraft.getInstance().getItemRenderer().isGui3d(itemStack)) {
				var7 = 0.375F;
				GlStateManager.translatef(0.0F, 0.1875F, -0.3125F);
				GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scalef(-var7, -var7, var7);
			} else if (item == Items.BOW) {
				var7 = 0.625F;
				GlStateManager.translatef(0.0F, 0.125F, 0.3125F);
				GlStateManager.rotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scalef(var7, -var7, var7);
				GlStateManager.rotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else if (item.isHandheld() && !ItemUtil.isBlazeRod(itemStack)) {
				var7 = 0.625F;
				if (item.shouldRotate()) {
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.translatef(0.0F, -0.125F, 0.0F);
				}
				parent.translate();
				GlStateManager.scalef(var7, -var7, var7);
				GlStateManager.rotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				var7 = 0.375F;
				GlStateManager.translatef(0.25F, 0.1875F, -0.1875F);
				GlStateManager.scalef(var7, var7, var7);
				GlStateManager.rotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}
		}
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$disableResourcePackTransformations(HeldItemRenderer instance, LivingEntity livingEntity, ItemStack itemStack, ModelTransformations.Type type, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get() && !ItemUtil.isCustomRenderer(itemStack)) {
			type = ModelTransformations.Type.NONE;
		}
		original.call(instance, livingEntity, itemStack, type);
	}
}
