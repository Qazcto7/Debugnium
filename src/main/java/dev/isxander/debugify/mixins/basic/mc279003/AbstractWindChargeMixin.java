/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc279003;

import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@BugFix(id = "MC-279003", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Explosions do not affect wind charges")
@Mixin(AbstractWindCharge.class)
public abstract class AbstractWindChargeMixin extends AbstractHurtingProjectile {
	protected AbstractWindChargeMixin(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "push(DDD)V", at = @At("HEAD"), cancellable = true)
	private void allowPush(double x, double y, double z, CallbackInfo ci) {
		super.push(x, y, z);
		ci.cancel();
	}
}
