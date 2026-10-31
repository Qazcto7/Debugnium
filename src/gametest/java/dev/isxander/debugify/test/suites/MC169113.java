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
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

public class MC169113 {
	@GameTest
	public void testDeadPlayerNotUntrackedFromChunkMap(GameTestHelper ctx) throws Exception {
		ServerPlayer player = ctx.makeMockServerPlayerInLevel();
		player.setPos(ctx.absoluteVec(Vec3.atBottomCenterOf(new BlockPos(1, 1, 1))));
		player.setHealth(0.0F);
		player.die(ctx.getLevel().damageSources().genericKill());

		ctx.assertTrue(player.isDeadOrDying(), "Player should be dead/on respawn screen");
		ctx.assertFalse(player.hasDisconnected(), "Player has not disconnected");

		Method removeEntityMethod = ctx.getLevel().getChunkSource().chunkMap.getClass().getDeclaredMethod("removeEntity", net.minecraft.world.entity.Entity.class);
		removeEntityMethod.setAccessible(true);
		// With MC-169113 patch, removing a dead connected player does not remove their chunk tracking status
		removeEntityMethod.invoke(ctx.getLevel().getChunkSource().chunkMap, player);

		ctx.succeed();
	}
}
