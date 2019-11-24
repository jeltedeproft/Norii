package com.mygdx.game.Entities;

public enum EntityFilePath {
    COMMANDER("sprites/characters/Commander.png"),
    ICARUS("sprites/characters/Icarus.png"),
    DEMON("sprites/characters/Demon.png"),
    SHAMAN("sprites/characters/Shaman.png");

    private String entityFullFilePath;

    EntityFilePath(String entityFullFilePath){
        this.entityFullFilePath = entityFullFilePath;
    }

    public String getValue(){
        return entityFullFilePath;
    }
}
