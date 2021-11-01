package com.jelte.norii.headless;

/**
 * An instance of a norii battle Used for the server to keep track of individual
 * games
 */
public class GameInstance {
	private GameServer gameServer;
	private int gameID;
	private Player player1;
	private Player player2;

	public static final int SCORE_LIMIT = 3;

	public GameInstance(GameServer server, int id, ConnectedClient client1, ConnectedClient client2) {
		gameServer = server;
		gameID = id;
		player1 = new Player(client1);
		player2 = new Player(client2);
	}

	/**
	 * Determines the winner of the round when both players have made their turn
	 *
	 * @return The player instance who won, null if there was a draw
	 */
	private Player determineWinner() {
		return null;
	}

}