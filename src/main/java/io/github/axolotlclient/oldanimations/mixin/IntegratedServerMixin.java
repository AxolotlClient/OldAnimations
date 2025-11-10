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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@WrapOperation(method = "loadWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldData;getDifficulty()Lnet/minecraft/world/Difficulty;"))
	private Difficulty axolotlclient$useGlobalDifficulty(WorldData instance, Operation<Difficulty> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.difficultyLogic.get()) {
			/* matches 1.7 difficulty logic */
			return null;
		}
		return original.call(instance);
	}

	@ModifyReturnValue(method = "getDefaultDifficulty", at = @At("RETURN"))
	private Difficulty axolotlclient$useGlobalDifficulty3(Difficulty original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.difficultyLogic.get()) {
			/* gotta use the global difficulty */
			return minecraft.options.difficulty;
		}
		return original;
	}

	@WrapOperation(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;world:Lnet/minecraft/client/world/ClientWorld;"))
	private ClientWorld axolotlclient$skipDifficultyAssign(Minecraft instance, Operation<ClientWorld> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.difficultyLogic.get()) {
			/* because we re-introduced the difficulty setting in the ServerPlayerEntity#updateSettings */
			/* we don't need to set difficulty here anymore :[ */
			return null;
		}
		return original.call(instance);
	}
}
