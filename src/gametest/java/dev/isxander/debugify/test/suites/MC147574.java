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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

public class MC147574 {
	@GameTest
	public void testEvokerAlertOthersIgnoresFarTarget(GameTestHelper ctx) throws Exception {
		Evoker evoker1 = ctx.spawnWithNoFreeWill(EntityTypes.EVOKER, new BlockPos(1, 1, 1));
		Evoker evoker2 = ctx.spawnWithNoFreeWill(EntityTypes.EVOKER, new BlockPos(2, 1, 1));

		// Make a mock player far away (30 blocks, evoker follow range is 12)
		Player player = ctx.makeMockPlayer(GameType.SURVIVAL);
		player.setPos(ctx.absoluteVec(Vec3.atCenterOf(new BlockPos(30, 1, 1))));

		HurtByTargetGoal goal = new HurtByTargetGoal(evoker1);
		Method alertOtherMethod = HurtByTargetGoal.class.getDeclaredMethod("alertOther", net.minecraft.world.entity.Mob.class, net.minecraft.world.entity.LivingEntity.class);
		alertOtherMethod.setAccessible(true);

		// Alert evoker2 with the far-away player
		alertOtherMethod.invoke(goal, evoker2, player);

		// With MC-147574 fix, evoker2 will not accept target far beyond follow range
		ctx.assertTrue(evoker2.getTarget() == null, "Evoker should not target player beyond follow range when alerted");

		ctx.succeed();
	}
}
