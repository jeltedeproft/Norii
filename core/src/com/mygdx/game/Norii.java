package com.mygdx.game;

import com.badlogic.gdx.Game;

public class Norii extends Game {
	public static final String GAME_IDENTIFIER = "com.mygdx.game";
	public Player player;
	
	@Override
	public void create () {	
		//initialize player
        Player.getInstance().initialize(this); 
		
		//initialize battle screen
        ScreenManager.getInstance().initialize(this);
        ScreenManager.getInstance().showScreen( ScreenEnum.BATTLE); 
	}

	@Override
	public void render () {
		//call every render method(from screen as well)
    	super.render();
	}
	
	@Override
	public void dispose () {
		
	}
}
