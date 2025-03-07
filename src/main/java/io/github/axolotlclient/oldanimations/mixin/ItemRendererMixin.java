package io.github.axolotlclient.oldanimations.mixin;

import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.utils.DummyItem;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

	@Shadow
	protected abstract void renderBakedItemModel(BakedModel bakedModel, ItemStack itemStack);

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
}
