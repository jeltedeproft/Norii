package com.mygdx.game.Entities;

public enum EntityPortaitFilePath {
    COMMANDER("sprites/characters/commanderProfileClean.png"),
    ICARUS("sprites/characters/icarusProfileClean.png"),
    DEMON("sprites/characters/demonProfileClean.png"),
    SHAMAN("sprites/characters/shamanProfileClean.png");

    private String entityFullFilePath;

    EntityPortaitFilePath(String entityFullFilePath){
        this.entityFullFilePath = entityFullFilePath;
    }

    public String getValue(){
        return entityFullFilePath;
    }
}
