package com.mygdx.game.Screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenManager {	 
    // Singleton: unique instance
    private static ScreenManager instance;
    private Game game;
 
    public static enum ScreenParams {
    	ARRAYLIST_OF_OWNERS
    }
    private ScreenManager() {
        super();
    }
 
    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }
 
    // Initialization with the game class
    public void initialize(Game game) {
        this.game = game;
    }
 

    public void showScreen(ScreenEnum screenEnum, Object... params) {
    	//current
        Screen currentScreen = game.getScreen();
 
        //new
        Screen newScreen = screenEnum.getScreen(params);
        game.setScreen(newScreen);
 
        // Dispose old
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }
}
