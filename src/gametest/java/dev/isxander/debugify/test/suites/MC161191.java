/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MC161191 {
	@GameTest
	public void doubleSlabDropsTwoSlabs(GameTestHelper ctx) {
		BlockPos pos = new BlockPos(2, 2, 2);
		BlockState doubleSlab = Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);

		FallingBlockEntity entity = FallingBlockEntity.fall(ctx.getLevel(), ctx.absolutePos(pos), doubleSlab);
		entity.time = 601;

		ctx.runAfterDelay(3, () -> {
			List<ItemEntity> items = ctx.getLevel().getEntitiesOfClass(
					ItemEntity.class,
					AABB.encapsulatingFullBlocks(ctx.absolutePos(new BlockPos(0, 0, 0)), ctx.absolutePos(new BlockPos(4, 4, 4)))
			);

			ctx.assertTrue(!items.isEmpty(), "An item should have been dropped");
			int totalCount = items.stream().mapToInt(item -> item.getItem().getCount()).sum();
			ctx.assertTrue(totalCount == 2, "Double slab should have dropped 2 slabs, but dropped " + totalCount);
			ctx.succeed();
		});
	}
}
