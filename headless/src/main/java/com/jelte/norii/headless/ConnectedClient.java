package com.jelte.norii.headless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.vertx.core.http.ServerWebSocket;

public class ConnectedClient {
	private static final String TAG = ConnectedClient.class.getSimpleName();
	private ServerWebSocket socket;
	private String name;
	private Array<String> team;
	private ClientState clientState;
	private int currentGameID;
	private final Json json;

	public ConnectedClient(ServerWebSocket s) {
		json = new Json();
		socket = s;
		clientState = ClientState.NAMELESS;
		currentGameID = -1;
		name = s.textHandlerID();
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

	public void setTeamFromJson(String team) {
		this.team = json.fromJson(Array.class, team);
	}

	public Array<String> getTeam() {
		return team;
	}
}
