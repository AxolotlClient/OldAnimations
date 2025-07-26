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
import net.minecraft.client.render.entity.SheepRenderer;
import net.minecraft.client.render.entity.layer.SheepFurLayer;
import net.minecraft.client.render.model.entity.SheepFurModel;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepFurLayer.class)
public abstract class SheepFurLayerMixin {

	@Shadow
	@Final
	private SheepRenderer parent;

	@Shadow
	@Final
	private SheepFurModel model;

	@Inject(method = "render(Lnet/minecraft/entity/living/mob/passive/animal/SheepEntity;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/entity/SheepFurModel;render(Lnet/minecraft/entity/Entity;FFFFFF)V", shift = At.Shift.AFTER))
    private void axolotlclient$addDamageBrightness(SheepEntity sheepEntity, float f, float g, float h, float i, float j, float k, float l, CallbackInfo ci) {
		/* colors the entity's layer red just like 1.7 */
		if (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.damageTintColor.get()) {
			return;
		}
		if (((IDamageTint) parent).axolotlclient$setupOverlayColor(sheepEntity, h)) {
			model.render(sheepEntity, f, g, i, j, k, l);
			DamageTint.unsetDamageTint();
		}
    }
}
