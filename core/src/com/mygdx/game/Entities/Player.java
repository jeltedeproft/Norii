package com.mygdx.game.Entities;

import com.badlogic.gdx.Game;

public class Player extends Owner{
    // Singleton: unique instance
    private static Player instance;
 
    // Reference to game
    private Game game;
 
    // Singleton: private constructor
    private Player() {
        super();
    }
 
    // Singleton: retrieve instance
    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
    }
 
    // Initialization with the game class
    public void initialize(Game game) {
        this.game = game;
    }
}
