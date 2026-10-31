/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc2164;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@BugFix(id = "MC-2164", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Ender pearls can teleport entities inside solid blocks")
@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin {
	@Shadow
	public abstract Entity getOwner();

	@ModifyExpressionValue(
			method = "onHit",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrownEnderpearl;oldPosition()Lnet/minecraft/world/phys/Vec3;"
			)
	)
	private Vec3 adjustTargetPosToAvoidSuffocation(Vec3 original, HitResult hitResult) {
		if (hitResult instanceof BlockHitResult blockHitResult) {
			Entity owner = this.getOwner();
			ThrownEnderpearl self = (ThrownEnderpearl) (Object) this;
			if (owner != null && self.level() instanceof ServerLevel serverLevel) {
				Direction direction = blockHitResult.getDirection();
				Vec3 nudge = new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
				if (!serverLevel.noCollision(owner, owner.getDimensions(owner.getPose()).makeBoundingBox(original))) {
					for (double step = 0.05; step <= 0.6; step += 0.05) {
						Vec3 candidate = original.add(nudge.scale(step));
						if (serverLevel.noCollision(owner, owner.getDimensions(owner.getPose()).makeBoundingBox(candidate))) {
							return candidate;
						}
					}
				}
			}
		}
		return original;
	}
}
