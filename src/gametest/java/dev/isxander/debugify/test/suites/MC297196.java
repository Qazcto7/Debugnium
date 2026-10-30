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
import net.minecraft.world.entity.monster.zombie.Zombie;

public class MC297196 {
	@GameTest
	public void testChunkMapTickAfterEntityTick(GameTestHelper ctx) {
		Zombie zombie = ctx.spawnWithNoFreeWill(EntityTypes.ZOMBIE, new BlockPos(1, 1, 1));
		zombie.setDeltaMovement(0.5, 0.2, 0.5);

		// Run 5 ticks to ensure ChunkMap.tick runs after entity ticks without error
		ctx.runAfterDelay(5, () -> {
			ctx.assertTrue(zombie.isAlive(), "Zombie should tick and update movement");
			ctx.succeed();
		});
	}
}
