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
public abstract class HeldItemRendererMixin {

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
		if (isSneakingEnabled() && livingEntity.isSneaking())
			GlStateManager.translatef(0.0F, 0.2F, 0.0F);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;isSneaking()Z"))
	private boolean axolotlclient$disableSneakTranslation(LivingEntity instance) {
		return (!isSneakingEnabled()) && instance.isSneaking();
	}

	//todo: delegate this to its own option
	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;I)V"), index = 0)
	private Item axolotlclient$changeToStick(Item item) {
		return areItemPositionsEnabled() ? Items.STICK : item;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRenderType()I"))
	private int axolotlclient$disableBlockTypeCheck(Block instance) {
		/* we need to stop these transformations from applying  */
		return areItemPositionsEnabled() ? 3 : instance.getRenderType();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$applyHeldItemLayerTransforms(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		if (!areItemPositionsEnabled()) return;
		if (ItemBlacklist.isPresent(itemStack)) return;
		Item item = itemStack.getItem();
		float var7;
		/* original transformations from 1.7 */
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
		} else if (item.isHandheld()) {
			var7 = 0.625F;
			if (item.shouldRotate()) {
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translatef(0.0F, -0.125F, 0.0F);
			}
			if (livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).getItemUseTimer() > 0 && ((PlayerEntity) livingEntity).isSwordBlocking() /* is blocking */) {
				GlStateManager.translatef(0.05F, 0.0F, -0.1F);
				GlStateManager.rotatef(-50.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(-10.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(-60.0F, 0.0F, 0.0F, 1.0F);
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

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;render(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"), index = 2)
	private ModelTransformations.Type axolotlclient$changeTransformType(ModelTransformations.Type type) {
		return areItemPositionsEnabled() && !ItemBlacklist.isPresent(itemStack) ? ModelTransformations.Type.NONE : type;
	}

	@Unique
	private static boolean areItemPositionsEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().itemPositions.get();
	}

	@Unique
	private static boolean isSneakingEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().sneaking.get();
	}
}
