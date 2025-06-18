package io.github.axolotlclient.oldanimations.mixin.mob_layers;

import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.utils.DamageTint;
import io.github.axolotlclient.oldanimations.utils.IDamageTint;
import net.minecraft.client.render.entity.EndermanRenderer;
import net.minecraft.client.render.entity.layer.EndermanEyesLayer;
import net.minecraft.entity.living.mob.hostile.EndermanEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEyesLayer.class)
public abstract class EndermanEyesLayerMixin {

	@Shadow
	@Final
	private EndermanRenderer parent;

	@Inject(method = "render(Lnet/minecraft/entity/living/mob/hostile/EndermanEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/Model;render(Lnet/minecraft/entity/Entity;FFFFFF)V", shift = At.Shift.AFTER))
    public void axolotlclient$addDamageBrightness(EndermanEntity endermanEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		/* colors the entity's layer red just like 1.7 */
		if (!OldAnimations.isEnabled() || !OldAnimations.getInstance().secondLayerDamageTint.get() || !OldAnimations.getInstance().damageColor.get()) {
			return;
		}
		if (((IDamageTint) parent).axolotlclient$setupOverlayColor(endermanEntity, h)) {
			parent.getModel().render(endermanEntity, f, g, i, j, k, l);
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
