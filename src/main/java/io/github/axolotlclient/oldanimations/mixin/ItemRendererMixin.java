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
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.GlintHandler;
import io.github.axolotlclient.oldanimations.util.OpaqueLeavesHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.BakedQuad;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

	@Shadow
	@Final
	private static Identifier ENCHANTMENT_GLINT_LOCATION;

	@Shadow
	protected abstract void prepareGuiItemRender(int x, int y, boolean gui3d);

	@Shadow
	protected abstract void render(BakedModel model, ItemStack stack);

	@Shadow
	public abstract boolean isGui3d(ItemStack itemStack);

	@Unique
	private boolean axolotlclient$isGui;

	@Unique
	private boolean axolotlclient$isHeld;

	@Unique
	private BakedModel axolotlclient$model = null;

	@Unique
	private int axolotlclient$glintColor = -8372020;

	@Unique
	private boolean axolotlclient$isLayered;

	@Unique
	private boolean axolotlclient$fastGraphics;

	@Inject(method = "renderItem", at = @At("HEAD"))
	private void axolotlclient$captureModel(ItemStack stack, BakedModel model, CallbackInfo ci) {
		axolotlclient$model = model;
	}

	@Inject(method = "renderItem", at = @At("TAIL"))
	private void axolotlclient$clearModel(CallbackInfo ci) {
		/* we need to clear this field */
		axolotlclient$model = null;
	}

	//TODO: should be a separate option
	@ModifyArgs(method = "applyNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/vertex/BufferBuilder;postNormal(FFF)V"))
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
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fastItems.get() && !model.isGui3d() && (axolotlclient$isGui || (!axolotlclient$isHeld))) {
			return filteredQuads;
		}
		return quads;
	}

	@ModifyArg(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemRenderer;renderEnchantmentGlint(Lnet/minecraft/client/resource/model/BakedModel;)V"))
	public BakedModel axolotlclient$replaceModel(BakedModel model) {
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGlint.get() ? GlintHandler.getModel(model) : model;
	}

	@ModifyArg(method = "render(Lnet/minecraft/client/resource/model/BakedModel;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemRenderer;render(Lnet/minecraft/client/resource/model/BakedModel;ILnet/minecraft/item/ItemStack;)V"), index = 1)
	private int axolotlclient$replaceColor(int color) {
		axolotlclient$glintColor = color;
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGlintColor.get() ? -10407781 : color;
	}

	@WrapWithCondition(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemRenderer;renderEnchantmentGlint(Lnet/minecraft/client/resource/model/BakedModel;)V"))
	private boolean axolotlclient$disableBlocksGlint(ItemRenderer instance, BakedModel bakedModel, @Local(argsOnly = true) ItemStack itemStack) {
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.disableGlintOnBlocks.get() || !isGui3d(itemStack);
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

	@Inject(method = "renderItemInHand(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemRenderer;renderItemInHand(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
	private void axolotlclient$fastItemOffset(ItemStack stack, ModelTransformations.Type transformationType, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fastItems.get()) {
			GlStateManager.translatef(0.0F, 0.0F, -0.0625F);
		}
	}

	@Inject(method = "renderItemInHand(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At("HEAD"))
	private void axolotlclient$captureHeldMode(ItemStack stack, LivingEntity entity, ModelTransformations.Type transformationType, CallbackInfo ci) {
		axolotlclient$isHeld = true;
	}

	@Inject(method = "renderItemInHand(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At("TAIL"))
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
			GlintHandler.renderEnchantmentGlintPre(ENCHANTMENT_GLINT_LOCATION, axolotlclient$glintColor);
			prepareGuiItemRender(x, y, false);
			GlintHandler.renderEnchantmentGlintPost();
		}
	}

	@ModifyVariable(method = "renderQuads", at = @At(value = "HEAD"), argsOnly = true)
	private List<BakedQuad> axolotlclient$filterModelQuads(List<BakedQuad> original, @Local(argsOnly = true) ItemStack itemStack) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGlintLayer.get() &&
			/* just to be safe, let's skip rendering while projectiles and dropped items are 2d */
			(!OldAnimationsConfig.instance.fastItems.get() || axolotlclient$isHeld)) {
			/* by default, items with multiple layers are rendered in multiple passes */
			/* in 1.7, the enchantment glint only shows on the base layer for whatever reason */
			return original.stream().filter(quad -> axolotlclient$isLayered == (quad.getTintIndex() > 0)).toList();
		}
		return original;
	}

	@Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;popMatrix()V"))
	private void axolotlclient$renderAdditionalModelLayers(ItemStack itemStack, BakedModel bakedModel, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGlintLayer.get() && !bakedModel.isCustomRenderer() && !bakedModel.isGui3d() && /* gotta make sure we aren't rendering extra crap */
			(!OldAnimationsConfig.instance.fastItems.get() || axolotlclient$isHeld)) {
			/* additional layers will be rendered without the glint :> */
			axolotlclient$isLayered = true;
			render(bakedModel, itemStack);
			axolotlclient$isLayered = false;
		}
	}

	//todo: can this feature be more concise? i have like 4 mixin injections. hmmm
	@Inject(method = "renderItemInHand(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;blendFuncSeparate(IIII)V", shift = At.Shift.AFTER))
	private void axolotlclient$fastGraphicsLeavesPre$held(ItemStack item, BakedModel model, ModelTransformations.Type transform, CallbackInfo ci) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.oldFastLeaves.get()
			|| OldAnimationsConfig.instance.opaqueLeavesTextures.get()) {
			return;
		}
		axolotlclient$fastGraphics = false;
		if (item.getItem() instanceof BlockItem) {
			/* MC-57356 */
			/* to my knowledge, leaf blocks are the only blocks affected by fast/fancy graphics */
			/* when it comes to their render layer. if theyre in their solid mode when it comes to world rendering */
			/* then we know fast graphics was enabled and so we can make the rendered item solid as well */
			/* we wont be messing with any other blocks */
			if (OpaqueLeavesHandler.isOpaqueLeavesBlock(Block.byItem(item.getItem()))) {
				axolotlclient$fastGraphics = true;
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
			}
		}
	}

	@Inject(method = "renderItemInHand(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;)V", shift = At.Shift.AFTER))
	private void axolotlclient$fastGraphicsLeavesPost$held(ItemStack item, BakedModel model, ModelTransformations.Type transform, CallbackInfo ci) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.oldFastLeaves.get()
			|| OldAnimationsConfig.instance.opaqueLeavesTextures.get()) {
			return;
		}
		if (axolotlclient$fastGraphics) {
			GlStateManager.enableAlphaTest();
			GlStateManager.enableBlend();
			axolotlclient$fastGraphics = false;
		}
	}

	@Inject(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;blendFunc(II)V", shift = At.Shift.AFTER))
	private void axolotlclient$fastGraphicsLeavesPre$gui(ItemStack item, int x, int y, CallbackInfo ci) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.oldFastLeaves.get()
			|| OldAnimationsConfig.instance.opaqueLeavesTextures.get()) {
			return;
		}
		axolotlclient$fastGraphics = false;
		if (item.getItem() instanceof BlockItem) {
			/* ditto */
			if (OpaqueLeavesHandler.isOpaqueLeavesBlock(Block.byItem(item.getItem()))) {
				axolotlclient$fastGraphics = true;
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
			}
		}
	}

	@Inject(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;)V", shift = At.Shift.AFTER))
	private void axolotlclient$fastGraphicsLeavesPost$gui(ItemStack item, int x, int y, CallbackInfo ci) {
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.oldFastLeaves.get()
			|| OldAnimationsConfig.instance.opaqueLeavesTextures.get()) {
			return;
		}
		if (axolotlclient$fastGraphics) {
			/* vanilla calls disableAlphaTest() right after this anyway, so we only */
			/* need to restore blend here to avoid leaking a disabled-blend state */
			GlStateManager.enableBlend();
			axolotlclient$fastGraphics = false;
		}
	}

	@ModifyArg(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/texture/Texture;pushFilter(ZZ)V"), index = 1)
	private boolean axolotlclient$guiItemsMipmap(boolean original) {
		/* MC-57574 and MC-277768. idk why there was 2 bug reports */
		/* in 1.7, the gui block items had mipmapping affect them since they were the same blocks used for world rendering */
		/* in 1.8, there is a distinction between block items and actual world rendered blocks */
		/* luckily it's easy to turn on mipmapping for rendered items */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.guiBlockItemsMipmap.get() ?
			Minecraft.getInstance().options.mipmapLevels > 0 : original;
	}
}
