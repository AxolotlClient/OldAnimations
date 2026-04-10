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

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.platform.GlStateManager;

import java.nio.FloatBuffer;

public class DamageTint {

	/* methods are both adapted from 1.7 */

	public static void setDamageTint(FloatBuffer buffer) {
		Minecraft.getInstance().gameRenderer.disableLightMap();
		GlStateManager.disableTexture();
		GlStateManager.disableAlphaTest();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 771);
		GlStateManager.depthFunc(514);
		GlStateManager.color4f(buffer.get(0), buffer.get(1), buffer.get(2), buffer.get(3) + 0.1f /* matches 1.7 */);
	}

	public static void unsetDamageTint() {
		GlStateManager.depthFunc(515);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
		Minecraft.getInstance().gameRenderer.enableLightMap();
	}
}
