/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc98448;

import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@BugFix(id = "MC-98448", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Visible ghasts despawning")
@Mixin(Ghast.class)
public abstract class GhastMixin extends Mob {
	protected GhastMixin(EntityType<? extends Mob> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	public abstract boolean isCharging();

	@Override
	public boolean removeWhenFarAway(double distanceToSqr) {
		if (this.getTarget() != null || this.isCharging() || (this.getLastHurtByMob() != null && this.tickCount - this.getLastHurtByMobTimestamp() < 100)) {
			return false;
		}
		return super.removeWhenFarAway(distanceToSqr);
	}
}
