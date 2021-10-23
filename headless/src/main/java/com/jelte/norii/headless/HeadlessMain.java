package com.jelte.norii.headless;

import com.badlogic.gdx.Game;

public class HeadlessMain extends Game {

	private GameServer gameServer;

	@Override
	public void create() {
		gameServer = new GameServer();
	}
}
