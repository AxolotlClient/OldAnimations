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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(OptionsScreen.class)
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

	@Inject(method = "buttonClicked", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/widget/ButtonWidget;id:I", ordinal = 2))
	private void axolotlclient$onlySetIfInWorld(ButtonWidget buttonWidget, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDifficultyButtonLogic.get() && minecraft.world == null && buttonWidget.id == 108) {
			/* this is so silly... but the alternatives are not fun :p */
			options.difficulty = Difficulty.byId(options.difficulty.getId() + 1 & 3);
			difficultyButton.message = getButtonLabel(options.difficulty);
		}
	}

	@ModifyExpressionValue(method = "buttonClicked", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/widget/ButtonWidget;id:I", ordinal = 2))
	private int axolotlclient$onlySetIfInWorld(int original) {
		/* because we're going to be able to toggle this button while not in a world, we can avoid the game crashing by */
		/* checking if the world is valid before we set the difficulty */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDifficultyButtonLogic.get() && minecraft.world == null ? -1 : original;
	}

	@ModifyArg(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Difficulty;byId(I)Lnet/minecraft/world/Difficulty;"), index = 0)
	private int axolotlclient$useOptionsDifficulty(int i) {
		/* use the options difficulty */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDifficultyButtonLogic.get()) {
			return options.difficulty.getId() + 1 & 3;
		}
		return i;
	}

	@Inject(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/options/OptionsScreen;getButtonLabel(Lnet/minecraft/world/Difficulty;)Ljava/lang/String;"))
	private void axolotlclient$updateOptionsDifficulty(ButtonWidget buttonWidget, CallbackInfo ci) {
		/* update the options difficulty */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDifficultyButtonLogic.get()) {
			options.difficulty = Difficulty.byId(options.difficulty.getId() + 1 & 3);
		}
	}

	@WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getDifficulty()Lnet/minecraft/world/Difficulty;"))
	private Difficulty axolotlclient$tryNotToCrashGame(ClientWorld instance, Operation<Difficulty> original) {
		/* if we don't do this, our game will crash as no valid world is loaded */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDifficultyButtonLogic.get()) {
			return options.difficulty;
		}
		return original.call(instance);
	}

	@Expression("? != null")
	@ModifyExpressionValue(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private boolean axolotlclient$skipRealmsNotificationButton(boolean original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDifficultyButtonLogic.get()) {
			/* now, the realms notification button will never be rendered. */
			/* unfortunately, this was the only injection i could think of to get this feature out in a compatible manner */
			return true;
		}
		return original;
	}

	@ModifyExpressionValue(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isInSingleplayer()Z"))
	private boolean axolotlclient$skipLockedDifficultyRendering(boolean original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDifficultyButtonLogic.get()) {
			/* we might as well skip over this to reduce the amount of work needed to replicate the old difficulty button */
			return false;
		}
		return original;
	}

	@WrapWithCondition(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;active:Z", ordinal = 1))
	private boolean axolotlclient$disableActiveState(ButtonWidget instance, boolean value) {
		/* you were a pawn */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.oldDifficultyButtonLogic.get();
	}

	@Inject(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;active:Z", ordinal = 1))
	private void axolotlclient$addHardcoreState(CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldDifficultyButtonLogic.get() &&
			minecraft.world != null && minecraft.world.getData().isHardcore()) {
			/* because we basically overwrote the original hardcore button code, we should add this :p */
			difficultyButton.active = false;
			difficultyButton.message = I18n.translate("options.difficulty") + ": " + I18n.translate("options.difficulty.hardcore");
		}
	}
}
