package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Game;

public class Norii extends Game {
	public static final String GAME_IDENTIFIER = "com.mygdx.game";
	public Player player;
	private ArrayList<Owner> fighters;
	private ArrayList<Entity> monsters;
	
	@Override
	public void create () {	
		//initialize player
        Player.getInstance().initialize(this);
        
        //this will go somewhere else, the fighters participating in a battle
        fighters = new ArrayList<Owner>();
        monsters = new ArrayList<Entity>();
        monsters.add(new Entity());
        Player.getInstance().setTeam(monsters);
        fighters.add(Player.getInstance());
		
		//initialize battle screen
        ScreenManager.getInstance().initialize(this);
        ScreenManager.getInstance().showScreen( ScreenEnum.BATTLE,fighters); 
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
