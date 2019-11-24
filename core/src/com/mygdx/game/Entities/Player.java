package com.mygdx.game.Entities;

import com.badlogic.gdx.Game;

public class Player extends Owner{
	//SINGLETON
    private static Player instance;
    private Game game;
 

    private Player() {
        super();
    }
 
    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
    }
 
    public void initialize(Game game) {
        this.game = game;
    }
}
