package com.jelte.norii.multiplayer;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;

public class MyWebSocketAdapter implements WebSocketListener {

	@Override
	public boolean onOpen(WebSocket webSocket) {
		return FULLY_HANDLED;
	}

	@Override
	public boolean onMessage(WebSocket webSocket, String packet) {
		NetworkMessage message = new NetworkMessage();
		message.importString(packet);
		ServerCommunicator.getInstance().addMessageFromServer(message);
		return FULLY_HANDLED;
	}

	@Override
	public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
		return FULLY_HANDLED;
	}

	@Override
	public boolean onMessage(final WebSocket webSocket, final byte[] packet) {
		return NOT_HANDLED;
	}

	@Override
	public boolean onError(final WebSocket webSocket, final Throwable error) {
		return NOT_HANDLED;
	}
}
