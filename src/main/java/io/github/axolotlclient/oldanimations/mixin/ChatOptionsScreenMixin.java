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
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.model.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatOptionsScreen.class)
public class ChatOptionsScreenMixin extends Screen {

	@Unique
	private String axolotlclient$multiplayerOptionsTitle;

	@Unique
	private int axolotlclient$seperatorWidth;

	/* this is probably the dumbest way of doing this */
	@Unique
	private static final GameOptions.Option[] OLD_CHAT_OPTIONS = new GameOptions.Option[]{
		GameOptions.Option.CHAT_VISIBILITY,
		GameOptions.Option.CHAT_COLOR,
		GameOptions.Option.CHAT_LINKS,
		GameOptions.Option.CHAT_OPACITY,
		GameOptions.Option.CHAT_LINKS_PROMPT,
		GameOptions.Option.CHAT_SCALE,
		GameOptions.Option.CHAT_HEIGHT_FOCUSED,
		GameOptions.Option.CHAT_HEIGHT_UNFOCUSED,
		GameOptions.Option.CHAT_WIDTH
	};

	@Unique
	private int axolotlclient$i;

	//TODO: Can this be done better? most likely-
	@ModifyVariable(method = "init", at = @At("LOAD"), index = 1)
	private int axolotlclient$captureAndUpdateLocal(int original) {
		/* this is a horrible injection, but its all i could come up with :p */
		axolotlclient$i = original;
		return original;
	}

	@ModifyExpressionValue(method = "init", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/client/gui/screen/options/ChatOptionsScreen;CHAT_OPTIONS:[Lnet/minecraft/client/options/GameOptions$Option;"))
	private GameOptions.Option[] axolotlclient$removeReducedDebugInfo(GameOptions.Option[] original) {
		/* this modification is really stupid and needs to be re-written */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldMultiplayerSettingsPage.get() ? OLD_CHAT_OPTIONS : original;
	}

	@Inject(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2))
	private void axolotlclient$addCapeToggle(CallbackInfo ci) {
		/* adds a cape toggle button */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldMultiplayerSettingsPage.get()) {
			int i = axolotlclient$i + 1 /* because of the shitty injections above, i need to add 1 */;
			/* in 1.8.1, this "multiplayer settings" text was removed in favor of saying "chat settings" due to MC-69983 */
			/* the text still exists in the lang files tho :p */
			axolotlclient$multiplayerOptionsTitle = I18n.translate("options.multiplayer.title");
			if (i % 2 == 1) {
				i++;
			}
			axolotlclient$seperatorWidth = height / 6 + 24 * (i >> 1);
			i += 2;
			buttons.add(new OptionButtonWidget(2004, width / 2 - 155 + i % 2 * 160, height / 6 + 24 * (i >> 1), getButtonLabel(PlayerModelPart.CAPE)));
		}
	}

	@ModifyExpressionValue(method = "init", at = @At(value = "CONSTANT", args = "intValue=120"))
	private int axolotlclient$moveDoneButton(int original) {
		/* move done button so that it does not cover the cape button. value taken from 1.7 */
		return original + (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldMultiplayerSettingsPage.get() ? 48 : 0);
	}

	@Inject(method = "buttonClicked", at = @At("TAIL"))
	public void axolotlclient$addCapeButtonFunction(ButtonWidget buttonWidget, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldMultiplayerSettingsPage.get() &&
			buttonWidget.active && buttonWidget.id == 2004) {
			/* adapted from SkinCustomizationScreen class */
			PlayerModelPart playerModelPart = PlayerModelPart.CAPE;
			minecraft.options.togglePlayerModelPart(playerModelPart);
			buttonWidget.message = getButtonLabel(playerModelPart);
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/options/ChatOptionsScreen;drawCenteredString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V", shift = At.Shift.AFTER))
	public void axolotlclient$addMultiplayerText(int i, int j, float f, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldMultiplayerSettingsPage.get()) {
			/* adds the appropriate label straight from 1.7 */
			drawCenteredString(textRenderer, axolotlclient$multiplayerOptionsTitle, width / 2, axolotlclient$seperatorWidth + 7, 16777215);
		}
	}

	/* adapted from SkinCustomizationScreen class */
	@Unique
	private String getButtonLabel(PlayerModelPart playerModelPart) {
		String string;
		if (minecraft.options.getPlayerModelParts().contains(playerModelPart)) {
			string = I18n.translate("options.on");
		} else {
			string = I18n.translate("options.off");
		}
		return "Show " + playerModelPart.getName().getFormattedString() + ": " + string;
	}
}
