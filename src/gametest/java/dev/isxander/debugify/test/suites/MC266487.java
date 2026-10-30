/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.gamerules.GameRules;

public class MC266487 {
	@GameTest
	public void testTrialSpawnerWorksWithDoMobSpawningFalse(GameTestHelper ctx) {
		BlockPos pos = new BlockPos(2, 2, 2);
		ctx.setBlock(pos, Blocks.TRIAL_SPAWNER);

		TrialSpawnerBlockEntity spawner = ctx.getBlockEntity(pos, TrialSpawnerBlockEntity.class);
		ctx.assertTrue(spawner != null, "Trial spawner block entity must exist");

		// Set doMobSpawning to false
		ctx.getLevel().getGameRules().set(GameRules.SPAWN_MOBS, false, ctx.getLevel().getServer());
		ctx.getLevel().getGameRules().set(GameRules.SPAWNER_BLOCKS_WORK, true, ctx.getLevel().getServer());

		// In vanilla, canSpawnInLevel returns false when doMobSpawning is false.
		// With MC-266487 fix, trial spawners ignore doMobSpawning just like regular spawners.
		ctx.assertTrue(spawner.getTrialSpawner().canSpawnInLevel(ctx.getLevel()), "Trial spawner should spawn mobs even when doMobSpawning is false");

		// Ensure spawnerBlocksWork is still respected
		ctx.getLevel().getGameRules().set(GameRules.SPAWNER_BLOCKS_WORK, false, ctx.getLevel().getServer());
		ctx.assertFalse(spawner.getTrialSpawner().canSpawnInLevel(ctx.getLevel()), "Trial spawner should still respect spawnerBlocksWork gamerule");

		// Reset gamerules
		ctx.getLevel().getGameRules().set(GameRules.SPAWN_MOBS, true, ctx.getLevel().getServer());
		ctx.getLevel().getGameRules().set(GameRules.SPAWNER_BLOCKS_WORK, true, ctx.getLevel().getServer());

		ctx.succeed();
	}
}
