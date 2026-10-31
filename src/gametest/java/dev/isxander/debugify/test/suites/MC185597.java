/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.monster.zombie.Zombie;

public class MC185597 {
	static class TestBreakDoorGoal extends BreakDoorGoal {
		public TestBreakDoorGoal(Mob mob, BlockPos pos) {
			super(mob, diff -> true);
			this.doorPos = pos;
			this.hasDoor = true;
		}

		public void setHasDoor(boolean hasDoor) {
			this.hasDoor = hasDoor;
		}
	}

	@GameTest
	public void testDoorBreakingStopsWhenDoorGone(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 2, 1);
		Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, pos);
		TestBreakDoorGoal goal = new TestBreakDoorGoal(zombie, helper.absolutePos(pos.north()));

		goal.start();

		// When door is gone (hasDoor = false), goal must not continue to use
		goal.setHasDoor(false);
		if (goal.canContinueToUse()) {
			helper.fail("BreakDoorGoal continued to use even though door was gone");
		}

		// When mob is removed, running goals must stop
		((dev.isxander.debugify.mixins.basic.mc185597.MobAccessor) zombie).debugify$getGoalSelector().addGoal(1, goal);
		goal.setHasDoor(true);
		if (!goal.canContinueToUse()) {
			helper.fail("BreakDoorGoal should be able to continue when door is present");
		}

		zombie.remove(Entity.RemovalReason.KILLED);
		if (goal.canContinueToUse()) {
			helper.fail("BreakDoorGoal continued to use after mob was removed");
		}

		helper.succeed();
	}
}
