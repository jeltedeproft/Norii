package com.jelte.norii.multiplayer;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.jelte.norii.ai.AITeams;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.screen.ScreenEnum;
import com.jelte.norii.screen.ScreenManager;
import com.jelte.norii.utility.AssetManagerUtility;

public class MyWebSocketAdapter implements WebSocketListener {
	private boolean loginValidated = false;
	
	@Override
	public boolean onOpen(WebSocket webSocket) {
		System.out.println("connected: ");
		return FULLY_HANDLED;
	}

	@Override
	public boolean onMessage(WebSocket webSocket, String packet) {
		NetworkMessage message = new NetworkMessage();
		message.importString(packet);
		System.out.println("received message :" + packet + "\n");
		switch (message.getType()) {
		case BATTLE:
			AITeams selectedLevel = AITeams.ONLINE_PLAYER;
			AssetManagerUtility.loadMapAsset(MapType.BATTLE_MAP_THE_DARK_SWAMP.toString());
			ScreenManager.getInstance().showScreen(ScreenEnum.BATTLE, selectedLevel);
			break;
		case LOGIN_VALIDATION:
			loginValidated = "true".equals(message.getLoginWorked());
			break;
		default:
			break;
		}
		return FULLY_HANDLED;
	}

	@Override
	public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
		System.out.println("Disconnected: " + reason + "\n");
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

	public boolean isLoginValidated() {
		return loginValidated;
	}

	public void setLoginValidated(boolean loginValidated) {
		this.loginValidated = loginValidated;
	}

}
