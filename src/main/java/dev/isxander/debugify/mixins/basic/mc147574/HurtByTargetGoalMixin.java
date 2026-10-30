/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc147574;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-147574", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Attacking a group of evokers will give nearby evokers infinite detection range and broken attack behavior")
@Mixin(HurtByTargetGoal.class)
public class HurtByTargetGoalMixin {
	@WrapOperation(
			method = "alertOther",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Mob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"
			)
	)
	private void checkTargetRangeBeforeAlerting(Mob other, LivingEntity target, Operation<Void> original) {
		if (target != null) {
			double followRange = other.getAttributeValue(Attributes.FOLLOW_RANGE);
			if (other.level() != target.level() || other.distanceToSqr(target) > followRange * followRange) {
				return;
			}
		}
		original.call(other, target);
	}
}
