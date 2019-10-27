package com.mygdx.game.Entities;

public enum EntityPortaitFilePath {
    COMMANDER("sprites/characters/Commander.png"),
    ICARUS("sprites/characters/Icarus.png"),
    DEMON("sprites/characters/Demon.png"),
    SHAMAN("sprites/characters/Shaman.png");

    private String _entityFullFilePath;

    EntityPortaitFilePath(String entityFullFilePath){
        this._entityFullFilePath = entityFullFilePath;
    }

    public String getValue(){
        return _entityFullFilePath;
    }
}
