package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.Screen.ScreenEnum;
import com.mygdx.game.Screen.ScreenManager;

public class Norii extends Game {
	public static final String GAME_IDENTIFIER = "com.mygdx.game";
	
	@Override
	public void create () {	
        ScreenManager.getInstance().initialize(this);
        ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU); 
	}
	
	@Override
	public void dispose () {
		this.getScreen().dispose();
	}
}
