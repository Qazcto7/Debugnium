/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class MC171420 {
	@GameTest
	public void testOpNotKickedByEnforceWhitelist(GameTestHelper ctx) {
		MinecraftServer server = ctx.getLevel().getServer();
		ServerPlayer opPlayer = ctx.makeMockServerPlayerInLevel();

		boolean prevEnforce = server.isEnforceWhitelist();
		boolean prevUsing = server.isUsingWhitelist();

		try {
			server.getPlayerList().op(opPlayer.nameAndId());
			server.setUsingWhitelist(true);
			server.setEnforceWhitelist(true);

			// opPlayer is OP, but NOT on whitelist.
			// kickUnlistedPlayers should NOT disconnect the OP player with MC-171420 patch!
			server.kickUnlistedPlayers();

			ctx.assertFalse(opPlayer.hasDisconnected(), "OP player should not be kicked when enforce whitelist is enabled");
		} finally {
			server.setEnforceWhitelist(prevEnforce);
			server.setUsingWhitelist(prevUsing);
			server.getPlayerList().deop(opPlayer.nameAndId());
		}

		ctx.succeed();
	}
}
