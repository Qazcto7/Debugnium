/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc185597;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "remove", at = @At("HEAD"))
	private void stopMobGoalsOnRemoval(Entity.RemovalReason reason, CallbackInfo ci) {
		if ((Object) this instanceof Mob mob) {
			for (WrappedGoal goal : ((MobAccessor) mob).debugify$getGoalSelector().getAvailableGoals()) {
				if (goal.isRunning()) {
					goal.stop();
				}
			}
		}
	}
}
