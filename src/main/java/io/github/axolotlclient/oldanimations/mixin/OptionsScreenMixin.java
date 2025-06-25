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
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin {

	@WrapWithCondition(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 5))
	private <E> boolean axolotlclient$disableSkinCustomizationButton(List<?> instance, E e) {
		/* disables the rendering of the skin customization button */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.disableServerSelectionButtons.get();
	}

	@ModifyExpressionValue(method = "init", at = @At(value = "CONSTANT", args = "stringValue=options.chat.title"))
	private String axolotlclient$useAlternativeTitle(String original) {
		/* for some reason, this text still exist in the game lol */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldMultiplayerSettingsPage.get() ? "options.multiplayer.title" : original;
	}

	@ModifyExpressionValue(method = "buttonClicked", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/widget/ButtonWidget;id:I", ordinal = 4))
	private int axolotlclient$disableSkinCustomizationButton2(int original) {
		/* disables the functionality of the skin customization button */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.disableServerSelectionButtons.get() ? -1 : original;
	}
}
