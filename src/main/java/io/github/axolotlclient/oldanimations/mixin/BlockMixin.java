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

import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.FireBlock;
import net.minecraft.block.HopperBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {

	@Shadow
	@Final
	public static Block.Sounds WOOD_SOUNDS;

	@Inject(method = "setSounds", at = @At("RETURN"))
	private void axolotlclient$oldBlockSounds(Block.Sounds sounds, CallbackInfoReturnable<Block> cir) {
		if (OldAnimationsConfig.isEnabled()) {
			Block block = (Block) (Object) this;
			if (OldAnimationsConfig.instance.fireSound.get() && block instanceof FireBlock) {
				/* the sound of jumping around in fire uses the wood sound in 1.7 and prior :D */
				block.sounds = WOOD_SOUNDS;
			}

			if (OldAnimationsConfig.instance.hopperSound.get() && block instanceof HopperBlock) {
				/* why were these blocks using wood sounds LMFAOOO */
				block.sounds = WOOD_SOUNDS;
			}
		}
	}
}
