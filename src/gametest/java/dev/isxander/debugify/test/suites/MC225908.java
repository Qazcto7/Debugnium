/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.lang.reflect.Method;
import java.util.Optional;

public class MC225908 {
	@GameTest
	public void testPiglinDoesNotTargetAlliedWitherSkeleton(GameTestHelper ctx) throws Exception {
		Piglin piglin = ctx.spawn(EntityTypes.PIGLIN, 2, 2, 2);
		WitherSkeleton skeleton = ctx.spawn(EntityTypes.WITHER_SKELETON, 3, 2, 2);

		Scoreboard scoreboard = ctx.getLevel().getScoreboard();
		PlayerTeam team = scoreboard.addPlayerTeam("piglin_allies");
		try {
			scoreboard.addPlayerToTeam(piglin.getStringUUID(), team);
			scoreboard.addPlayerToTeam(skeleton.getStringUUID(), team);

			ctx.assertTrue(piglin.isAlliedTo(skeleton), "Piglin and Wither Skeleton should be allied");

			// Set wither skeleton as nemesis in brain
			piglin.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, Optional.of(skeleton));

			Method findTargetMethod = PiglinAi.class.getDeclaredMethod("findNearestValidAttackTarget", ctx.getLevel().getClass(), Piglin.class);
			findTargetMethod.setAccessible(true);
			@SuppressWarnings("unchecked")
			Optional<? extends LivingEntity> target = (Optional<? extends LivingEntity>) findTargetMethod.invoke(null, ctx.getLevel(), piglin);

			ctx.assertTrue(target.isEmpty() || target.get() != skeleton, "Piglin should not target allied Wither Skeleton");
		} finally {
			scoreboard.removePlayerTeam(team);
		}

		ctx.succeed();
	}

	@GameTest
	public void testPiglinBruteDoesNotTargetAlliedWitherSkeleton(GameTestHelper ctx) throws Exception {
		PiglinBrute brute = ctx.spawn(EntityTypes.PIGLIN_BRUTE, 2, 2, 2);
		WitherSkeleton skeleton = ctx.spawn(EntityTypes.WITHER_SKELETON, 3, 2, 2);

		Scoreboard scoreboard = ctx.getLevel().getScoreboard();
		PlayerTeam team = scoreboard.addPlayerTeam("brute_allies");
		try {
			scoreboard.addPlayerToTeam(brute.getStringUUID(), team);
			scoreboard.addPlayerToTeam(skeleton.getStringUUID(), team);

			ctx.assertTrue(brute.isAlliedTo(skeleton), "Piglin Brute and Wither Skeleton should be allied");

			// Set wither skeleton as nemesis in brain
			brute.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, Optional.of(skeleton));

			Method findTargetMethod = PiglinBruteAi.class.getDeclaredMethod("findNearestValidAttackTarget", ctx.getLevel().getClass(), net.minecraft.world.entity.monster.piglin.AbstractPiglin.class);
			findTargetMethod.setAccessible(true);
			@SuppressWarnings("unchecked")
			Optional<? extends LivingEntity> target = (Optional<? extends LivingEntity>) findTargetMethod.invoke(null, ctx.getLevel(), brute);

			ctx.assertTrue(target.isEmpty() || target.get() != skeleton, "Piglin Brute should not target allied Wither Skeleton");
		} finally {
			scoreboard.removePlayerTeam(team);
		}

		ctx.succeed();
	}
}
