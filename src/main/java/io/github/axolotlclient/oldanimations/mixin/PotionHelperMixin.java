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
import net.minecraft.client.util.IntegerBuffer;
import net.minecraft.entity.living.effect.PotionHelper;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;

@Mixin(PotionHelper.class)
public abstract class PotionHelperMixin {

	@Shadow
	@Final
	private static Map<Integer, Integer> COLOR_CACHE;

	@WrapOperation(method = "getColor(Ljava/util/Collection;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/effect/StatusEffect;getPotionColor()I"))
	private static int axolotlclient$oldJumpBoostParticlesColor(StatusEffect instance, Operation<Integer> original, Collection<StatusEffectInstance> collection) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldJumpBoostPotionColor.get()) {
			for (StatusEffectInstance statusEffectInstance : collection) {
				if (statusEffectInstance.getId() == 8 /* jump boost */) {
					/* taken from 1.7 */
					return 7889559;
				}
			}
		}
		return original.call(instance);
	}

	@Inject(method = "getColor(IZ)I", at = @At("HEAD"))
	private static void axolotlclient$refreshPotionCache(int dataValue, boolean bypassCache, CallbackInfoReturnable<Integer> cir) {
		if (OldAnimationsConfig.instance.reloadPotionColors) {
			OldAnimationsConfig.instance.reloadPotionColors = false;
			/* in order to update the potion color, we will need to clear then update the colors */
			COLOR_CACHE.clear();
			Integer integer = IntegerBuffer.get(dataValue);
			COLOR_CACHE.put(integer, PotionHelper.getColor(PotionHelper.getStatusEffects(integer, false)));
		}
	}
}
