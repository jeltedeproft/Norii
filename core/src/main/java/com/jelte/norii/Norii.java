package com.jelte.norii;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSockets;
import com.jelte.norii.multiplayer.MyWebSocketAdapter;
import com.jelte.norii.screen.ScreenEnum;
import com.jelte.norii.screen.ScreenManager;

public class Norii extends Game {
	public static final String GAME_IDENTIFIER = "com.jelte.norii";
	private static final String APP_LINK = "norii-ipmpb.ondigitalocean.app";
	private WebSocket socket;

	@Override
	public void create() {
		// Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		Gdx.app.setLogLevel(3);// debug
		initMultiplayer();
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN);
	}
	
	private void initMultiplayer() {
		socket = WebSockets.newSocket(WebSockets.toSecureWebSocketUrl(APP_LINK, 443));
		socket.setSendGracefully(true);
		socket.addListener(new MyWebSocketAdapter());
		socket.connect();
	}

	public WebSocket getSocket() {
		return socket;
	}

	@Override
	public void dispose() {
		this.getScreen().dispose();
	}
}