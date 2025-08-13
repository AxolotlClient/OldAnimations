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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	public PlayerEntityMixin(World world) {
		super(world);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/living/player/PlayerEntity;y:D", ordinal = 0))
	private double axolotlclient$includeEyeHeight$Y(double original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSneaking.get()) {
			/* sneaking moves the eyeheight down by 0.08 units... we must also make sure this applies to other renderings */
			original += isSneaking() ? -0.08F : 0.0F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/living/player/PlayerEntity;y:D", ordinal = 1))
	private double axolotlclient$includeEyeHeight$Y2(double original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSneaking.get()) {
			/* sneaking moves the eyeheight down by 0.08 units... we must also make sure this applies to other renderings */
			original += isSneaking() ? -0.08F : 0.0F;
		}
		return original;
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/living/player/PlayerEntity;y:D", ordinal = 2))
	private double axolotlclient$includeEyeHeight$Y3(double original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.thirdPersonSneaking.get()) {
			/* sneaking moves the eyeheight down by 0.08 units... we must also make sure this applies to other renderings */
			original += isSneaking() ? -0.08F : 0.0F;
		}
		return original;
	}
}
