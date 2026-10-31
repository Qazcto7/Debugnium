/*
 * Copyright (C) 2026 The Debugify Contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.debugify.test.suites;

import dev.isxander.debugify.mixins.basic.mc248335.ConnectionAccessor;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.Field;

public class MC248335 {
	@GameTest
	public void testHandleDisconnectionNotCalledTwice(GameTestHelper ctx) throws Exception {
		ServerPlayer player = ctx.makeMockServerPlayerInLevel();

		Field connectionField = player.connection.getClass().getSuperclass().getDeclaredField("connection");
		connectionField.setAccessible(true);
		Connection connection = (Connection) connectionField.get(player.connection);

		ctx.assertTrue(connection != null, "Connection should not be null");

		ConnectionAccessor accessor = (ConnectionAccessor) connection;

		connection.disconnect(Component.literal("test"));
		connection.handleDisconnection();
		ctx.assertTrue(accessor.debugify$isDisconnectionHandled(), "Disconnection should be handled");

		connection.handleDisconnection();
		ctx.assertTrue(accessor.debugify$isDisconnectionHandled(), "Disconnection should remain handled");

		ctx.succeed();
	}
}
