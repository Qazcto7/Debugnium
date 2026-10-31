/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

public class MC2164 {
	@GameTest
	public void testEnderPearlDoesNotTeleportInsideBlock(GameTestHelper ctx) throws Exception {
		BlockPos wallPos = new BlockPos(2, 2, 2);
		ctx.setBlock(wallPos, Blocks.STONE.defaultBlockState());

		ServerPlayer player = ctx.makeMockServerPlayerInLevel();
		player.setPos(ctx.absoluteVec(Vec3.atBottomCenterOf(new BlockPos(0, 1, 0))));

		ThrownEnderpearl pearl = new ThrownEnderpearl(ctx.getLevel(), player, new ItemStack(Items.ENDER_PEARL));
		Vec3 hitVec = ctx.absoluteVec(new Vec3(2.0, 2.5, 2.5));
		Vec3 pearlPos = ctx.absoluteVec(new Vec3(1.95, 2.0, 2.5));
		pearl.setPos(pearlPos);
		pearl.setOldPosAndRot();

		BlockHitResult hitResult = new BlockHitResult(hitVec, Direction.WEST, ctx.absolutePos(wallPos), false);

		Method onHitMethod = ThrownEnderpearl.class.getDeclaredMethod("onHit", HitResult.class);
		onHitMethod.setAccessible(true);
		onHitMethod.invoke(pearl, hitResult);

		ctx.assertTrue(
				ctx.getLevel().noCollision(player, player.getBoundingBox()),
				"Player should not collide with solid blocks after ender pearl teleportation"
		);

		ctx.succeed();
	}
}
