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
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.animal.fish.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.fish.Cod;
import net.minecraft.world.level.block.Blocks;

import java.lang.reflect.Field;

public class MC182763 {
	@GameTest
	public void testSchoolLeaderDoesNotBecomeFollower(GameTestHelper ctx) throws Exception {
		Cod fishA = ctx.spawnWithNoFreeWill(EntityTypes.COD, new BlockPos(2, 2, 2));
		Cod fishC = ctx.spawnWithNoFreeWill(EntityTypes.COD, new BlockPos(2, 2, 2));

		// Set fishA to already have a full school (8 fish)
		Field schoolSizeField = AbstractSchoolingFish.class.getDeclaredField("schoolSize");
		schoolSizeField.setAccessible(true);
		schoolSizeField.setInt(fishA, 8);

		ctx.assertTrue(fishA.hasFollowers(), "fishA should have followers");
		ctx.assertFalse(fishA.canBeFollowed(), "fishA should have a full school and cannot be followed");
		ctx.assertFalse(fishA.isFollower(), "fishA is initially a leader, not a follower");

		// Run flock leader goal for fishC
		FollowFlockLeaderGoal goalC = new FollowFlockLeaderGoal(fishC);
		Field nextStartTickField = FollowFlockLeaderGoal.class.getDeclaredField("nextStartTick");
		nextStartTickField.setAccessible(true);
		nextStartTickField.setInt(goalC, 0);

		goalC.canUse();

		// In vanilla, fishA would become a follower of fishC even though fishA is already a leader,
		// causing fishA to freeze. With MC-182763 patch, fish with followers are not made followers.
		ctx.assertFalse(fishA.isFollower(), "fishA should NOT become a follower because it already has followers");

		ctx.succeed();
	}

	@GameTest
	public void testFishSwimsWithHighNoActionTime(GameTestHelper ctx) {
		for (int x = 1; x <= 3; x++) {
			for (int y = 1; y <= 3; y++) {
				for (int z = 1; z <= 3; z++) {
					ctx.setBlock(x, y, z, Blocks.WATER);
				}
			}
		}

		Cod fish = ctx.spawnWithNoFreeWill(EntityTypes.COD, new BlockPos(2, 2, 2));
		fish.setNoActionTime(200);

		RandomSwimmingGoal goal = new RandomSwimmingGoal(fish, 1.0, 1);
		ctx.assertTrue(goal.canUse(), "Fish swim goal should be able to run even when noActionTime >= 100");

		ctx.succeed();
	}
}
