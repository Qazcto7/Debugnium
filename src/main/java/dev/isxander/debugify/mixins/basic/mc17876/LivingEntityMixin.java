/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc17876;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-17876", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Entities and players extra hearts granted by attributes reset upon reloading world")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	@Final
	private static EntityDataAccessor<Float> DATA_HEALTH_ID;

	@WrapOperation(
			method = "readAdditionalSaveData",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"
			)
	)
	private void preserveHealthOnLoad(LivingEntity instance, float health, Operation<Void> original) {
		// Do not clamp health to base max health before attribute modifiers/equipment have finished loading
		instance.getEntityData().set(DATA_HEALTH_ID, Math.max(0.0F, health));
	}
}
