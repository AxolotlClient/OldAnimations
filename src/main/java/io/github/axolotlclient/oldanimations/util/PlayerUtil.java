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

package io.github.axolotlclient.oldanimations.util;

import io.github.axolotlclient.oldanimations.mixin.LivingEntityAccessor;
import io.github.axolotlclient.oldanimations.util.ducks.Sneaky;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobType;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;

public final class PlayerUtil {
	public static PlayerUtil INSTANCE = new PlayerUtil();

	public boolean isSelf(Entity entity) {
		return entity instanceof PlayerEntity && Minecraft.getInstance().player.getNetworkId() == entity.getNetworkId();
	}

	//TODO: exact port of 1.7's code would be ideal :p
	public float getEyeHeight() {
		return ((Sneaky) Minecraft.getInstance().gameRenderer).axolotlclient$getEyeHeight();
	}

	public float getPlayerEntityEyeHeight() {
		/* being so deadass rn, this a real value taken from PlayerEntity#getEyeHeight in 1.7 */
		/* to make matters worse, when you add 0.08 to 0.12 you get 0.2... this is a conspiracy */
		return 0.12F;
	}

	public void fakeSwing(LocalClientPlayerEntity player) {
		int armSwingAnimationEnd = ((LivingEntityAccessor) player).getArmSwingAnimationEnd();
		if ((!player.armSwinging || player.armSwingingTicks >= armSwingAnimationEnd / 2 || player.armSwingingTicks < 0)) {
			player.armSwingingTicks = -1;
			player.armSwinging = true;
		}
	}

	public void fakeAttackEntity(PlayerEntity player, Entity entity) {
		if (entity.canBePunched() && !entity.onPunched(player)) {
			boolean isLivingEntity = entity instanceof LivingEntity;
			if (player.fallDistance > 0.0F && !player.onGround && !player.isClimbing() && !player.isInWater() &&
				!player.hasStatusEffect(StatusEffect.BLINDNESS) && player.vehicle == null && isLivingEntity) {
				Minecraft.getInstance().particleManager.addEmitter(entity, ParticleType.CRIT);
			}
			float g;
			if (isLivingEntity) {
				g = EnchantmentHelper.modifyDamage(player.getItemInHand(), ((LivingEntity) entity).getMobType());
			} else {
				g = EnchantmentHelper.modifyDamage(player.getItemInHand(), MobType.UNDEFINED);
			}
			if (g > 0.0F) {
				Minecraft.getInstance().particleManager.addEmitter(entity, ParticleType.CRIT_MAGIC);
			}
		}
	}
}
