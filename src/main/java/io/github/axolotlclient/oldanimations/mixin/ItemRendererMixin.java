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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.item.ItemModelShaper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.BakedQuad;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

	@Shadow
	@Final
	private static Identifier ENCHANTMENT_GLINT_LOCATION;

	@Shadow
	@Final
	private TextureManager textureManager;

	@Shadow
	protected abstract void prepareGuiItemRender(int x, int y, boolean gui3d);

	@Shadow
	protected abstract void render(BakedModel model, ItemStack stack);

	@Shadow
	public abstract boolean isGui3d(ItemStack itemStack);

	@Shadow
	public abstract ItemModelShaper getModelShaper();

	@Unique
	private boolean axolotlclient$isGui;

	@Unique
	private boolean axolotlclient$isHeld;

	@Unique
	private BakedModel axolotlclient$model = null;

	@Unique
	private int axolotlclient$glintColor = -8372020;

	@Inject(method = "renderItem", at = @At("HEAD"))
	private void axolotlclient$captureModel(ItemStack stack, BakedModel model, CallbackInfo ci) {
		axolotlclient$model = model;
	}

	@Inject(method = "renderItem", at = @At("TAIL"))
	private void axolotlclient$clearModel(CallbackInfo ci) {
		/* we need to clear this field */
		axolotlclient$model = null;
	}

	@ModifyArgs(method = "applyNormal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;postNormal(FFF)V"))
	private void axolotlclient$modifyNormals(Args args) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.fastItems.get()) {
			return;
		}
		if (!axolotlclient$isGui && !axolotlclient$isHeld && !axolotlclient$model.isGui3d()) {
			args.setAll(args.get(0), args.get(2), args.get(1));
		}
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/client/resource/model/BakedModel;ILnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/model/BakedModel;getQuads()Ljava/util/List;"))
	private List<BakedQuad> axolotlclient$changeToSprite(List<BakedQuad> quads, @Local(argsOnly = true) BakedModel model) {
		List<BakedQuad> filteredQuads = quads.stream().filter(baked -> baked.getFace() == Direction.SOUTH).toList();
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fastItems.get() && !model.isGui3d() && (axolotlclient$isGui ||
			(!axolotlclient$isHeld))) {
			return filteredQuads;
		}
		return quads;
	}

	@ModifyArg(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderEnchantmentGlint(Lnet/minecraft/client/resource/model/BakedModel;)V"))
	public BakedModel axolotlclient$replaceModel(BakedModel model) {
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGlint.get() ? GlintHandler.getModel(model) : model;
	}

	@ModifyArg(method = "render(Lnet/minecraft/client/resource/model/BakedModel;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;render(Lnet/minecraft/client/resource/model/BakedModel;ILnet/minecraft/item/ItemStack;)V"), index = 1)
	private int axolotlclient$replaceColor(int color) {
		axolotlclient$glintColor = color;
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGlintColor.get() ? -10407781 : color;
	}

	@WrapOperation(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderEnchantmentGlint(Lnet/minecraft/client/resource/model/BakedModel;)V"))
	public void axolotlclient$disableBlocksGlint(ItemRenderer instance, BakedModel bakedModel, Operation<Void> original, @Local(argsOnly = true) ItemStack itemStack) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.disableGlintOnBlocks.get() || !isGui3d(itemStack)) {
			original.call(instance, bakedModel);
		}
	}

	@Inject(method = "renderEnchantmentGlint", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$disableDefaultGlint(CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled()) {
			if (OldAnimationsConfig.instance.oldGuiGlint.get() && axolotlclient$isGui) {
				ci.cancel();
			}
			if (OldAnimationsConfig.instance.fastItems.get() && !axolotlclient$isGui && !axolotlclient$isHeld) {
				ci.cancel();
			}
		}
	}

	@ModifyExpressionValue(method = "renderEnchantmentGlint", at = @At(value = "CONSTANT", args = "floatValue=8.0F"))
	private float axolotlclient$modifyScale(float original) {
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGlint.get() ? 1.0F / original : original;
	}

	@Inject(method = "renderHeldItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderHeldItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$fastItemOffset(ItemStack stack, ModelTransformations.Type transformationType, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fastItems.get()) {
			GlStateManager.translatef(0.0F, 0.0F, -0.0625F);
		}
	}

	@Inject(method = "renderHeldItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At("HEAD"))
	private void axolotlclient$captureHeldMode(ItemStack stack, LivingEntity entity, ModelTransformations.Type transformationType, CallbackInfo ci) {
		axolotlclient$isHeld = true;
	}

	@Inject(method = "renderHeldItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At("TAIL"))
	private void axolotlclient$releaseHeldMode(ItemStack stack, LivingEntity entity, ModelTransformations.Type transformationType, CallbackInfo ci) {
		axolotlclient$isHeld = false;
	}

	@Inject(method = "renderGuiItemModel", at = @At("HEAD"))
	private void axolotlclient$captureGuiMode(ItemStack stack, int x, int y, CallbackInfo ci) {
		axolotlclient$isGui = true;
	}

	@Inject(method = "renderGuiItemModel", at = @At("TAIL"))
	private void axolotlclient$renderGuiGlint(ItemStack stack, int x, int y, CallbackInfo ci) {
		axolotlclient$isGui = false;
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGuiGlint.get() && stack.hasEnchantmentGlint()) {
			if (OldAnimationsConfig.instance.disableGlintOnBlocks.get() && isGui3d(stack)) {
				return;
			}
			GlintHandler.renderEnchantmentGlintPre(textureManager, ENCHANTMENT_GLINT_LOCATION, axolotlclient$glintColor);
			prepareGuiItemRender(x, y, false);
			GlintHandler.renderEnchantmentGlintPost(textureManager);
		}
	}

	@WrapOperation(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;render(Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/item/ItemStack;)V"))
	private void axolotlclient$useCustomModel$layer0(ItemRenderer instance, BakedModel model, ItemStack stack, Operation<Void> original) {
		/* renders the potion's overlay WITH the glint like in 1.7 */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldPotionGlint.get() &&
			!axolotlclient$isGui && stack.getItem() instanceof PotionItem &&
			/* just to be safe, let's skip rendering while projectiles and dropped items are 2d */
			(!OldAnimationsConfig.instance.fastItems.get() || axolotlclient$isHeld)) {
			model = axolotlclient$getModel("bottle_overlay");
		}
		original.call(instance, model, stack);
	}

	@Inject(method = "renderItem", at = @At(value = "INVOKE",target = "Lcom/mojang/blaze3d/platform/GlStateManager;popMatrix()V"))
	private void axolotlclient$useCustomModel$layer1(ItemStack stack, BakedModel model, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldPotionGlint.get() &&
			!model.isCustomRenderer() && !axolotlclient$isGui && stack.getItem() instanceof PotionItem &&
			/* just to be safe, let's skip rendering while projectiles and dropped items are 2d */
			(!OldAnimationsConfig.instance.fastItems.get() || axolotlclient$isHeld)) {
			/* renders the splash/drinkable bottle AFTER the glint rendering like in 1.7 */
			String id = PotionItem.isSplashPotion(stack.getMetadata()) ? "bottle_splash_empty" : "bottle_drinkable_empty";
			/* hacky way of rendering the bottle without using the potion's overlay color */
			render(axolotlclient$getModel(id), ItemUtil.DummyItem.getStack());
		}
	}

	@Inject(method = "renderGuiItemModel", at = @At(value = "HEAD"))
	private void axolotlclient$fixDepthAndCaptureStack(ItemStack stack, int x, int y, CallbackInfo ci) {
		/* honestly, idk why this works, but it does :p */
		GlStateManager.enableDepthTest();
	}

	@Inject(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/block/ModelTransformations;apply(Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$captureGuiStack(ItemStack itemStack, int i, int j, CallbackInfo ci) {
		ItemUtil.setGuiItemStack(itemStack);
	}

	@Inject(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/block/ModelTransformations;apply(Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", shift = At.Shift.AFTER))
	private void axolotlclient$releaseGuiStack(ItemStack itemStack, int i, int j, CallbackInfo ci) {
		ItemUtil.setGuiItemStack(null);
	}

	@Inject(method = "renderHeldItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/block/ModelTransformations;apply(Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$captureHeldStack(ItemStack itemStack, BakedModel bakedModel, ModelTransformations.Type type, CallbackInfo ci) {
		ItemUtil.setHeldItemStack(itemStack);
	}

	@Inject(method = "renderHeldItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/block/ModelTransformations;apply(Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", shift = At.Shift.AFTER))
	private void axolotlclient$releaseHeldStack(ItemStack itemStack, BakedModel bakedModel, ModelTransformations.Type type, CallbackInfo ci) {
		ItemUtil.setHeldItemStack(null);
	}

	@Inject(method = "renderHeldItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/block/ModelTransformations;apply(Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$reverseTransformations(ItemStack itemStack, BakedModel bakedModel, ModelTransformations.Type type, CallbackInfo ci) {
		/* we can replicate ModelTransformations.Type.NONE by just reversing the default transformations! */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.itemPositions.get() &&
			!OldAnimationsConfig.instance.disableResourcePackItemTransformations.get() && !ItemUtil.isBlacklisted(itemStack)) {
			float scale;
			float scale2 = 0.0625F;
			if (type == ModelTransformations.Type.FIRST_PERSON && !isGui3d(itemStack)) {
				scale = 1.0F / 1.7F;
				GlStateManager.scalef(scale, scale, scale);
				GlStateManager.rotatef(-25.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translatef(0.0F, -4.0F * scale2, -2.0F * scale2);
			} else if (type == ModelTransformations.Type.THIRD_PERSON) {
				/* fortnite, we need to talk... */
				Item item = itemStack.getItem();
				if (item instanceof BlockItem && Minecraft.getInstance().getItemRenderer().isGui3d(itemStack)) {
					if (Block.byItem(item).getRenderType() == 2) return;
					scale = 1.0F / 0.375F;
					GlStateManager.scalef(scale, scale, scale);
					GlStateManager.rotatef(-170.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotatef(-10.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.translatef(0.0F, -1.5F * scale2, 2.75F * scale2);
				} else if (item == Items.BOW) {
					GlStateManager.rotatef(45.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotatef(-5.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(-80.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.translatef(-0.75F * scale2, 0.0F * scale2, -0.25F * scale2);
				} else if (item.isHandheld()) {
					scale = 1.0F / 0.85F;
					GlStateManager.scalef(scale, scale, scale);
					GlStateManager.rotatef(35.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.translatef(0.0F, (ItemUtil.isBlazeRod(itemStack) ? -0.75F : -1.25F) * scale2, 3.5F * scale2);
				} else {
					scale = 1.0F / 0.55F;
					GlStateManager.scalef(scale, scale, scale);
					GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.translatef(0.0F, -1.0F * scale2, 3.0F * scale2);
				}
			}
		}
	}

	@Unique
	private BakedModel axolotlclient$getModel(String model) {
		return getModelShaper().getManager().getModel(new ModelIdentifier(model, "inventory"));
	}
}
