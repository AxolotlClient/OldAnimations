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

import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.world.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class DebugComponents {

	private static final Minecraft mc = Minecraft.getInstance();

	public static List<String> getLeft() {
		List<String> list = new ArrayList<>();
		/* gotta make sure it all matches haha */
		list.add("Minecraft " + (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.show1_7_10.get() ? "1.7.10" : "1.8.9") + " (" + Minecraft.getCurrentFps() + " fps, " + RenderChunk.updateCounter + " chunk updates)");
		list.add(mc.worldRenderer.getChunkDebugInfo()); /* this will return different data than in 1.7 unfortunately */
		list.add(mc.worldRenderer.getEntityDebugInfo());
		list.add("P: " + mc.particleManager.getDebugInfo() + ". T: " + mc.world.getDebugInfo());
		list.add(mc.world.getChunkSourceDebugInfo());
		list.add("");
		return list;
	}

	/* this code was converted from kotlin to java. i am too lazy to make this readable tbh */
	public static List<String> getLeftBottom() {
		BlockPos blockpos = new BlockPos(mc.getCamera().x, mc.getCamera().getShape().minY, mc.getCamera().z);
		Entity entity = mc.getCamera();
		Direction enumfacing = entity.getHorizontalFacing();
		WorldChunk chunk = mc.world.getChunk(blockpos);
		List<String> list = new ArrayList<>();
		double playerPosX = mc.player.x;
		double playerPosY = mc.player.y;
		double playerPosZ = mc.player.z;
		String var13 = "x: %.5f (%d) // c: %d (%d)";
		Object[] var14 = new Object[]{playerPosX, MathHelper.floor(playerPosX), MathHelper.floor(playerPosX) >> 4, MathHelper.floor(playerPosX) & 15};
		list.add(String.format(var13, Arrays.copyOf(var14, var14.length)));
		var13 = "y: %.3f (feet pos, %.3f eyes pos)";
		/* this eyeheight stuff is actually directly a fix for MC-51150... */
		var14 = new Object[]{mc.player.getShape().minY, playerPosY + PlayerUtil.INSTANCE.getEyeHeight()};
		list.add(String.format(var13, Arrays.copyOf(var14, var14.length)));
		var13 = "z: %.5f (%d) // c: %d (%d)";
		var14 = new Object[]{playerPosZ, MathHelper.floor(playerPosZ), MathHelper.floor(playerPosZ) >> 4, MathHelper.floor(playerPosZ) & 15};
		list.add(String.format(var13, Arrays.copyOf(var14, var14.length)));
		StringBuilder var32 = (new StringBuilder()).append("f: ").append(MathHelper.floor((double)(mc.player.yaw * 4.0F / 360.0F) + (double)0.5F) & 3).append(" (");
		String enumfacingString = enumfacing.toString();
		enumfacingString = enumfacingString.toUpperCase(Locale.ROOT);
		list.add(var32.append(enumfacingString).append(") / ").append(MathHelper.wrapDegrees(mc.player.yaw)).toString());

		try {
			int light = chunk.getLight(blockpos, 0);
			String biomeName = chunk.getBiome(blockpos, mc.world.getBiomeSource()).name;
			int blockLight = chunk.getLight(LightType.BLOCK, blockpos);
			int skyLight = chunk.getLight(LightType.SKY, blockpos);
			list.add("lc: " + light + " b: " + biomeName + " bl: " + blockLight + " sl: " + skyLight + " rl: " + light);
		} catch (Exception e) {
			/* yeah, in 1.7, this actually is empty when ur in the void LMFAO */
			list.add("");
		}

		String var17 = "ws: %.3f, fs: %.3f, g: %b, fl: %.0f";
		Object[] var18 = new Object[]{mc.player.abilities.getWalkSpeed(), mc.player.abilities.getFlySpeed(), mc.player.onGround, playerPosY};
		String var33 = String.format(var17, Arrays.copyOf(var18, var18.length));
		list.add(var33);
		GameRenderer gameRenderer = mc.gameRenderer;
		if (gameRenderer != null) {
			gameRenderer = gameRenderer.hasShader() ? gameRenderer : null;
			if (gameRenderer != null) {
				list.add("shader: " + gameRenderer.getShader().getName());
			}
		}
		return list;
	}

	public static List<String> getRight() {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long usedMemory = totalMemory - freeMemory;
		long usedMemoryMb = bytesToMb(usedMemory);
		long maxMemoryMb = bytesToMb(maxMemory);
		long totalMemoryMb = bytesToMb(totalMemory);
		List<String> list = new ArrayList<>();
		list.add("Used memory: " + usedMemory * 100L / maxMemory + "% (" + usedMemoryMb + "MB) of " + maxMemoryMb + "MB");
		list.add("Allocated memory: " + totalMemory * 100L / maxMemory + "% (" + totalMemoryMb + "MB)");
		list.add("");
		return list;
	}

	private static long bytesToMb(long bytes) {
		return bytes / 1024L / 1024L;
	}
}
