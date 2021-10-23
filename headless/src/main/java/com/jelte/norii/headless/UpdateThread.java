package com.jelte.norii.headless;

import static java.lang.Thread.sleep;

import java.util.ArrayList;

public class UpdateThread implements Runnable {
	public GameServer gameServer;

	@Override
	public void run() {
		while (true) {
			try {
				gameServer.matchPlayers();
				sleep(3000);
			} catch (final Exception e) {
				System.out.println(e.getStackTrace());
			}
		}
	}

	/**
	 * Called every few seconds to check if any matches can be created with the queued players
	 */
	public void attemptMatchmake() {
		// Return if there aren't enough players on the server
		if (remoteClients.size() < 2)
			return;

		ArrayList<RemoteClient> players = new ArrayList<>();

		// Try and pair queued players with eachother
		for (final RemoteClient client : remoteClients.values()) {
			if (client.getClientState() == ClientState.QUEUED) {
				players.add(client);
				if (players.size() == 2) {
					// Create a GameSetup packet to send to each client
					final Packets.GameSetup gameSetup = new Packets.GameSetup();
					gameSetup.gameID = gamesCreated;
					gameSetup.scoreLimit = GameInstance.SCORE_LIMIT;

					// Send the packet to the first player
					gameSetup.playerName = players.get(0).getPlayerName();
					gameSetup.opponentName = players.get(1).getPlayerName();
					players.get(0).getConnection().sendTCP(gameSetup);

					// Send the packet to the second player
					gameSetup.playerName = players.get(1).getPlayerName();
					gameSetup.opponentName = players.get(0).getPlayerName();
					players.get(1).getConnection().sendTCP(gameSetup);

					// Change the state for each player to be ingame
					players.get(0).setClientState(ClientState.INGAME);
					players.get(1).setClientState(ClientState.INGAME);

					// Change the gameID for each player to the ID of the new game
					players.get(0).setGameID(gamesCreated);
					players.get(1).setGameID(gamesCreated);

					// Create a game instance and add it to the list of all active game instances
					final GameInstance newGame = new GameInstance(this, gamesCreated, players.get(0), players.get(1));
					activeGames.put(gamesCreated, newGame);

					// Output matchup to log
					Log.info("Game " + gamesCreated + ": " + players.get(0).getPlayerName() + " vs " + players.get(1).getPlayerName());
					gamesCreated++;

					// Clear the players list and carry on running, so multiple games can be created each function call
					players = new ArrayList<>();
				}
			}
		}

	}

}
