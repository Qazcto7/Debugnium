/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc279288;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-279288", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Shield occasionally does not block projectiles")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@WrapOperation(
			method = "applyItemBlocking",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")
	)
	private Vec3 fixFlightFromPosVector(Vec3 projectilePos, Vec3 entityPos, Operation<Vec3> original, @Local(argsOnly = true) DamageSource damageSource) {
		Vec3 fromVectorAdjustment = Vec3.ZERO;
		if (damageSource.getDirectEntity() instanceof Projectile projectile) {
			Vec3 deltaMovement = projectile.getDeltaMovement();
			double horizontalSize = Math.max(this.getBoundingBox().getXsize(), this.getBoundingBox().getZsize());
			fromVectorAdjustment = deltaMovement.normalize().scale(-2.0 * horizontalSize);
		}
		return original.call(projectilePos, entityPos).add(fromVectorAdjustment);
	}
}
