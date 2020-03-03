package com.mygdx.game.Screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenManager {	 
    private static ScreenManager instance;
    private Game game;
 
    public enum ScreenParams {
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
 
    public void initialize(Game game) {
        this.game = game;
    }
 

    public void showScreen(ScreenEnum screenEnum, Object... params) {
        Screen currentScreen = game.getScreen();
 
        Screen newScreen = screenEnum.getScreen(params);
        game.setScreen(newScreen);
 
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }
}
