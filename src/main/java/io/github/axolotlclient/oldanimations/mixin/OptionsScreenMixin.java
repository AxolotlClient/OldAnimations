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

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.Difficulty;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(OptionsScreen.class /* C_1860331 */)
public abstract class OptionsScreenMixin extends Screen {

	@Shadow
	@Final
	private GameOptions options;

	@Shadow
	private ButtonWidget difficultyButton;

	@Shadow
	public abstract String getButtonLabel(Difficulty difficulty);

	@WrapWithCondition(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 5))
	private <E> boolean axolotlclient$disableSkinCustomizationButton(List<?> instance, E e) {
		/* disables the rendering of the skin customization button */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.disableSkinCustomizationButton.get();
	}

	@ModifyExpressionValue(method = "init", at = @At(value = "CONSTANT", args = "stringValue=options.chat.title"))
	private String axolotlclient$useAlternativeTitle(String original) {
		/* for some reason, this text still exist in the game lol */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldMultiplayerSettingsPage.get() ? "options.multiplayer.title" : original;
	}

	@WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getDifficulty()Lnet/minecraft/world/Difficulty;"))
	private Difficulty axolotlclient$tryNotToCrashGame(ClientWorld instance, Operation<Difficulty> original) {
		/* if we don't do this, our game will crash as no valid world is loaded */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.difficultyLogic.get()) {
			return options.difficulty;
		}
		return original.call(instance);
	}

	@Expression("? != null")
	@ModifyExpressionValue(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private boolean axolotlclient$skipRealmsNotificationButton(boolean original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.difficultyLogic.get()) {
			/* now, the realms notification button will never be rendered. */
			return true;
		}
		return original;
	}

	@ModifyExpressionValue(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isInSingleplayer()Z"))
	private boolean axolotlclient$skipLockedDifficultyRendering(boolean original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.difficultyLogic.get()) {
			/* we might as well skip over this to reduce the amount of work needed to replicate the old difficulty button */
			return false;
		}
		return original;
	}

	@WrapOperation(method = "init", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gui/widget/ButtonWidget;active:Z", ordinal = 1))
	private void axolotlclient$addHardcoreState(ButtonWidget instance, boolean value, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.difficultyLogic.get()) {
			if (minecraft.world != null && minecraft.world.getData().isHardcore()) {
				/* because we basically overwrote the original hardcore button code, we should add this :p */
				original.call(instance, value);
				instance.message = I18n.translate("options.difficulty") + ": " + I18n.translate("options.difficulty.hardcore");
			}
		} else {
			original.call(instance, value);
		}
	}

	@ModifyExpressionValue(method = "buttonClicked", at = @At(value = "CONSTANT", args = "intValue=108"))
	private int axolotlclient$updateOptionsDifficulty(int original, @Local(argsOnly = true) ButtonWidget buttonWidget) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.difficultyLogic.get() &&
			buttonWidget.id == original) {
			/* mostly adapted from 1.7. we need to update the difficulty option instead of the world difficulty */
			options.difficulty = Difficulty.byId(options.difficulty.getId() + 1 & 3);
			difficultyButton.message = getButtonLabel(options.difficulty);
			/* if we don't save, then the difficulty will not be saved if the user quits the game */
			/* i was pulling my hair out for a whole month wondering why the difficulty was not saving properly */
			minecraft.options.save();
		}
		return original;
	}
}
