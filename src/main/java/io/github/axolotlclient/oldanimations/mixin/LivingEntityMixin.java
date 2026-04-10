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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.oldanimations.config.OldAnimationsConfig;
import io.github.axolotlclient.oldanimations.util.ducks.ILivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.ArmorStandEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.monster.GuardianEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntity {

	@Shadow
	public abstract float getHealth();

	@Unique
	private float axolotlclient$lastHealth;

	public LivingEntityMixin(World world) {
		super(world);
	}

	/* NOTE: the following two injections already exist in optifine, however, for people not using it, */
	/* i think it would be preferred to add an option in this mod seeing as it's relevant to 1.7 */

	@ModifyExpressionValue(method = "getRotationVec", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/living/LivingEntity;lastHeadYaw:F"))
	private float axolotlclient$usePrevYaw(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.rotationVecYawFix.get()) {
			/* don't use the prev head yaw as it is not accurate compared to prev yaw */
			original = lastYaw;
		}
		return original;
	}

	@ModifyExpressionValue(method = "getRotationVec", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/living/LivingEntity;headYaw:F"))
	private float axolotlclient$useYaw(float original) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.rotationVecYawFix.get()) {
			/* ditto but with yaw */
			original = yaw;
		}
		return original;
	}

	@Inject(method = "takeDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", ordinal = 1))
	private void axolotlclient$cacheLastHealth(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.heartFlashing.get()) {
			/* light work, no reaction */
			axolotlclient$lastHealth = getHealth();
		}
	}

	@WrapOperation(method = "moveRelative", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;isLocallyControlled()Z"))
	private boolean axolotlclient$clientSidedEntityMovement(LivingEntity instance, Operation<Boolean> original) {
		/* in 1.7, entity movement/velocity has a clientside prediction component whereas in 1.8, it's solely serverside */
		/* due to the nature of the prediction, and its conflict with entity position packets, entities have choppy movement */
		/* fyberoptic (the goat) gives more information on this in MC-69655 */
		/* funnily enough, squids have insanely responsive knockback :p */
		if (OldAnimationsConfig.isEnabled() && OldAnimationsConfig.instance.clientSideEntityMovement.get() &&
			/* these entities don't exist in 1.7 so we should just avoid messing with their position */
			Minecraft.getInstance().isSingleplayer() && /* this only makes sense to be available in singleplayer */
			!(instance instanceof ArmorStandEntity) && !(instance instanceof GuardianEntity)) {
			return true;
		}
		return original.call(instance);
	}

	@Override
	public float axolotlclient$getLastHealth() {
		return axolotlclient$lastHealth;
	}
}
