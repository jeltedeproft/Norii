package com.jelte.norii.headless;


import com.badlogic.gdx.Gdx;

import io.vertx.core.http.ServerWebSocket;

public class ConnectedClient {
	private static final String TAG = ConnectedClient.class.getSimpleName();
	private ServerWebSocket socket;
	private String name;
	private ClientState clientState;
	private int currentGameID;

	public ConnectedClient(ServerWebSocket s) {
		socket = s;
		clientState = ClientState.NAMELESS;
		currentGameID = -1;
	}

	public ServerWebSocket getSocket() {
		return socket;
	}

	public String getPlayerName() {
		return name;
	}

	public ClientState getClientState() {
		return clientState;
	}

	public int getGameID() {
		return currentGameID;
	}

	public void setGameID(int id) {
		currentGameID = id;
	}

	public void setName(String nameToSet) {
		if (clientState == ClientState.NAMELESS) {
			name = nameToSet;
			clientState = ClientState.IDLE;
		} else {
			Gdx.app.error(TAG, "Tried to set a name for a client that already has one!");
		}
	}

	public void setClientState(ClientState state) {
		clientState = state;
	}
}
