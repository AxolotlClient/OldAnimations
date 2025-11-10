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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.Callable;

@Mixin(CrashReport.class)
public class CrashReportMixin {

	@WrapOperation(method = "fillSystemDetails", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/crash/CrashReportCategory;add(Ljava/lang/String;Ljava/util/concurrent/Callable;)V", ordinal = 0))
	private void axolotlclient$spoofCrashVersion(CrashReportCategory instance, String string, Callable<String> callable, Operation<Void> original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.show1_7_10.get()) {
			/* blame me for any future confusions ;) */
			/* on a real note, this injection is kinda poop */
			callable = () -> "1.7.10";
		}
		original.call(instance, string, callable);
	}
}
