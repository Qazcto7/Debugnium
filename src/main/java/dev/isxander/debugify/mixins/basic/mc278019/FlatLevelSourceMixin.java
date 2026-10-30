/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.mixins.basic.mc278019;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.isxander.debugify.fixes.BugFix;
import dev.isxander.debugify.fixes.FixCategory;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import org.spongepowered.asm.mixin.Mixin;

@BugFix(id = "MC-278019", category = FixCategory.BASIC, env = BugFix.Env.SERVER, description = "Superflat worlds have their sea level at Y=-63")
@Mixin(FlatLevelSource.class)
public class FlatLevelSourceMixin {
	@WrapMethod(method = "getSeaLevel")
	private int fixSeaLevel(Operation<Integer> original) {
		return 63;
	}
}
