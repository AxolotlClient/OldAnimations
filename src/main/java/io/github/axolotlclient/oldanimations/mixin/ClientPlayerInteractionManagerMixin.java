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
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ducks.IClientPlayerInteractionManager;
import net.minecraft.client.ClientPlayerInteractionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin implements IClientPlayerInteractionManager {

	@Shadow
	private boolean isMiningBlock;

	@Shadow
	private float miningProgress;

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	private BlockPos target;

	@ModifyExpressionValue(method = "stopMiningBlock", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/ClientPlayerInteractionManager;isMiningBlock:Z"))
	private boolean axolotlclient$allowStateReset(boolean original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.miningProgressResetLogic.get()) {
			/* in 1.8, mining progress ONLY resets when the mining state is true. */
			/* this may sound logical, however, it's entirely possible for there */
			/* to be mining progress while the mining state is false in 1.8. */

			/* this modification is not entirely needed to replicate 1.7 behavior since the modification done in */
			/* axolotlclient$fixMiningStatePacketLogic tackles the issue head on */
			return true;
		}
		return original;
	}

	@WrapWithCondition(method = "stopMiningBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/handler/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
	private boolean axolotlclient$sendIfMiningBlock(ClientPlayNetworkHandler instance, Packet<?> packet) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.miningProgressResetLogic.get()) {
			/* the mining abort packet should only be sent if the player is genuinely in the mining state like in 1.7 */
			return isMiningBlock;
		}
		return true;
	}

	@ModifyExpressionValue(method = "updateBlockMining", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ClientPlayerInteractionManager;isMiningBlock(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean axolotlclient$fixMiningStatePacketLogic(boolean original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.miningProgressResetLogic.get()) {
			/* this code is not taken from 1.7 exactly, rather, it's an attempt to recreate a similar mining progress resetting behavior */
			/* in 1.7, block mining progress resets when the player is not mining regardless of the circumstances */
			/* this code is better than porting 1.7's code as it works with 1.8's packet order */

			/* Moulberry (the goat) came up with the fix and gives a simple explanation in MC-255057 */

			/* to really flesh out his explanation, the game basically determines whether the mining progress should be stopped/started */
			/* based on whether the block that the player is mining is in the place that it was mined at after mining is complete. */
			/* the game will also check whether the tool that is mining is the same tool that the player is holding. */
			/* it does not consider the current mining progress or mining state of the block, just it's position and whether the mining tool's integrity. */
			/* if the server spawns the block back instantly, therefore making the block occupy the same position as the one previously mined, */
			/* then the client will think that previous mining operation is continuing just due to the fact that the block is still there in the same place */
			/* which will result in a start mining packet not being sent at the appropriate moment. */
			/* this results in a sort of de-sync where the mining progress continues to exist even after mining technically ended */
			/* this can be easily amended by checking if mining state is currently in progress or complete. */
			/* if mining is not in progress (false), then the if statement will fail and the else statement would be enacted. */
			/* startMiningBlock will be invoked which will send a start mining packet appropriately, and the universe will be at peace at last. */

			/* interestingly enough, startMiningBlock actually has this exact logic but inverted when determining which packet to send when attempting to mine */
			return original && isMiningBlock;
		}
		return original;
	}

	@Override
	public void axolotlclient$fakeStopMiningBlock() {
		/* no mining abort packet shall be sent... grim don't hurt me :C */
		isMiningBlock = false;
		miningProgress = 0.0F;
		minecraft.world.updateBlockMiningProgress(minecraft.player.getNetworkId(), target, -1);
	}
}
