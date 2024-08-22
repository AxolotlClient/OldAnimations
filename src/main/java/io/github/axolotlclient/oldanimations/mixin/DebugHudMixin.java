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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public class DebugHudMixin {

	@Inject(method = "renderLeftText", at = @At("HEAD"), cancellable = true)
	public void axolotlclient$renderLeftDebugText(CallbackInfo callback) {
		if (!isDebugOverlayEnabled())
			return;

		callback.cancel();

		TextRenderer text = client.textRenderer;

		String debug = client.fpsDebugString;
		debug = debug.substring(0, client.fpsDebugString.indexOf(") ") + 1);
		debug = debug.replace(" (", ", ");
		debug = '(' + debug;
		// *really*
		debug = debug.replace("update)", "updates)");
		debug = "Minecraft 1.8.9 " + debug;
		text.drawWithShadow(debug, 2, 2, -1);

		String chunksDebug = client.worldRenderer.getChunksDebugString();
		text.drawWithShadow(chunksDebug, 2, 12, -1);

		text.drawWithShadow(client.worldRenderer.getEntitiesDebugString(), 2, 22, -1);
		text.drawWithShadow(String.format("P: %s T: %s", client.particleManager.getDebugString(),
			client.world.addDetailsToCrashReport()), 2, 32, -1);
		text.drawWithShadow(client.world.getDebugString(), 2, 42, -1);

		PlayerEntity player = client.player;
		World world = client.world;

		double x = player.x;
		double y = player.y;
		double z = player.z;

		int xFloor = MathHelper.floor(x);
		int yFloor = MathHelper.floor(y);
		int zFloor = MathHelper.floor(z);

		text.drawWithShadow(String.format("x: %.5f (%d) // c: %d (%d)", x, xFloor, xFloor >> 4, xFloor & 15), 2, 64,
			0xE0E0E0);
		text.drawWithShadow(String.format("y: %.3f (feet pos, %.3f eyes pos)", y,
			y + client.player.getEyeHeight()), 2, 72, 0xE0E0E0);
		text.drawWithShadow(String.format("z: %.5f (%d) // c: %d (%d)", z, zFloor, zFloor >> 4, zFloor & 15), 2, 80,
			0xE0E0E0);

		int f = MathHelper.floor(player.yaw * 4F / 360F + 0.5) & 3;
		text.drawWithShadow(
			String.format("f: %d (%s) / %.5f", f, DIRECTIONS[f], MathHelper.wrapDegrees(client.player.yaw)), 2,
			88, 0xE0E0E0);

		BlockPos absPos = new BlockPos(xFloor, yFloor, zFloor);
		if (world != null && world.blockExists(absPos)) {
			Chunk chunk = world.getChunk(absPos);
			BlockPos relativePos = new BlockPos(xFloor & 15, yFloor, zFloor & 15);
			text.drawWithShadow(
				String.format("lc: %d b: %s bl: %d sl %d rl: %d", chunk.getHighestNonEmptySectionYOffset() + 15,
					chunk.getBiomeAt(absPos, world.getBiomeSource()).name,
					chunk.getLightAtPos(LightType.BLOCK, relativePos),
					chunk.getLightAtPos(LightType.SKY, relativePos), chunk.getLightLevel(relativePos, 0)),
				2, 96, 0xE0E0E0);
		}

		text.drawWithShadow(String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", player.abilities.getWalkSpeed(),
			player.abilities.getFlySpeed(), player.onGround,
			getHeightValue(world, xFloor, zFloor)), 2, 104, 0xE0E0E0);

		if (client.gameRenderer.areShadersSupported() && client.gameRenderer.getShader() != null)
			text.drawWithShadow(String.format("shader: %s", client.gameRenderer.getShader().getName()), 2, 112,
				0xE0E0E0);
	}

	@Inject(method = "renderRightText", at = @At("HEAD"), cancellable = true)
	public void axolotlclient$renderRightDebugText(Window window, CallbackInfo callback) {
		if (!isDebugOverlayEnabled())
			return;

		callback.cancel();

		TextRenderer text = client.textRenderer;
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long used = total - free;

		String usedString = String.format("Used memory: %d%% (%dMB) of %dMB", used * 100 / max, used / 1024 / 1024,
			max / 1024 / 1024);
		text.drawWithShadow(usedString, window.getWidth() - text.getStringWidth(usedString), 2, 0xE0E0E0);

		String allocatedString = String.format("Allocated memory: %d%% (%dMB)", total * 100 / max,
			total / 1024 / 1024);
		text.drawWithShadow(allocatedString, window.getWidth() - text.getStringWidth(allocatedString), 12,
			0xE0E0E0);
	}

	@Unique
	private static int getHeightValue(World world, int x, int y) {
		if (x >= -30000000 && y >= -30000000 && x < 30000000 && y < 30000000) {
			if (!world.getChunkProvider().chunkExists(x >> 4, y >> 4))
				return 0;

			Chunk chunk = world.getChunk(x >> 4, y >> 4);
			return chunk.getHighestBlockY(x & 15, y & 15);
		}

		return 64;
	}

	@Shadow
	@Final
	private MinecraftClient client;

	@Unique
	private static final String[] DIRECTIONS = {"SOUTH", "WEST", "NORTH", "EAST"};

	@Unique
	private static boolean isDebugOverlayEnabled() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().debugOverlay.get();
	}

}
