/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class MC30946 {
	@GameTest
	public void testBeaconRegenerationPhasePreserved(GameTestHelper ctx) {
		// Test Regeneration I (interval = 50)
		// 260 % 50 = 10
		MobEffectInstance regen1 = new MobEffectInstance(MobEffects.REGENERATION, 260, 0);
		// Beacon refresh duration = 340 (340 % 50 = 40)
		MobEffectInstance beaconRefresh = new MobEffectInstance(MobEffects.REGENERATION, 340, 0);

		regen1.update(beaconRefresh);

		ctx.assertTrue(
				regen1.getDuration() % 50 == 10,
				"Regen duration modulo 50 should remain 10 after update, but was " + (regen1.getDuration() % 50)
		);

		// Test Regeneration II (interval = 25)
		// 115 % 25 = 15
		MobEffectInstance regen2 = new MobEffectInstance(MobEffects.REGENERATION, 115, 1);
		MobEffectInstance beaconRefresh2 = new MobEffectInstance(MobEffects.REGENERATION, 340, 1);

		regen2.update(beaconRefresh2);

		ctx.assertTrue(
				regen2.getDuration() % 25 == 15,
				"Regen II duration modulo 25 should remain 15 after update, but was " + (regen2.getDuration() % 25)
		);

		ctx.succeed();
	}
}
