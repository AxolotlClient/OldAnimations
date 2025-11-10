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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.gui.screen.inventory.menu.PlayerInventoryScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerInventoryScreen.class)
public class PlayerInventoryScreenMixin {

	@WrapWithCondition(method = "checkStatusEffects", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gui/screen/inventory/menu/PlayerInventoryScreen;x:I", ordinal = 1))
	private boolean axolotlclient$dontUpdateEffectsHudPos(PlayerInventoryScreen instance, int value) {
		/* yeah oops */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.dontUpdateEffectsHud.get();
	}

	@WrapWithCondition(method = "checkStatusEffects", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gui/screen/inventory/menu/PlayerInventoryScreen;hasStatusEffect:Z", ordinal = 1))
	private boolean axolotlclient$dontUpdateEffectsHudPos2(PlayerInventoryScreen instance, boolean value) {
		/* buggy ahh menu */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.dontUpdateEffectsHud.get();
	}
}
