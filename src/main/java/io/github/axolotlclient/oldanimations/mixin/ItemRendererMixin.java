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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class ItemRendererMixin {
	@Redirect(method = "renderArmHoldingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipAndSwingOffset(FF)V"))
	public void allowUseAndSwing(HeldItemRenderer instance, float equipProgress, float swingProgress) {
		applyEquipAndSwingOffset(equipProgress,
				swingProgress == 0.0F && OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().useAndMine.get()
						? client.player.getHandSwingProgress(((MinecraftClientAccessor) client).getTicker().tickDelta)
						: swingProgress);
	}

	@Inject(method = "applySwordBlockTransformation", at = @At("RETURN"))
	public void oldBlocking(CallbackInfo callback) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().blocking.get()) {
			OldAnimations.oldBlocking();
		}
	}

	@Inject(method = "applyEatOrDrinkTransformation", at = @At("HEAD"), cancellable = true)
	public void oldDrinking(AbstractClientPlayerEntity clientPlayer, float partialTicks, CallbackInfo callback) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().eatingAndDrinking.get()) {
			callback.cancel();
			OldAnimations.oldDrinking(mainHand, clientPlayer, partialTicks);
		}
	}

	@Inject(method = "applyEquipAndSwingOffset", at = @At("HEAD"))
	private void oldanimations$transformSwing(float f, float g, CallbackInfo ci) {
		OldAnimations.getInstance().transformItem(mainHand.getItem());
	}

	@Shadow
	protected abstract void applyEquipAndSwingOffset(float equipProgress, float swingProgress);

	@Shadow
	private @Final MinecraftClient client;

	@Shadow
	private ItemStack mainHand;
}
