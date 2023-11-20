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
import net.minecraft.client.gui.overlay.DebugOverlay;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugOverlay.class)
public class DebugHudMixin {

	@Inject(method = "drawGameInfo", at = @At("HEAD"), cancellable = true)
	public void render(CallbackInfo callback) {
		if (!active())
			return;

		callback.cancel();

		TextRenderer text = minecraft.textRenderer;

		String debug = minecraft.fpsDebugString;
		debug = debug.substring(0, minecraft.fpsDebugString.indexOf(") ") + 1);
		debug = debug.replace(" (", ", ");
		debug = '(' + debug;
		// *really*
		debug = debug.replace("update)", "updates)");
		debug = "Minecraft 1.8.9 " + debug;
		text.drawWithShadow(debug, 2, 2, -1);

		String chunksDebug = minecraft.worldRenderer.getChunkDebugInfo();
		text.drawWithShadow(chunksDebug, 2, 12, -1);

		text.drawWithShadow(minecraft.worldRenderer.getEntityDebugInfo(), 2, 22, -1);
		text.drawWithShadow(String.format("P: %s T: %s", minecraft.particleManager.getParticlesDebugInfo(),
			minecraft.world.getEntitiesDebugInfo()), 2, 32, -1);
		text.drawWithShadow(minecraft.world.getChunkSourceDebugInfo(), 2, 42, -1);

		PlayerEntity player = minecraft.player;
		World world = minecraft.world;

		double x = player.x;
		double y = player.y;
		double z = player.z;

		int xFloor = MathHelper.floor(x);
		int yFloor = MathHelper.floor(y);
		int zFloor = MathHelper.floor(z);

		text.drawWithShadow(String.format("x: %.5f (%d) // c: %d (%d)", x, xFloor, xFloor >> 4, xFloor & 15), 2, 64,
			0xE0E0E0);
		text.drawWithShadow(String.format("y: %.3f (feet pos, %.3f eyes pos)", y,
			y + minecraft.player.getEyeHeight()), 2, 72, 0xE0E0E0);
		text.drawWithShadow(String.format("z: %.5f (%d) // c: %d (%d)", z, zFloor, zFloor >> 4, zFloor & 15), 2, 80,
			0xE0E0E0);

		int f = MathHelper.floor(player.yaw * 4F / 360F + 0.5) & 3;
		text.drawWithShadow(
			String.format("f: %d (%s) / %.5f", f, DIRECTIONS[f], MathHelper.wrapDegrees(minecraft.player.yaw)), 2,
			88, 0xE0E0E0);

		BlockPos absPos = new BlockPos(xFloor, yFloor, zFloor);
		if (world != null && world.isChunkLoaded(absPos)) {
			WorldChunk chunk = world.getChunk(absPos);
			BlockPos relativePos = new BlockPos(xFloor & 15, yFloor, zFloor & 15);
			text.drawWithShadow(
				String.format("lc: %d b: %s bl: %d sl %d rl: %d", chunk.getHighestSectionOffset() + 15,
					chunk.getBiome(absPos, world.getBiomeSource()).name,
					chunk.getLight(LightType.BLOCK, relativePos),
					chunk.getLight(LightType.SKY, relativePos), chunk.getLight(relativePos, 0)),
				2, 96, 0xE0E0E0);
		}

		text.drawWithShadow(String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", player.abilities.getWalkSpeed(),
			player.abilities.getFlySpeed(), player.onGround,
			getHeightValue(world, xFloor, zFloor)), 2, 104, 0xE0E0E0);

		if (minecraft.gameRenderer.hasShader() && minecraft.gameRenderer.getShader() != null)
			text.drawWithShadow(String.format("shader: %s", minecraft.gameRenderer.getShader().getName()), 2, 112,
				0xE0E0E0);
	}

	@Inject(method = "drawSystemInfo", at = @At("HEAD"), cancellable = true)
	public void render(Window window, CallbackInfo callback) {
		if (!active())
			return;

		callback.cancel();

		TextRenderer text = minecraft.textRenderer;
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long used = total - free;

		String usedString = String.format("Used memory: %d%% (%dMB) of %dMB", used * 100 / max, used / 1024 / 1024,
			max / 1024 / 1024);
		text.drawWithShadow(usedString, window.getWidth() - text.getWidth(usedString), 2, 0xE0E0E0);

		String allocatedString = String.format("Allocated memory: %d%% (%dMB)", total * 100 / max,
			total / 1024 / 1024);
		text.drawWithShadow(allocatedString, window.getWidth() - text.getWidth(allocatedString), 12,
			0xE0E0E0);
	}

	@Unique
	private static int getHeightValue(World world, int x, int y) {
		if (x >= -30000000 && y >= -30000000 && x < 30000000 && y < 30000000) {
			if (!world.getChunkSource().hasChunk(x >> 4, y >> 4))
				return 0;

			WorldChunk chunk = world.getChunkAt(x >> 4, y >> 4);
			return chunk.getHeight(x & 15, y & 15);
		}

		return 64;
	}

	@Shadow
	private @Final Minecraft minecraft;

	@Unique
	private static final String[] DIRECTIONS = {"SOUTH", "WEST", "NORTH", "EAST"};

	@Unique
	private static boolean active() {
		return OldAnimations.getInstance().enabled.get() && OldAnimations.getInstance().debugOverlay.get();
	}

}
