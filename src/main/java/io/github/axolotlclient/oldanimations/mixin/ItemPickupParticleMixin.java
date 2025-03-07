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
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemPickupParticle.class)
public abstract class ItemPickupParticleMixin {

	@Shadow
	private Entity interactingEntity;

	@Redirect(method = "draw", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/Entity;prevTickY:D"))
	private double axolotlclient$includeEyeHeight$PrevTickY(Entity instance) {
		double original = instance.prevTickY;
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().oldItemPickup.get()) {
			original += interactingEntity.getEyeHeight();
		}
		return original;
	}

	@Redirect(method = "draw", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/Entity;y:D", ordinal = 1))
	private double axolotlclient$includeEyeHeight$Y(Entity instance) {
		double original = instance.y;
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().oldItemPickup.get()) {
			original += interactingEntity.getEyeHeight();
		}
		return original;
	}
}
