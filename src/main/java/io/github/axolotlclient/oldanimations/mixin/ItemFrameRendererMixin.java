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
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.render.entity.ItemFrameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemFrameRenderer.class)
public class ItemFrameRendererMixin {

	/* wow */

	@WrapWithCondition(method = "renderItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;disableLighting()V"))
	private boolean axolotlclient$allowLighting() {
		/* taken from 1.7 */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.framedItemLighting.get();
	}

	@WrapWithCondition(method = "renderItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;pushLightingAttributes()V"))
	private boolean axolotlclient$allowLighting2() {
		/* taken from 1.7 */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.framedItemLighting.get();
	}

	@WrapWithCondition(method = "renderItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Lighting;turnOff()V"))
	private boolean axolotlclient$allowLighting3() {
		/* taken from 1.7 */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.framedItemLighting.get();
	}

	@WrapWithCondition(method = "renderItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Lighting;turnOn()V"))
	private boolean axolotlclient$allowLighting4() {
		/* taken from 1.7 */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.framedItemLighting.get();
	}

	@WrapWithCondition(method = "renderItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;popAttributes()V"))
	private boolean axolotlclient$allowLighting5() {
		/* taken from 1.7 */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.framedItemLighting.get();
	}

	@WrapWithCondition(method = "renderItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;enableLighting()V"))
	private boolean axolotlclient$allowLighting6() {
		/* taken from 1.7 */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.framedItemLighting.get();
	}
}
