/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc185597;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BreakDoorGoal.class)
public abstract class BreakDoorGoalMixin extends DoorInteractGoal {
	public BreakDoorGoalMixin(Mob mob) {
		super(mob);
	}

	@Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
	private void checkDoorStillPresent(CallbackInfoReturnable<Boolean> cir) {
		if (!this.hasDoor || this.mob.isDeadOrDying() || this.mob.isRemoved()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z",
			shift = At.Shift.AFTER
		)
	)
	private void clearProgressOnBreak(CallbackInfo ci) {
		this.mob.level().destroyBlockProgress(this.mob.getId(), this.doorPos, -1);
	}
}
