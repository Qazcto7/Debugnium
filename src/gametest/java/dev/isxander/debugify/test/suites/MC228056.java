/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

public class MC228056 {
	@GameTest
	public void testEntitiesNotUntrackedOnDeath(GameTestHelper ctx) throws Exception {
		ServerPlayer player = ctx.makeMockServerPlayerInLevel();
		player.setPos(ctx.absoluteVec(Vec3.atBottomCenterOf(new BlockPos(1, 1, 1))));

		Cow cow = ctx.spawn(EntityTypes.COW, 2, 1, 2);

		player.setHealth(0.0F);
		player.die(ctx.getLevel().damageSources().genericKill());

		ctx.assertTrue(player.isDeadOrDying(), "Player should be dead/on respawn screen");
		ctx.assertFalse(player.hasDisconnected(), "Player has not disconnected");

		Method removeEntityMethod = ctx.getLevel().getChunkSource().chunkMap.getClass().getDeclaredMethod("removeEntity", net.minecraft.world.entity.Entity.class);
		removeEntityMethod.setAccessible(true);
		removeEntityMethod.invoke(ctx.getLevel().getChunkSource().chunkMap, player);

		ctx.assertTrue(cow.isAlive(), "Cow should remain alive");
		ctx.succeed();
	}
}
