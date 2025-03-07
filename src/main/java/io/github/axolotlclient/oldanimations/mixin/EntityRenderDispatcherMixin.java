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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

	@Shadow
	private PlayerEntityRenderer playerRenderer;

	@Shadow
	public float pitch;

	@Inject(
		method = "updateCamera(Lnet/minecraft/world/World;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Lnet/minecraft/client/option/GameOptions;F)V",
		at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;yaw:F", ordinal = 0, shift = At.Shift.AFTER),
		slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/option/GameOptions;perspective:I"))
	)
	private void axolotlclient$fixCameraRotation(World world, TextRenderer textRenderer, Entity entity, Entity entity2, GameOptions gameOptions, float f, CallbackInfo ci) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().mirrorProjectiles.get()) {
			pitch *= -1;
		}
	}

	@Inject(method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$defaultToSteve(Entity entity, CallbackInfoReturnable<PlayerEntityRenderer> cir) {
		if (OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().oldSkinRendering.get() && entity instanceof ClientPlayerEntity) {
			cir.setReturnValue(playerRenderer);
		}
	}
}
