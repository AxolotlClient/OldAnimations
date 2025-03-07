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
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	//todo: find a better way to do this :p
	@Unique
	private boolean bl;

	@ModifyVariable(
		method = "renderStatusBars",
		at = @At(
			value = "STORE",
			ordinal = 0
		),
		index = 4
	)
	private boolean axolotlclient$disableFlashingCheck(boolean value) {
		bl = value;
		return false;
	}

	@ModifyArg(
		method = "renderStatusBars",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(IIIIII)V",
			ordinal = 3
		),
		index = 2
	)
	private int axolotlclient$enableFlashingCheck(int par1) {
		return par1 + (isHeartFlashingEnabled() && bl ? 1 : 0) * 9;
	}

	@Unique
	private static boolean isHeartFlashingEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().heartFlashing.get();
	}
}
