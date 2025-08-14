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
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(World world) {
		super(world);
	}

	/* NOTE: the following two injections already exist in optifine, however, for people not using it, */
	/* i think it would be preferred to add an option in this mod seeing as it's relevant to 1.7 */

	@ModifyExpressionValue(method = "getRotationVec", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/living/LivingEntity;prevHeadYaw:F"))
	private float axolotlclient$usePrevYaw(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.rotationVecYawFix.get()) {
			/* don't use the prev head yaw as it is not accurate compared to prev yaw */
			original = prevYaw;
		}
		return original;
	}

	@ModifyExpressionValue(method = "getRotationVec", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/living/LivingEntity;headYaw:F"))
	private float axolotlclient$useYaw(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.rotationVecYawFix.get()) {
			/* ditto but with yaw */
			original = yaw;
		}
		return original;
	}
}
