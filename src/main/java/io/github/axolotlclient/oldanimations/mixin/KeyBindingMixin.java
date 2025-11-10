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

import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

	@ModifyVariable(method = "<init>", at = @At(value = "LOAD", ordinal = 0), index = 3, argsOnly = true)
	private static String axolotlclient$changeKeyBindingCategory(
		String original,
		@Local(ordinal = 0, index = 1, argsOnly = true) String string,
		@Local(ordinal = 0, index = 2, argsOnly = true) int i
	) {
		/* MC-70305 is the culprit */
		/* honestly this is a very silly injection, but idk a better way */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.moveSprintKeybind.get() &&
			"key.sprint".equals(string) && i == 29 && "key.categories.movement".equals(original)) {
			return "key.categories.gameplay";
		}
		return original;
	}
}
