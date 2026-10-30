/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class MC102949 {
	@GameTest
	public void testUntamedSkeletonHorseInteraction(GameTestHelper ctx) {
		SkeletonHorse horse = ctx.spawnWithNoFreeWill(EntityTypes.SKELETON_HORSE, new BlockPos(2, 2, 2));
		horse.setTrap(false);

		Player player = ctx.makeMockPlayer(GameType.SURVIVAL);
		InteractionResult result = horse.mobInteract(player, InteractionHand.MAIN_HAND);

		ctx.assertTrue(result.consumesAction(), "Interacting with untamed skeleton horse should consume action / mount horse");

		ctx.succeed();
	}
}
