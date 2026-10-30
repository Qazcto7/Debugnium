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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-147574", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Attacking a group of evokers will give nearby evokers infinite detection range and broken attack behavior")
@Mixin(targets = "net.minecraft.world.entity.monster.illager.SpellcasterIllager$SpellcasterUseSpellGoal")
public class SpellcasterUseSpellGoalMixin {
	@Shadow
	@Final
	SpellcasterIllager this$0;

	@WrapOperation(
			method = "canUse",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;isAlive()Z"
			)
	)
	private boolean checkTargetValidAndInRange(LivingEntity target, Operation<Boolean> original) {
		if (!original.call(target)) {
			return false;
		}

		double followRange = this.this$0.getAttributeValue(Attributes.FOLLOW_RANGE);
		if (this.this$0.level() != target.level() || this.this$0.distanceToSqr(target) > followRange * followRange) {
			this.this$0.setTarget(null);
			return false;
		}

		return true;
	}
}
