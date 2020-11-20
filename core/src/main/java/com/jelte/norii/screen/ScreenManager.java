package com.jelte.norii.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenManager {
	private static ScreenManager instance;
	private static Screen mainMenu;
	private Game game;

	private ScreenManager() {
		super();
	}

	public static ScreenManager getInstance() {
		if (instance == null) {
			instance = new ScreenManager();
		}
		return instance;
	}

	public void initialize(Game game) {
		this.game = game;
	}

	public void showScreen(ScreenEnum screenEnum, Object... params) {
		final Screen currentScreen = game.getScreen();

		final Screen newScreen = screenEnum.getScreen(params);
		game.setScreen(newScreen);

		if (currentScreen != null) {
			currentScreen.dispose();
		}
	}

	public static Screen getMainMenu() {
		return mainMenu;
	}

	public static void setMainMenu(Screen mainMenu) {
		ScreenManager.mainMenu = mainMenu;
	}

}
