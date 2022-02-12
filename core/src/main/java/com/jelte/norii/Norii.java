package com.jelte.norii;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.jelte.norii.screen.ScreenEnum;
import com.jelte.norii.screen.ScreenManager;

public class Norii extends Game {
	public static final String GAME_IDENTIFIER = "com.jelte.norii";

	@Override
	public void create() {
		//Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		Gdx.app.setLogLevel(3);// debug
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN);
	}

	@Override
	public void dispose() {
		this.getScreen().dispose();
	}
}