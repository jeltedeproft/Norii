package com.mygdx.game.Entities;

public enum EntityPortaitFilePath {
    COMMANDER("sprites/characters/commanderProfile.png"),
    ICARUS("sprites/characters/icarusProfile.png"),
    DEMON("sprites/characters/demonProfile.png"),
    SHAMAN("sprites/characters/shamanProfile.png");

    private String entityFullFilePath;

    EntityPortaitFilePath(String entityFullFilePath){
        this.entityFullFilePath = entityFullFilePath;
    }

    public String getValue(){
        return entityFullFilePath;
    }
}
