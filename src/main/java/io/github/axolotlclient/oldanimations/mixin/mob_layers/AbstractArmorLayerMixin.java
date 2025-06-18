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

import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.utils.DamageTint;
import io.github.axolotlclient.oldanimations.utils.IDamageTint;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.layer.AbstractArmorLayer;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArmorLayer.class)
public abstract class AbstractArmorLayerMixin {

	@Shadow
	@Final
	private LivingEntityRenderer<?> parent;

	@Shadow
	public abstract Model getModel(int i);

	@Inject(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/Model;render(Lnet/minecraft/entity/Entity;FFFFFF)V", shift = At.Shift.AFTER))
	private void axolotlclient$addDamageBrightness(LivingEntity entity, float handSwingAmount, float handSwing, float tickDelta, float age, float headYaw, float headPitch, float scale, int equipmentSlot, CallbackInfo ci) {
		/* colors the armor pieces red just like 1.7 */
		if (!OldAnimations.isEnabled() || !OldAnimations.getInstance().secondLayerDamageTint.get() || !OldAnimations.getInstance().damageColor.get()) {
			return;
		}
		if (((IDamageTint) parent).axolotlclient$setupOverlayColor(entity, tickDelta)) {
			getModel(equipmentSlot).render(entity, handSwingAmount, handSwing, age, headYaw, headPitch, scale);
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
