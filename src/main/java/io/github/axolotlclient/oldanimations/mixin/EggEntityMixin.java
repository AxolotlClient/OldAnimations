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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EggEntity.class)
public abstract class EggEntityMixin {

	@WrapOperation(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/entity/particle/ParticleType;DDDDDD[I)V"))
	private void axolotlclient$useSnowballParticle(World instance, ParticleType particleType, double d, double e, double f, double g, double h, double i, int[] is, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.eggEntityCollisionParticles.get()) {
			/* wild to think this was the particle used before 1.8 wtf */
			instance.addParticle(ParticleType.SNOWBALL, d, e, f, 0.0, 0.0, 0.0);
		} else {
			original.call(instance, particleType, d, e, f, g, h, i, is);
		}
	}
}
