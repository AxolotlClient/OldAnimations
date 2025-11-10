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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DisconnectedScreen.class)
public class DisconnectedScreenMixin extends Screen {

	@WrapOperation(method = "init", at = @At(value = "NEW", target = "(IIILjava/lang/String;)Lnet/minecraft/client/gui/widget/ButtonWidget;"))
	private ButtonWidget axolotlclient$disconnectScreenInit(int i, int j, int k, String string, Operation<ButtonWidget> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.disconnectScreen.get()) {
			/* reposition the button slightly to match 1.7 */
			k = height / 4 + 120 + 12;
			/* this text still exists within the lang file!! */
			string = I18n.translate("gui.toTitle");
		}
		return original.call(i, j, k, string);
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DisconnectedScreen;drawCenteredString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V", ordinal = 0))
	private void axolotlclient$disconnectScreenRendering(DisconnectedScreen instance, TextRenderer textRenderer, String s, int i, int j, int k, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.disconnectScreen.get()) {
			/* reposition the text slightly to match 1.7 */
			j = height / 2 - 50;
		}
		original.call(instance, textRenderer, s, i, j, k);
	}

	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/screen/DisconnectedScreen;textHeight:I", ordinal = 1))
	private int axolotlclient$redirectTextHeight(int original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.disconnectScreen.get()) {
			/* reposition this other text slightly to match 1.7 */
			original = 60;
		}
		return original;
	}
}
