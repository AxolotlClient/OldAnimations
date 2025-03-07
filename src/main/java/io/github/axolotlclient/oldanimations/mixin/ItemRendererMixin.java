package io.github.axolotlclient.oldanimations.mixin;

import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.util.DummyItem;
import io.github.axolotlclient.oldanimations.util.GlintModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

	@Shadow
	protected abstract void renderBakedItemModel(BakedModel bakedModel, ItemStack itemStack);

	@ModifyArg(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGlint(Lnet/minecraft/client/render/model/BakedModel;)V"))
	public BakedModel axolotlclient$replaceModel(BakedModel model) {
		return isOldGlintEnabled() ? GlintModel.getModel(model) : model;
	}

	@ModifyArg(method = "renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;ILnet/minecraft/item/ItemStack;)V"), index = 1)
	public int axolotlclient$replaceColor(int color) {
		return isOldGlintEnabled() ? -10407781 : color;
	}

	@ModifyConstant(method = "renderGlint", constant = @Constant(floatValue = 8.0F))
	public float axolotlclient$modifyScale(float original) {
		return isOldGlintEnabled() ? 1.0F / original : original;
	}

	@Redirect(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/item/ItemStack;)V"))
	private void axolotlclient$useCustomModel$layer0(ItemRenderer instance, BakedModel bakedModel, ItemStack itemStack) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().oldPotionGlint.get() && itemStack.getItem() instanceof PotionItem) {
			bakedModel = DummyItem.getModelFromID("bottle_overlay");
		}
		renderBakedItemModel(bakedModel, itemStack);
	}

	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;popMatrix()V"))
	private void axolotlclient$useCustomModel$layer1(ItemStack stack, BakedModel model, CallbackInfo ci) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().oldPotionGlint.get() && stack.getItem() instanceof PotionItem) {
			String id = PotionItem.isThrowable(stack.getData()) ? "bottle_splash_empty" : "bottle_drinkable_empty";
			renderBakedItemModel(DummyItem.getModelFromID(id), DummyItem.getStack());
		}
	}

	@Unique
	private static boolean isOldGlintEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().oldGlint.get();
	}
}
