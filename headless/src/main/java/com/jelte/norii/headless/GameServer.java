package com.jelte.norii.headless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

public class GameServer {
	private final ConcurrentLinkedQueue<ServerWebSocket> clients = new ConcurrentLinkedQueue<>();
	private final Map<ServerWebSocket, ConnectedClient> socketsWithClients = new HashMap<>();
	private HttpServer server;
	private static final int PORT = 80;
	private static final String CLIENT_TAG = "Client";

	public GameServer() {
		initServer();

		Gdx.app.log("Server", "Starting on port " + PORT);
		server.listen(PORT);
		Gdx.app.log("Server", "Started");
	}

	private void initServer() {
		final Vertx vertx = Vertx.vertx();
		final HttpServerOptions options = new HttpServerOptions();
		server = vertx.createHttpServer(options);

		server.webSocketHandler(initConnectedClient());
	}

	private Handler<ServerWebSocket> initConnectedClient() {
		return client -> {
			handleConnectedClient(client);

			client.frameHandler(this::handleFrameClient);

			client.closeHandler(event -> {
				handleCloseClient(client);
			});
		};
	}

	private void handleConnectedClient(ServerWebSocket client) {
		Gdx.app.log(CLIENT_TAG, "Connected " + client.textHandlerID());

		clients.add(client);
		clients.forEach(c -> c.writeFinalTextFrame("Client connected: " + client.textHandlerID()));
	}

	private void handleFrameClient(WebSocketFrame event) {
		Gdx.app.log(CLIENT_TAG, "Message " + event.textData());

		clients.forEach(c -> c.writeFinalTextFrame(event.textData()));
	}

	private void handleCloseClient(ServerWebSocket client) {
		Gdx.app.log(CLIENT_TAG, "Disconnected " + client.textHandlerID());
		clients.remove(client);

		clients.forEach(c -> c.writeFinalTextFrame("Client disconnected: " + client.textHandlerID()));
	}

	public void matchPlayers() {
		if (socketsWithClients.size() < 2)
			return;

		ArrayList<ConnectedClient> players = new ArrayList<>();

		for (final ConnectedClient client : socketsWithClients.values()) {
			if (client.getClientState() == ClientState.QUEUED) {
				players.add(client);
				if (players.size() == 2) {
					// Create a GameSetup packet to send to each client
					String battleMessage = NetworkMessage.createBattleMessageForSending(CLIENT_TAG, CLIENT_TAG, CLIENT_TAG, CLIENT_TAG)

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
