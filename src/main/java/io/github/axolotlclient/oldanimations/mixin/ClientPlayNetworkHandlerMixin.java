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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitlesS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

	@Shadow
	private ClientWorld world;

	@Shadow
	@Final
	private Random random;

	@Unique
	private static final String[] axolotlclient$EVENT_MESSAGES = new String[]{"tile.bed.notValid", null, null, "gameMode.changed"};

	@ModifyExpressionValue(method = "handleAddXpOrb", at = @At(value = "CONSTANT", args = "doubleValue=32"))
	private double axolotlclient$oldOrbRendering(double original) {
		return original / (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.xpOrbPosition.get() ?
			/* MC-4167 and MC-12013 yall suck */
			32.0D : 1.0D /* renders the xp orbs similar to 1.7 by oddly offsetting them */
		);
	}

	@ModifyExpressionValue(method = "handleEntityPickup", at = @At(value = "CONSTANT", args = "floatValue=0.5"))
	private float axolotlclient$oldItemPickup(float original) {
		/* taken from 1.7 */
		return OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldItemPickup.get() ? -0.5F : original;
	}

	@Inject(method = "handleTitles", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$disableTitlesPacket(TitlesS2CPacket packet, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.disableTitles.get()) {
			/* 1.7 doesn't have titles */
			ci.cancel();
		}
	}

	@WrapWithCondition(method = "handleLogin", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/options/GameOptions;difficulty:Lnet/minecraft/world/Difficulty;"))
	private boolean axolotlclient$dontUsePacketDifficulty(GameOptions instance, Difficulty value) {
		/* we're going to set the options difficulty elsewhere, so let's remove this as it's not needed */
		return !OldAnimationsConfig.isEnabled() || !OldAnimationsConfig.instance.difficultyLogic.get();
	}

	@WrapOperation(method = "handleGameEvent", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/network/packet/s2c/play/GameEventS2CPacket;EVENT_MESSAGES:[Ljava/lang/String;"))
	private String[] axolotlclient$oldEventMessages(Operation<String[]> original) {
		/* taken from 1.7 */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.oldGameModeCommand.get()) {
			/* the server still notifies us on game mode changes, so we can easily just adapt this 1.7 code */
			return axolotlclient$EVENT_MESSAGES;
		}
		return original.call();
	}

	//TODO: this may conflict with particles summoned by a command
	@Inject(method = "handleParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketUtils;ensureOnSameThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/handler/PacketHandler;Lnet/minecraft/util/BlockableEventLoop;)V", shift = At.Shift.AFTER), cancellable = true)
	private void axolotlclient$disableServerSideFallParticles(ParticleS2CPacket particleS2CPacket, CallbackInfo ci) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.fallParticles.get() &&
			particleS2CPacket.getType() == ParticleType.BLOCK_DUST &&
			particleS2CPacket.getVelocityScale() == 0.15F /* let's hope servers also use this scale factor */) {
			/* we need a blank slate to adapt the 1.7 fall particles code */
			/* luckily, the packet gives us enough information to reconstruct the 1.7 logic */
			ci.cancel();
			int x = MathHelper.floor(particleS2CPacket.getX());
			int y = MathHelper.floor(particleS2CPacket.getY() - 0.2F);
			int z = MathHelper.floor(particleS2CPacket.getZ());
			BlockPos blockPos = new BlockPos(x, y, z);
			if (world.isAir(blockPos)) {
				Block block = world.getBlockState(blockPos.down()).getBlock();
				if (block instanceof FenceBlock || block instanceof WallBlock || block instanceof FenceGateBlock) {
					/* this is a bug, but fences/walls don't emit particles - MC-30543 */
					return;
				}
			}
			int count = particleS2CPacket.getCount(); /* 150 * fallDistance */
			double fallDistance = count / 150.0; /* must be a double to ensure precision :p */
			for (int i = 0; i < count; ++i) {
				float r1 = MathHelper.nextFloat(random, 0.0F, ((float) Math.PI * 2F));
				double r2 = MathHelper.nextFloat(random, 0.75F, 1.0F);
				double velocityY = (double) 0.2F + fallDistance / (double) 100.0F;
				double velocityX = (double) (MathHelper.cos(r1) * 0.2F) * r2 * r2 * (fallDistance + 0.2);
				double velocityZ = (double) (MathHelper.sin(r1) * 0.2F) * r2 * r2 * (fallDistance + 0.2);
				world.addParticle(
					particleS2CPacket.getType(),
					blockPos.getX() + 0.5F,
					blockPos.getY() + 1.0F,
					blockPos.getZ() + 0.5F,
					velocityX, velocityY, velocityZ,
					particleS2CPacket.getParameters() /* metadata for particle color */
				);
			}
		}
	}
}
