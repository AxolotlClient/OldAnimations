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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.layer.HeldItemLayer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemLayer.class)
public abstract class HeldItemLayerMixin {

	@Unique
	private ItemStack itemStack;

	@ModifyVariable(method = "render", at = @At("STORE"), index = 9)
	private ItemStack axolotlclient$captureLocalItemStack(ItemStack value) {
		itemStack = value;
		return value;
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void axolotlclient$releaseCapturedLocal(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		itemStack = null; /* big brain time */
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/entity/HumanoidModel;translateRightArm(F)V"))
	private void axolotlclient$addSneakTranslation(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		if (isSneakingFixEnabled() && livingEntity.isSneaking())
			GlStateManager.translatef(0.0F, 0.2F, 0.0F);
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;isSneaking()Z"))
	private boolean axolotlclient$disableSneakTranslation(LivingEntity instance, Operation<Boolean> original) {
		return (!isSneakingFixEnabled()) && original.call(instance);
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;I)V"), index = 0)
	private Item axolotlclient$changeToStick(Item item) {
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.stickRod.get() ? Items.STICK : item;
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRenderType()I"))
	private int axolotlclient$disableBlockTypeCheck(Block instance, Operation<Integer> original) {
		/* we need to stop these transformations from applying  */
		//TODO: Fix this
		return areItemPositionsEnabled() && OldAnimationsConfig.instance.disableResourcePackItemTransformations.get() ? 3 : original.call(instance);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$applyHeldItemLayerTransforms(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		if (!OldAnimationsConfig.isEnabled() || ItemUtil.isBlacklisted(itemStack)) return;
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
				if (!OldAnimationsConfig.instance.disableResourcePackItemTransformations.get() && Block.byItem(item).getRenderType() == 2) return;
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
				GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
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

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"), index = 2)
	private ModelTransformations.Type axolotlclient$changeTransformType(ModelTransformations.Type type) {
		return areItemPositionsEnabled() && OldAnimationsConfig.instance.disableResourcePackItemTransformations.get() && !ItemUtil.isBlacklisted(itemStack) ? ModelTransformations.Type.NONE : type;
	}

	@Unique
	private static boolean areItemPositionsEnabled() {
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get();
	}

	@Unique
	private static boolean isSneakingFixEnabled() {
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fixThirdPersonHeldItemSneakDeSync.get();
	}
}
