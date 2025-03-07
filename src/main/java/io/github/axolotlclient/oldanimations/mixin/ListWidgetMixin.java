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
import net.minecraft.client.gui.widget.ListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ListWidget.class)
public abstract class ListWidgetMixin {

	@Shadow
	public abstract int getMaxScroll();

	@Shadow
	protected float scrollAmount;

	@Shadow
	protected boolean centerListVertically;

	//todo: can this be written better?
	@Inject(method = "capYPosition", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$allowNonNegativeScrolling(CallbackInfo ci) {
		if (!(OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().centerGuiSelection.get())) {
			return;
		}

		ci.cancel();
		int var1 = getMaxScroll();

		if (var1 < 0) {
			var1 /= 2;
		}

		if (!centerListVertically && var1 < 0) {
			var1 = 0;
		}

		if (scrollAmount < 0.0F) {
			scrollAmount = 0.0F;
		}

		if (scrollAmount > (float) var1) {
			scrollAmount = (float) var1;
		}
	}

	@ModifyArgs(method = "getMaxScroll", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"))
	private void axolotlclient$removeNonNegativeRestriction(Args args) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().centerGuiSelection.get()) {
			args.set(0, args.get(1));
		}
	}
}
