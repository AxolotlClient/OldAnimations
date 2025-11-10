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

import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.OldButtonWidget;
import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public class KeyBindingEntryMixin {

	@Mutable
	@Shadow
	@Final
	private ButtonWidget keyBindingButton;

	@Mutable
	@Shadow
	@Final
	private ButtonWidget resetButton;

	@Inject(method = "<init>(Lnet/minecraft/client/gui/screen/options/ControlsListWidget;Lnet/minecraft/client/options/KeyBinding;)V", at = @At(value = "TAIL"))
	private void axolotlclient$oldButtonSize(ControlsListWidget controlsListWidget, KeyBinding keyBinding, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.controlsListButtonHeight.get()) {
			/* MC-70308 */
			/* because of axolotlclient's modifications to the buttonwidget, im sort of forced to create a copy */
			/* of the button widget to use for the controlslistwidget. this new button will have the exact same */
			/* properties as the buttons created before this injection. the only difference is the height like 1.7 */
			/* took me like a week before i realized axolotlclient changed shit. lmfao */
			keyBindingButton = new OldButtonWidget(
				keyBindingButton.id,
				keyBindingButton.x, keyBindingButton.y,
				keyBindingButton.getWidth(), 18,
				keyBindingButton.message
			);
			resetButton = new OldButtonWidget(
				resetButton.id,
				resetButton.x, resetButton.y,
				resetButton.getWidth(), 18,
				resetButton.message
			);
		}
	}
}
