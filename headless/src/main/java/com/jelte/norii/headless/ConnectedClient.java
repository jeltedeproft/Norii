package com.jelte.norii.headless;

import java.sql.Connection;

import com.badlogic.gdx.Gdx;

public class ConnectedClient {
	private static final String TAG = ConnectedClient.class.getSimpleName();
	private final Connection connection;
	private String name;
	private ClientState clientState;
	private int currentGameID;

	public ConnectedClient(Connection c) {
		connection = c;
		clientState = ClientState.NAMELESS;
		currentGameID = -1;
	}

	public Connection getConnection() {
		return connection;
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
