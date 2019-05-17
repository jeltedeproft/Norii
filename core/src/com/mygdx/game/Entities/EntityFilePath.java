package com.mygdx.game.Entities;

public enum EntityFilePath {
    COMMANDER("sprites/characters/Commander.png"),
    ICARUS("sprites/characters/Icarus.png"),
    DEMON("sprites/characters/Demon.png"),
    SHAMAN("sprites/characters/Shaman.png");

    private String _entityFullFilePath;

    EntityFilePath(String entityFullFilePath){
        this._entityFullFilePath = entityFullFilePath;
    }

    public String getValue(){
        return _entityFullFilePath;
    }
}
