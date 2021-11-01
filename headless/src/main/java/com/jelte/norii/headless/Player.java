package com.jelte.norii.headless;

public class Player {
	private ConnectedClient connectedClient;
	// private List<Entity> team;

	public Player(ConnectedClient r) {
		connectedClient = r;
	}

	public ConnectedClient getConnectedClient() {
		return connectedClient;
	}
}