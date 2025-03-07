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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin_Layer {

	@Unique
	private ItemStack itemStack;

	@ModifyVariable(method = "render", at = @At("STORE"), index = 9)
	private ItemStack axolotlclient$captureLocalItemStack(ItemStack value) {
		itemStack = value;
		return value;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BiPedModel;setArmAngle(F)V"))
	private void axolotlclient$addSneakTranslation(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		if (isSneakingEnabled() && livingEntity.isSneaking())
			GlStateManager.translate(0.0F, 0.2F, 0.0F);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSneaking()Z"))
	private boolean axolotlclient$disableSneakTranslation(LivingEntity instance) {
		return (!isSneakingEnabled()) && instance.isSneaking();
	}

	//todo: delegate this to its own option
	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;I)V"), index = 0)
	private Item axolotlclient$changeToStick(Item item) {
		return areItemPositionsEnabled() ? Items.STICK : item;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getBlockType()I"))
	private int axolotlclient$disableBlockTypeCheck(Block instance) {
		return areItemPositionsEnabled() ? 3 : instance.getBlockType();
	}

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;)V"))
    private void axolotlclient$applyHeldItemLayerTransforms(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		if (!areItemPositionsEnabled()) return;
		if (ItemBlacklist.isPresent(itemStack)) return;
		Item item = itemStack.getItem();
		float var7;
		if (item instanceof BlockItem && MinecraftClient.getInstance().getItemRenderer().hasDepth(itemStack)) {
			var7 = 0.375F;
			GlStateManager.translate(0.0F, 0.1875F, -0.3125F);
			GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(-var7, -var7, var7);
		} else if (item == Items.BOW) {
			var7 = 0.625F;
			GlStateManager.translate(0.0F, 0.125F, 0.3125F);
			GlStateManager.rotate(-20.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(var7, -var7, var7);
			GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
		} else if (item.isHandheld()) {
			var7 = 0.625F;
			if (item.shouldRotate()) {
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translate(0.0F, -0.125F, 0.0F);
			}
			if (livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).getItemUseTicks() > 0 && ((PlayerEntity) livingEntity).method_2611()) {
				GlStateManager.translate(0.05F, 0.0F, -0.1F);
				GlStateManager.rotate(-50.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-10.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-60.0F, 0.0F, 0.0F, 1.0F);
			}
			GlStateManager.translate(0.0F, 0.1875F, 0.0F);
			GlStateManager.scale(var7, -var7, var7);
			GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
		} else {
			var7 = 0.375F;
			GlStateManager.translate(0.25F, 0.1875F, -0.1875F);
			GlStateManager.scale(var7, var7, var7);
			GlStateManager.rotate(60.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(20.0F, 0.0F, 0.0F, 1.0F);
		}
    }

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;)V"), index = 2)
	private ModelTransformation.Mode axolotlclient$changeTransformType(ModelTransformation.Mode mode) {
		return areItemPositionsEnabled() && !ItemBlacklist.isPresent(itemStack) ? ModelTransformation.Mode.NONE : mode;
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
