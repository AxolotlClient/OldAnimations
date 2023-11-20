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
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.HeldItemRenderer;
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
	@Shadow
	private ItemStack item;

	@Shadow
	protected abstract void appyFirstPersonTransform(float f, float g);

	@Redirect(method = "renderInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/HeldItemRenderer;appyFirstPersonTransform(FF)V"))
	public void allowUseAndSwing(HeldItemRenderer instance, float equipProgress, float swingProgress) {
		appyFirstPersonTransform(equipProgress,
			swingProgress == 0.0F && OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().useAndMine.get()
				? minecraft.player.getHandSwingProcess(((MinecraftClientAccessor) minecraft).getTicker().tickDelta)
				: swingProgress);
	}

	@Inject(method = "applySwordBlocking", at = @At("RETURN"))
	public void oldBlocking(CallbackInfo callback) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().blocking.get()) {
			OldAnimations.oldBlocking();
		}
	}

	@Inject(method = "applyConsuming", at = @At("HEAD"), cancellable = true)
	public void oldDrinking(ClientPlayerEntity clientPlayer, float partialTicks, CallbackInfo callback) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().eatingAndDrinking.get()) {
			callback.cancel();
			OldAnimations.oldDrinking(item, clientPlayer, partialTicks);
		}
	}

	@Inject(method = "appyFirstPersonTransform", at = @At("HEAD"))
	private void oldanimations$transformSwing(float f, float g, CallbackInfo ci) {
		OldAnimations.getInstance().transformItem(item.getItem());
	}

	@Shadow
	private @Final Minecraft minecraft;
}
