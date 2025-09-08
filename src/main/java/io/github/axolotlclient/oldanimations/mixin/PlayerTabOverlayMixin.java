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
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.overlay.PlayerTabOverlay;
import net.minecraft.client.network.PlayerInfo;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin extends GuiElement {

	@Shadow
	@Final
	private Minecraft minecraft;

	//TODO: Player list entries are left adjacent in 1.8... we need to made them centered instead

	@ModifyVariable(method = "render", at = @At("STORE"))
	private List<PlayerInfo> axolotlclient$doNotSortList(List<PlayerInfo> original, @Local ClientPlayNetworkHandler clientPlayNetworkHandler) {
		/* 1.7 does not sort the players. we should just convert the online player map to an array list */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.dontSortTabEntries.get()) {
			return new ArrayList<>(clientPlayNetworkHandler.getOnlinePlayers());
		}
		return original;
	}

	@ModifyVariable(method = "render", at = @At(value = "LOAD", ordinal = 0), index = 5)
	private List<PlayerInfo> axolotlclient$useOldObjectivesPositionLogic(List<PlayerInfo> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldObjectivesPosition.get()) {
			/* because we're porting the 1.7 logic over below, we can just skip this for loop completely */
			return Collections.EMPTY_LIST;
		}
		return original;
	}

	@ModifyVariable(method = "render", at = @At("LOAD"), index = 6, ordinal = 1)
	private int axolotlclient$useOldObjectivesPositionLogic2(int original, @Local(index = 25) String string2) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldObjectivesPosition.get()) {
			/* taken from 1.7 */
			return minecraft.textRenderer.getWidth(string2) + 4;
		}
		return original;
	}

	@ModifyVariable(method = "render", at = @At(value = "LOAD", ordinal = 0), index = 27)
	private int axolotlclient$useOldObjectivesPositionLogic3(int original, @Local(index = 22) int w, @Share("localRefAc") LocalIntRef localRefAc) {
		/* storing the original value for later use */
		localRefAc.set(original);
		/* we need to solely use the player name/head position to determine the placement of the objective number like 1.7 */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldObjectivesPosition.get() ? w : original;
	}

	@ModifyVariable(method = "render", at = @At(value = "LOAD", ordinal = 1), index = 27)
	private int axolotlclient$undoModifyVariableAbove(int original, @Share("localRefAc") LocalIntRef localRefAc) {
		/* yep as described in the name, we need to revert the modification done above :p */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldObjectivesPosition.get() ? localRefAc.get() : original;
	}

	@ModifyVariable(method = "render", at = @At(value = "LOAD", ordinal = 1), index = 12)
	private int axolotlclient$useOldObjectivesPositionLogic4(int original, @Local(index = 13) int p) {
		/* rahh */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldObjectivesPosition.get() ? p - 17 : original;
	}

	@ModifyExpressionValue(method = "render", at = @At(value = "CONSTANT", args = "intValue=90"))
	private int axolotlclient$skipRenderingHeartsSpace(int original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.hideScoreboardHearts.get()) {
			return 0;
		}
		return original;
	}

	@WrapOperation(method = "renderDisplayScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardObjective;getRenderType()Lnet/minecraft/scoreboard/criterion/ScoreboardCriterion$RenderType;"))
	private ScoreboardCriterion.RenderType axolotlclient$skipRenderingHearts(ScoreboardObjective instance, Operation<ScoreboardCriterion.RenderType> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.hideScoreboardHearts.get()) {
			/* yep. no more hearts woo */
			return ScoreboardCriterion.RenderType.INTEGER;
		}
		return original.call(instance);
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 1))
	private int axolotlclient$replacePlayerListSize(List<PlayerInfo> instance, Operation<Integer> original) {
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

	//TODO: This can be a better injection
	@ModifyExpressionValue(method = "renderPing", at = @At(value = "CONSTANT", args = "intValue=11"))
	private int axolotlclient$movePingElement(int original) {
		/* move the ping element */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.tabDimensions.get() ? 12 : original;
	}
}
