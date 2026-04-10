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

package io.github.axolotlclient.oldanimations.mixin.mob_layers;

import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.DamageTint;
import io.github.axolotlclient.oldanimations.util.IDamageTint;
import net.minecraft.client.render.entity.SpiderRenderer;
import net.minecraft.client.render.entity.layer.SpiderEyesLayer;
import net.minecraft.entity.living.mob.monster.SpiderEntity;
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

	@Inject(method = "render(Lnet/minecraft/entity/living/mob/monster/SpiderEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/Model;render(Lnet/minecraft/entity/Entity;FFFFFF)V", shift = At.Shift.AFTER))
    private void axolotlclient$addDamageBrightness(SpiderEntity spiderEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		/* colors the entity's layer red just like 1.7 */
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.secondLayerDamageTint.get() || !OldAnimationsConfig.instance.damageTintColor.get()) {
			return;
		}
		if (((IDamageTint) parent).axolotlclient$setupOverlayColor(spiderEntity, h)) {
			parent.getModel().render(spiderEntity, f, g, i, j, k, l);
			DamageTint.unsetDamageTint();
		}
    }

	@Inject(method = "colorsWhenDamaged", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$applyDamageColor(CallbackInfoReturnable<Boolean> callback) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.secondLayerDamageTint.get() && !OldAnimationsConfig.instance.damageTintColor.get()) {
			/* enables coloring the second layer in 1.8 */
			callback.setReturnValue(true);
		}
	}
}
