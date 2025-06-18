package io.github.axolotlclient.oldanimations.mixin.mob_layers;

import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.utils.DamageTint;
import io.github.axolotlclient.oldanimations.utils.IDamageTint;
import net.minecraft.client.render.entity.WolfRenderer;
import net.minecraft.client.render.entity.layer.WolfCollarLayer;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfCollarLayer.class)
public abstract class WolfCollarLayerMixin {

	@Shadow
	@Final
	private WolfRenderer parent;

	@Inject(method = "render(Lnet/minecraft/entity/living/mob/passive/animal/tamable/WolfEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/Model;render(Lnet/minecraft/entity/Entity;FFFFFF)V", shift = At.Shift.AFTER))
    public void axolotlclient$addDamageBrightness(WolfEntity wolfEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		/* colors the entity's layer red just like 1.7 */
		if (!OldAnimations.isEnabled() || !OldAnimations.getInstance().damageColor.get()) {
			return;
		}
		if (((IDamageTint) parent).axolotlclient$setupOverlayColor(wolfEntity, h)) {
			parent.getModel().render(wolfEntity, f, g, i, j, k, l);
			DamageTint.unsetDamageTint();
		}
    }
}
