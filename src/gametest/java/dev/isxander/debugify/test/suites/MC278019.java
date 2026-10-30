/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class MC278019 {
	@GameTest
	public void superflatSeaLevelIs63(GameTestHelper ctx) {
		FlatLevelGeneratorSettings settings = FlatLevelGeneratorSettings.getDefault(
				ctx.getLevel().registryAccess().lookupOrThrow(Registries.BIOME),
				ctx.getLevel().registryAccess().lookupOrThrow(Registries.STRUCTURE_SET),
				ctx.getLevel().registryAccess().lookupOrThrow(Registries.PLACED_FEATURE)
		);
		FlatLevelSource source = new FlatLevelSource(settings);

		ctx.assertTrue(source.getSeaLevel() == 63, "FlatLevelSource sea level should be 63, but was " + source.getSeaLevel());
		ctx.succeed();
	}
}
