/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;

public class MC279003 {
	@GameTest
	public void testWindChargePushed(GameTestHelper ctx) {
		WindCharge windCharge = ctx.spawn(EntityTypes.WIND_CHARGE, new BlockPos(1, 1, 1));
		windCharge.push(1.0, 0.5, 0.0);

		ctx.assertTrue(windCharge.getDeltaMovement().x > 0.0, "Wind charge should be affected by push / explosions");
		ctx.succeed();
	}
}
