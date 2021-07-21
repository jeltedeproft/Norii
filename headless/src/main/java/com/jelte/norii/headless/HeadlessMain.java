package com.jelte.norii.headless;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;

public class HeadlessMain extends Game {
	private final ConcurrentLinkedQueue<ServerWebSocket> clients = new ConcurrentLinkedQueue<>();

	@Override
	public void create() {
		Vertx vertx = Vertx.vertx();

		HttpServerOptions options = new HttpServerOptions();
		HttpServer server = vertx.createHttpServer(options);

		server.webSocketHandler(client -> {
			Gdx.app.log("Client", "Connected " + client.textHandlerID());

			clients.add(client);
			clients.forEach(c -> c.writeFinalTextFrame("Client connected: " + client.textHandlerID()));

			client.frameHandler(event -> {
				Gdx.app.log("Client", "Message " + event.textData());
				clients.forEach(c -> c.writeFinalTextFrame(event.textData()));

				System.out.println(event.textData());
			});

			client.closeHandler(event -> {
				Gdx.app.log("Client", "Disconnected " + client.textHandlerID());
				clients.remove(client);

				clients.forEach(c -> c.writeFinalTextFrame("Client disconnected: " + client.textHandlerID()));
			});
		});

		int port = 80;
		Gdx.app.log("Server", "Starting on port " + port);
		server.listen(port);

		Gdx.app.log("Server", "Started");
	}
}
