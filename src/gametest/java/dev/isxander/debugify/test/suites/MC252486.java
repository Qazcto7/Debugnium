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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.StartAdmiringItemIfSeen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MC252486 {
	@GameTest
	public void testPiglinDoesNotAdmireDistantItem(GameTestHelper ctx) {
		Piglin piglin = ctx.spawnWithNoFreeWill(EntityTypes.PIGLIN, new BlockPos(1, 1, 1));
		BehaviorControl<LivingEntity> behavior = StartAdmiringItemIfSeen.create(119);

		// Distant gold item (12 blocks away, > 9 blocks)
		ItemEntity distantItem = new ItemEntity(ctx.getLevel(), piglin.getX() + 12.0, piglin.getY(), piglin.getZ(), new ItemStack(Items.GOLD_INGOT));
		piglin.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, distantItem);

		boolean startedDistant = behavior.tryStart(ctx.getLevel(), piglin, ctx.getLevel().getGameTime());
		ctx.assertFalse(startedDistant, "Behavior should not trigger for distant item (> 9 blocks)");
		ctx.assertFalse(piglin.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_ITEM), "Piglin should not admire item farther than 9 blocks away");

		// Close gold item (4 blocks away, <= 9 blocks)
		ItemEntity closeItem = new ItemEntity(ctx.getLevel(), piglin.getX() + 4.0, piglin.getY(), piglin.getZ(), new ItemStack(Items.GOLD_INGOT));
		piglin.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, closeItem);

		boolean startedClose = behavior.tryStart(ctx.getLevel(), piglin, ctx.getLevel().getGameTime());
		ctx.assertTrue(startedClose, "Behavior should trigger for close item (<= 9 blocks)");
		ctx.assertTrue(piglin.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_ITEM), "Piglin should admire close item");

		ctx.succeed();
	}
}
