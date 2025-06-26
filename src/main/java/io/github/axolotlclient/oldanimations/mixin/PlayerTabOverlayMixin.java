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
import io.github.axolotlclient.oldanimations.OldAnimations;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.overlay.PlayerTabOverlay;
import net.minecraft.client.network.PlayerInfo;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

@Mixin(value = PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin extends GuiElement {

	@Shadow
	@Final
	private Minecraft minecraft;

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 1))
	private int axolotlclient$replace(List<PlayerInfo> instance, Operation<Integer> original) {
		/* renders a fixed amount of player slots just like 1.7 */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.tabDimensions.get() ? minecraft.getNetworkHandler().maxPlayerCount : original.call(instance);
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1))
	private int axolotlclient$staticSlotWidth(int a, int b, Operation<Integer> original) {
		/* makes the slot width static just like 1.7 */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.tabDimensions.get() ? 300 : original.call(a, b);
	}

	@ModifyVariable(method = "render", at = @At("STORE"), index = 13)
	private int axolotlclient$capSlotWidth(int value) {
		/* caps the slot width just like 1.7 */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.tabDimensions.get() && value > 150) {
			value = 150;
		}
		return value;
	}

	@ModifyExpressionValue(
		method = "render",
		at = @At(value = "CONSTANT", args = "intValue=5"),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardObjective;getRenderType()Lnet/minecraft/scoreboard/criterion/ScoreboardCriterion$RenderType;", ordinal = 1),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/overlay/PlayerTabOverlay;fill(IIIII)V", ordinal = 2)
		))
	private int axolotlclient$removeBackgroundSpace(int constant) {
		/* cancels spacing */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.tabDimensions.get() ? 0 : constant;
	}

	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/overlay/PlayerTabOverlay;header:Lnet/minecraft/text/Text;", ordinal = 0))
	private Text axolotlclient$disableHeaderElement(Text original) {
		/* disables the tab header */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.disableTabHeader.get() ? null : original;
	}

	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/overlay/PlayerTabOverlay;footer:Lnet/minecraft/text/Text;", ordinal = 0))
	private Text axolotlclient$disableFooterElement(Text original) {
		/* disables the tab footer */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.disableTabFooter.get() ? null : original;
	}

	@ModifyVariable(method = "render", at = @At("STORE"), index = 11)
	private boolean axolotlclient$disablePlayerHeads(boolean original) {
		/* disables the rendering of player heads */
		return (!OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.disableTabPlayerHeads.get()) && original;
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/overlay/PlayerTabOverlay;fill(IIIII)V"), index = 2)
	private int axolotlclient$removeExtraPixels(int par1) {
		/* corrects for an extra column of pixels added in 1.8+ */
		return par1 - (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.tabDimensions.get() ? 1 : 0);
	}
}
