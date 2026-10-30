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
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class MC98448 {
	@GameTest
	public void testGhastCombatDespawnPrevented(GameTestHelper ctx) {
		Ghast ghast = ctx.spawnWithNoFreeWill(EntityTypes.GHAST, new BlockPos(1, 1, 1));

		// Idle ghast should be eligible for normal despawning
		ctx.assertTrue(ghast.removeWhenFarAway(10000.0), "Idle ghast should be eligible for despawn when far away");

		// When charging a fireball, despawn should be prevented
		ghast.setCharging(true);
		ctx.assertFalse(ghast.removeWhenFarAway(10000.0), "Ghast should not despawn while charging in combat");

		// When targeting a player, despawn should also be prevented
		ghast.setCharging(false);
		Player player = ctx.makeMockPlayer(GameType.SURVIVAL);
		ghast.setTarget(player);
		ctx.assertFalse(ghast.removeWhenFarAway(10000.0), "Ghast should not despawn while targeting a player");

		ctx.succeed();
	}
}
