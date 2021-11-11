package com.jelte.norii.headless;

import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.map.MapFactory.MapType;
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
	private MapType map;
	private BattleState statePlayer1;
	private BattleState statePlayer2;

	public static final int SCORE_LIMIT = 3;

	public GameInstance(GameServer server, int id, ConnectedClient client1, ConnectedClient client2, MapType map) {
		gameServer = server;
		gameID = id;
		player1 = new Player(client1);
		player2 = new Player(client2);
		this.map = map;
		// maybe use state pattern, for different stages of battle, or reuse from
		// client?
		// deploymentphase where every turn player gets to place a unit
		// how many max units?? things to decide
	}

	private void sendMessagePlayer1(NetworkMessage message) {
		player1.getConnectedClient().getSocket().writeFinalTextFrame(message.messageToString());
	}

	private void sendMessagePlayer2(NetworkMessage message) {
		player2.getConnectedClient().getSocket().writeFinalTextFrame(message.messageToString());
	}

	public boolean containsClient(ConnectedClient client) {
		return (player1.getConnectedClient().getPlayerName().equals(client.getPlayerName()) || (player2.getConnectedClient().getPlayerName().equals(client.getPlayerName())));
	}

	public ConnectedClient getOpponent(ConnectedClient client) {
		if (player1.getConnectedClient() == client) {
			return player2.getConnectedClient();
		} else if (player2.getConnectedClient() == client) {
			return player1.getConnectedClient();
		}

		return null;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Game Instance \n ");
		builder.append("_____________ \n ");
		builder.append("gameId : " + gameID + "\n");
		builder.append("player 1 : " + player1.getConnectedClient().getPlayerName() + "state : " + statePlayer1 + "\n");
		builder.append("player 2 : " + player2.getConnectedClient().getPlayerName() + "state : " + statePlayer2 + "\n");
		builder.append("map : " + map.toString() + "\n");

		return  builder.toString();
	}

}