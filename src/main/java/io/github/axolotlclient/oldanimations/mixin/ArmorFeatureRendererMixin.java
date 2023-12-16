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

import io.github.axolotlclient.oldanimations.OldAnimations;
import net.minecraft.client.render.entity.layer.AbstractArmorLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractArmorLayer.class)
public class ArmorFeatureRendererMixin {

	@ModifyArg(method = "getModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/layer/AbstractArmorLayer;usesInnerModel(I)Z"))
	private int oldArmour(int i) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().armourDamage.get())
			return 2;
		return i;
	}
}
