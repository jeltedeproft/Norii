package com.jelte.norii.headless;

import com.badlogic.gdx.utils.Array;

public class Player {
	private ConnectedClient connectedClient;
	private Move lastMove;
	private Array<String> team;

	public Player(ConnectedClient r) {
		connectedClient = r;
		team = r.getTeam();
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

	public Array<String> getTeam() {
		return team;
	}
}