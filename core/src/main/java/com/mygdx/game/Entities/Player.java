package com.mygdx.game.Entities;

public class Player extends TeamLeader{
    private static Player instance;
 

    private Player() {
        super();
    }
 
    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
    }
}
