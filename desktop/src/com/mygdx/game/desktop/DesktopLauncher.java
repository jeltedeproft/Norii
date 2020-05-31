package com.mygdx.game.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.Norii;

public class DesktopLauncher {
	public static void main(final String[] arg) {
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Norii a turn based strategy game by Jelte";
		config.width = 580;
		config.height = 580;
		config.fullscreen = false;
		config.initialBackgroundColor = Color.BLACK;

		final Application app = new LwjglApplication(new Norii(), config);
		Gdx.app = app;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
}
