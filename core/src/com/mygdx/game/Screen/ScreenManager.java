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
 
    // Show in the game the screen which enum type is received
    public void showScreen(ScreenEnum screenEnum, Object... params) {
 
        // Get current screen to dispose it
        Screen currentScreen = game.getScreen();
 
        // Show new screen
        Screen newScreen = screenEnum.getScreen(params);
        game.setScreen(newScreen);
 
        // Dispose previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }
}
