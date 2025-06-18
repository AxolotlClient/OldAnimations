package io.github.axolotlclient.oldanimations.mixin.mob_layers;

import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.utils.DamageTint;
import io.github.axolotlclient.oldanimations.utils.IDamageTint;
import net.minecraft.client.render.entity.SpiderRenderer;
import net.minecraft.client.render.entity.layer.SpiderEyesLayer;
import net.minecraft.entity.living.mob.hostile.SpiderEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpiderEyesLayer.class)
public abstract class SpiderEyesLayerMixin {

	@Shadow
	@Final
	private SpiderRenderer<SpiderEntity> parent;

	@Inject(method = "render(Lnet/minecraft/entity/living/mob/hostile/SpiderEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/Model;render(Lnet/minecraft/entity/Entity;FFFFFF)V", shift = At.Shift.AFTER))
    public void axolotlclient$addDamageBrightness(SpiderEntity spiderEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		/* colors the entity's layer red just like 1.7 */
		if (!OldAnimations.isEnabled() || !OldAnimations.getInstance().secondLayerDamageTint.get() || !OldAnimations.getInstance().damageColor.get()) {
			return;
		}
		if (((IDamageTint) parent).axolotlclient$setupOverlayColor(spiderEntity, h)) {
			parent.getModel().render(spiderEntity, f, g, i, j, k, l);
			DamageTint.unsetDamageTint();
		}
    }

	@Inject(method = "colorsWhenDamaged", at = @At("HEAD"), cancellable = true)
	public void axolotlclient$applyDamageColor(CallbackInfoReturnable<Boolean> callback) {
		if (OldAnimations.isEnabled() && OldAnimations.getInstance().secondLayerDamageTint.get() && !OldAnimations.getInstance().damageColor.get()) {
			/* enables colring the second layer in 1.8 */
			callback.setReturnValue(true);
		}
	}
}
