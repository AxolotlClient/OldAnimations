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

import net.minecraft.client.render.texture.TextureAtlasSprite;

/* we need to negate one of the texture coords just like 1.7 did for the glint */
public final class CustomTextureAtlasSprite extends TextureAtlasSprite {
    public static final CustomTextureAtlasSprite INSTANCE = new CustomTextureAtlasSprite();

    private CustomTextureAtlasSprite() {
        super(null);
    }

    @Override
    public float getU(double u) {
        return (float) (-u / 16.0);
    }

    @Override
    public float getV(double v) {
        return (float) (v / 16.0);
    }
}
