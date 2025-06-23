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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.util.DebugComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.overlay.DebugOverlay;
import net.minecraft.client.render.TextRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugOverlay.class)
public abstract class DebugOverlayMixin {

	@Shadow
	@Final
	private TextRenderer textRenderer;

	@ModifyExpressionValue(method = "getGameInfo", at = @At(value = "CONSTANT", args = "stringValue=Minecraft 1.8.9 ("))
	private String axolotlclient$spoofDebugVersion(String original) {
		/* nostalgiaaaa */
		return OldAnimations.isEnabled() && OldAnimations.getInstance().show1_7_10.get() ? "Minecraft 1.7.10 (" : original;
	}

	@WrapOperation(method = "getGameInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getGameVersion()Ljava/lang/String;"))
	private String axolotlclient$spoofDebugVersion2(Minecraft instance, Operation<String> original) {
		/* why does it show the game version twice!!?? */
		return OldAnimations.isEnabled() && OldAnimations.getInstance().show1_7_10.get() ? "1.7.10" : original.call(instance);
	}

	@ModifyExpressionValue(method = "drawGameInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/overlay/DebugOverlay;getGameInfo()Ljava/util/List;"))
	private List<String> axolotlclient$replaceGameInfo(List<String> original) {
		return OldAnimations.isEnabled() && OldAnimations.getInstance().debugInfo.get() ? DebugComponents.getLeft() : original;
	}

	@Inject(method = "drawGameInfo", at = @At("TAIL"), remap = false)
	private void axolotlclient$addBottomLeftColumn(CallbackInfo ci) {
		if (OldAnimations.isEnabled() && OldAnimations.getInstance().debugInfo.get()) {
			/* renders the bottom left column of debug text, but in the greyish color just like 1.7 */
			final int fontHeight = textRenderer.fontHeight - (OldAnimations.getInstance().debugTextSpacing.get() ? 1 : 0);
			int top = DebugComponents.getLeft().size() * 10 + 4 /* should be 64, just like 1.7 */;
			for (String msg : DebugComponents.getLeftBottom()) {
				if (msg == null) continue;
				if (!OldAnimations.getInstance().disableDebugBackground.get()) {
					GuiElement.fill(1, top - 1, 2 + textRenderer.getWidth(msg) + 1, top + fontHeight - 1, -1873784752);
				}
				textRenderer.draw(msg, 2, top, 14737632, OldAnimations.getInstance().debugTextShadow.get());
				top += fontHeight;
			}
		}
	}

	@ModifyExpressionValue(method = "drawSystemInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/overlay/DebugOverlay;getSystemInfo()Ljava/util/List;"))
	private List<String> axolotlclient$replaceSystemInfo(List<String> original) {
		return OldAnimations.isEnabled() && OldAnimations.getInstance().debugInfo.get() ? DebugComponents.getRight() : original;
	}

	@WrapOperation(method = {"drawGameInfo", "drawSystemInfo"}, at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/render/TextRenderer;fontHeight:I"))
	private int axolotlclient$changeFontHeight(TextRenderer instance, Operation<Integer> original) {
		return OldAnimations.isEnabled() && OldAnimations.getInstance().debugTextSpacing.get() ? 10 : original.call(instance);
	}

	@WrapWithCondition(method = {"drawGameInfo", "drawSystemInfo"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/overlay/DebugOverlay;fill(IIIII)V"))
	private boolean axolotlclient$removeBackgroundRectangle(int left, int top, int right, int bottom, int color) {
		/* disable rendering the rectangular background, just like 1.7 */
		return !OldAnimations.isEnabled() || !OldAnimations.getInstance().disableDebugBackground.get();
	}

	@WrapOperation(method = "drawSystemInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TextRenderer;draw(Ljava/lang/String;III)I"))
	private int axolotlclient$addTextShadow(TextRenderer instance, String text, int x, int y, int color, Operation<Integer> original) {
		/* uses the alternative drawString method which allows text shadows, just like in 1.7 */
		return instance.draw(text, x, y, color, OldAnimations.isEnabled() && OldAnimations.getInstance().debugTextShadow.get());
	}

	@WrapOperation(method = "drawGameInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TextRenderer;draw(Ljava/lang/String;III)I"))
	private int axolotlclient$addTextShadow2(TextRenderer instance, String text, int x, int y, int color, Operation<Integer> original) {
		/* same as above redirect, but the text is white, just like in 1.7 */
		int textColor = OldAnimations.isEnabled() && OldAnimations.getInstance().debugTextColorScheme.get() ? 16777215 : 0xE0E0E0;
		return instance.draw(text, x, y, textColor, OldAnimations.isEnabled() && OldAnimations.getInstance().debugTextShadow.get());
	}
}
