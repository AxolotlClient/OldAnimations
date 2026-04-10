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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.platform.GlStateManager;

public class OldButtonWidget extends ButtonWidget {

	public OldButtonWidget(int i, int j, int k, int l, int m, String string) {
		super(i, j, k, l, m, string);
	}

	@Override
	public void render(Minecraft minecraft, int i, int j) {
		/* axolotlclient actually fixes some issues with the button rendering */
		/* i want those issues tho :p they're important to be 1:1 with 1.7 */
		if (visible) {
			TextRenderer textRenderer = minecraft.textRenderer;
			minecraft.getTextureManager().bind(WIDGETS_LOCATION);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = i >= x && j >= y && i < x + width && j < y + height;
			int k = getYImage(hovered);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			drawTexture(x, y, 0, 46 + k * 20, width / 2, height);
			drawTexture(x + width / 2, y, 200 - width / 2, 46 + k * 20, width / 2, height);
			renderBackground(minecraft, i, j);
			int l = 14737632;
			if (!active) {
				l = 10526880;
			} else if (hovered) {
				l = 16777120;
			}
			drawCenteredString(textRenderer, message, x + width / 2, y + (height - 8) / 2, l);
		}
	}

}
