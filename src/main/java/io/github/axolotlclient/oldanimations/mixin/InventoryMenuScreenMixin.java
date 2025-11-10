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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import net.minecraft.client.gui.screen.inventory.menu.SurvivalInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryMenuScreen.class)
public class InventoryMenuScreenMixin extends Screen {

	//TODO: This implementation is not perfect :(
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Lighting;turnOff()V", ordinal = 1))
	private void axolotlclient$changeLightingStateChange(Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.inventoryTextLighting.get() && axolotlclient$isValidState()) {
			/* weird. MC-16608 */
			GlStateManager.enableLighting();
		} else {
			original.call();
		}
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/inventory/menu/InventoryMenuScreen;drawForeground(II)V"))
	private void axolotlclient$changeTextColor(InventoryMenuScreen instance, int i, int j, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.inventoryTextLighting.get() && axolotlclient$isInSurvivalInventory()) {
			/* this is not the right modification. forgive me :/ */
			j = 3552822;
		}
		original.call(instance, i, j);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Lighting;turnOnGui()V", ordinal = 1))
	private boolean axolotlclient$removeLightingStateChange2() {
		/* yup. goodbye */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.inventoryTextLighting.get() || !axolotlclient$isValidState();
	}

	@Unique
	private boolean axolotlclient$isValidState() {
		/* the bug only affected guis like hoppers and chests. not the survival inventory for some reason?? */
		return minecraft.player.inventory.getMainHandStack() != null && !axolotlclient$isInSurvivalInventory();
	}

	@Unique
	private boolean axolotlclient$isInSurvivalInventory() {
		return ((InventoryMenuScreen)(Object) this instanceof SurvivalInventoryScreen);
	}
}
