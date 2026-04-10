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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.living.mob.passive.animal.tameable.WolfEntity;
import net.minecraft.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WolfEntity.class)
public class WolfEntityMixin {

	@WrapOperation(method = "registerSyncedData", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/DyeColor;getId()I"))
	private int axolotlclient$fixCollarColor(DyeColor instance, Operation<Integer> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.defaultWolfCollarColor.get() &&
			Minecraft.getInstance().isSingleplayer()) {
			/* MC-54109 - the metadata and id are switched here in 1.8 for some reason */
			/* this fix was backported from 15w46a :) */
			return instance.getMetadata();
		}
		return original.call(instance);
	}
}
