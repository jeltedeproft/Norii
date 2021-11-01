package com.jelte.norii.headless;

public class Player {
	private ConnectedClient connectedClient;
	private Move lastMove;
	// private List<Entity> team;

	public Player(ConnectedClient r) {
		connectedClient = r;
	}

	public ConnectedClient getConnectedClient() {
		return connectedClient;
	}
	
	public void makeMove(Move move) {
		lastMove = move;
	}
	
	public void refreshMove() {
		lastMove = null;
	}
	
	public Move getMove() {
		return lastMove;
	}
}