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
import net.minecraft.client.gui.GuiElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiElement.class)
public abstract class GuiElementMixin {

	@ModifyArg(method = "drawString", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I"), index = 0)
	private String axolotlclient$spoofTitleVersion(String string, @Local(argsOnly = true, ordinal = 0) int i, @Local(argsOnly = true, ordinal = 2) int k) {
		/* an absolutely atrocious hack to get the title version to say 1.7.10. this should be changed one day */
		/* wonder why the client brand is not showing... not my issue i think */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.show1_7_10.get() &&
			string.contains("1.8.9") && i == 2 && k == -1 /* this will only target the titlescreen drawString invocation hopefully */) {
			string = string.replace("1.8.9", "1.7.10");
		}
		return string;
	}
}
