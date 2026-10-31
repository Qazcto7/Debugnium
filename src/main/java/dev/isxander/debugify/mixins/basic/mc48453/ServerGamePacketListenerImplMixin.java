/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc48453;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	@Inject(method = "addBlockDataToItem", at = @At("RETURN"))
	private static void copyBlockStateTextures(BlockState state, ServerLevel level, BlockPos pos, ItemStack itemStack, CallbackInfo ci) {
		BlockItemStateProperties properties = itemStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
		for (Property<?> property : state.getProperties()) {
			if (property == BlockStateProperties.LIT || ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.contains(property)) {
				properties = properties.with(property, state);
			}
		}
		if (!properties.isEmpty()) {
			itemStack.set(DataComponents.BLOCK_STATE, properties);
		}
	}
}
