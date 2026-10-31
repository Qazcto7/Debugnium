/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.goat.Goat;

import java.lang.reflect.Method;

public class MC264204 {
	@GameTest
	public void testGoatKillCreditRecordedForPlayer(GameTestHelper ctx) throws Exception {
		ServerPlayer player = ctx.makeMockServerPlayerInLevel();
		Goat goat = ctx.spawn(EntityTypes.GOAT, 2, 2, 2);
		Pig pig = ctx.spawn(EntityTypes.PIG, 3, 2, 2);

		DamageSource damageSource = ctx.getLevel().damageSources().noAggroMobAttack(goat);

		Method resolveMobMethod = LivingEntity.class.getDeclaredMethod("resolveMobResponsibleForDamage", DamageSource.class);
		resolveMobMethod.setAccessible(true);

		// For player, goat damage should record goat as lastHurtByMob so kill credits, stats, and advancements work
		resolveMobMethod.invoke(player, damageSource);
		ctx.assertTrue(player.getLastHurtByMob() == goat, "Player's last hurt mob should be the goat even with noAggroMobAttack");

		// For non-player mob, noAggroMobAttack should NOT trigger anger / set lastHurtByMob
		resolveMobMethod.invoke(pig, damageSource);
		ctx.assertTrue(pig.getLastHurtByMob() == null, "Non-player mob should not have last hurt mob set by noAggroMobAttack");

		ctx.succeed();
	}
}
