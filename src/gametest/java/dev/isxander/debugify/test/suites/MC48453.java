/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import java.lang.reflect.Method;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MC48453 {
	@GameTest
	public void testPickBlockCopiesVisualState(GameTestHelper helper) {
		BlockPos furnacePos = new BlockPos(1, 2, 1);
		BlockState litFurnace = Blocks.FURNACE.defaultBlockState().setValue(BlockStateProperties.LIT, true);
		helper.setBlock(furnacePos, litFurnace);

		ItemStack furnaceStack = new ItemStack(Items.FURNACE);
		try {
			Method addBlockData = ServerGamePacketListenerImpl.class.getDeclaredMethod(
				"addBlockDataToItem",
				BlockState.class,
				ServerLevel.class,
				BlockPos.class,
				ItemStack.class
			);
			addBlockData.setAccessible(true);

			addBlockData.invoke(null, litFurnace, helper.getLevel(), helper.absolutePos(furnacePos), furnaceStack);

			BlockItemStateProperties furnaceProps = furnaceStack.get(DataComponents.BLOCK_STATE);
			if (furnaceProps == null || !Boolean.TRUE.equals(furnaceProps.get(BlockStateProperties.LIT))) {
				helper.fail("Lit furnace did not retain LIT blockstate property when picked");
			}

			BlockPos bookshelfPos = new BlockPos(1, 2, 2);
			BlockState bookshelfState = Blocks.CHISELED_BOOKSHELF.defaultBlockState()
				.setValue(ChiseledBookShelfBlock.SLOT_0_OCCUPIED, true)
				.setValue(ChiseledBookShelfBlock.SLOT_3_OCCUPIED, true);
			helper.setBlock(bookshelfPos, bookshelfState);

			ItemStack bookshelfStack = new ItemStack(Items.CHISELED_BOOKSHELF);
			addBlockData.invoke(null, bookshelfState, helper.getLevel(), helper.absolutePos(bookshelfPos), bookshelfStack);

			BlockItemStateProperties bookshelfProps = bookshelfStack.get(DataComponents.BLOCK_STATE);
			if (bookshelfProps == null
				|| !Boolean.TRUE.equals(bookshelfProps.get(ChiseledBookShelfBlock.SLOT_0_OCCUPIED))
				|| !Boolean.TRUE.equals(bookshelfProps.get(ChiseledBookShelfBlock.SLOT_3_OCCUPIED))) {
				helper.fail("Chiseled bookshelf did not retain slot occupied blockstate properties when picked");
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to invoke addBlockDataToItem via reflection", e);
		}

		helper.succeed();
	}
}
