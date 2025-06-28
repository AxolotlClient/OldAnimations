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

package io.github.axolotlclient.oldanimations.util;

import io.github.axolotlclient.oldanimations.mixin.ClientPlayerInteractionManagerAccessor;
import io.github.axolotlclient.oldanimations.mixin.LivingEntityAccessor;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.sound.instance.SimpleSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldSettings;

public final class PlayerUtil {

	public static PlayerUtil INSTANCE = new PlayerUtil();

	private float miningCooldown;
	private float miningProgress;
	private BlockPos target = new BlockPos(-1, -1, -1);

	@Setter
	private boolean syncMiningProgress;

	public static boolean isSelf(Entity entity) {
		return entity instanceof PlayerEntity && Minecraft.getInstance().player.getNetworkId() == entity.getNetworkId();
	}

	public void fakeSwing(LocalClientPlayerEntity player) {
		int armSwingAnimationEnd = ((LivingEntityAccessor) player).getArmSwingAnimationEnd();
		if ((!player.handSwinging || player.handSwingTicks >= armSwingAnimationEnd / 2 || player.handSwingTicks < 0)) {
			player.handSwingTicks = -1;
			player.handSwinging = true;
		}
	}

	public void fakeDestroyBlock(Minecraft minecraft, BlockPos blockPos) {
		ClientPlayerInteractionManagerAccessor accessor = ((ClientPlayerInteractionManagerAccessor) minecraft.interactionManager);
		if (miningCooldown > 0) {
			miningCooldown--;
			return;
		}
		if (minecraft.interactionManager.getGameMode() == WorldSettings.GameMode.CREATIVE && minecraft.world.getWorldBorder().contains(blockPos)) {
			miningCooldown = 5;
			return;
		}

		if (!blockPos.equals(target)) {
			/* if the cursor is moved to another block, we need to stop the mining progress */
			stopFakeMiningBlock(minecraft);
			target = blockPos;
		}

		BlockState blockState = minecraft.world.getBlockState(blockPos);
		Block block = blockState.getBlock();

		if (syncMiningProgress) {
			/* for a seamless fake mining progress to look believable, we should sync it with the real progress */
			miningProgress = accessor.getMiningProgress();
			syncMiningProgress = false;
		}

		miningProgress = miningProgress + block.getMiningSpeed(minecraft.player, minecraft.player.world, blockPos);

		/* trying to sync the mining sounds :p */
		if (accessor.getMiningSoundTimer() % 4.0F == 0.0F) {
			minecraft.getSoundManager().play(new SimpleSoundInstance(
				new Identifier(block.sound.getStepSound()),
				(block.sound.getVolume() + 1.0F) / 8.0F,
				block.sound.getPitch() * 0.5F,
				(float) blockPos.getX() + 0.5F,
				(float) blockPos.getY() + 0.5F,
				(float) blockPos.getZ() + 0.5F
			));
		}
		accessor.setMiningSoundTimer(accessor.getMiningSoundTimer() + 1);
		if (miningProgress >= 1.0F) {
			minecraft.getSoundManager().play(new SimpleSoundInstance(
				new Identifier(block.sound.getDigSound()),
				(block.sound.getVolume() + 1.0F) / 2.0F,
				block.sound.getPitch() * 0.8F,
				(float)blockPos.getX() + 0.5F,
				(float)blockPos.getY() + 0.5F,
				(float)blockPos.getZ() + 0.5F
			));
			minecraft.particleManager.addBlockMiningParticles(blockPos, blockState);

			target = new BlockPos(blockPos.getX(), -1, blockPos.getZ());
			miningProgress = 0.0F;
			accessor.setMiningSoundTimer(0.0F);
			miningCooldown = 5;
		}

		minecraft.world.updateBlockMiningProgress(minecraft.player.getNetworkId(), target, (int) (miningProgress * 10.0F) - 1);
	}

	public void stopFakeMiningBlock(Minecraft minecraft) {
		miningProgress = 0.0F;
		minecraft.world.updateBlockMiningProgress(minecraft.player.getNetworkId(), target, -1);
	}
}
