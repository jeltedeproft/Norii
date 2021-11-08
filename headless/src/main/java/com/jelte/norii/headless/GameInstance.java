package com.jelte.norii.headless;

import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.map.Map;
import com.jelte.norii.multiplayer.NetworkMessage;

/**
 * An instance of a norii battle Used for the server to keep track of individual
 * games
 */
public class GameInstance {
	private GameServer gameServer;
	private int gameID;
	private Player player1;
	private Player player2;
	private Map map;
	private BattleState statePlayer1;
	private BattleState statePlayer2;

	public static final int SCORE_LIMIT = 3;

	public GameInstance(GameServer server, int id, ConnectedClient client1, ConnectedClient client2) {
		gameServer = server;
		gameID = id;
		player1 = new Player(client1);
		player2 = new Player(client2);
		statePlayer1 = new BattleState();
		// maybe use state pattern, for different stages of battle, or reuse from
		// client?
		// deploymentphase where every turn player gets to place a unit
		// how many max units?? things to decide
	}

	private void initTeams() {
		// TODO Auto-generated method stub

	}

	private void sendMessagePlayer1(NetworkMessage message) {
		player1.getConnectedClient().getSocket().writeFinalTextFrame(message.messageToString());
	}

	private void sendMessagePlayer2(NetworkMessage message) {
		player2.getConnectedClient().getSocket().writeFinalTextFrame(message.messageToString());
	}

}