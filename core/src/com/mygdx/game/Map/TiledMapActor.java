package com.mygdx.game.Map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import Utility.TiledMapPosition;

public class TiledMapActor extends Actor {
	private static final String TAG = TiledMapActor.class.getSimpleName();
	
    private Map tiledMap;
    private TiledMapTileLayer tiledLayer;   
    private Boolean isFreeSpawn;
    private Boolean isHovered;
	private TiledMapPosition actorPos = new TiledMapPosition();

    public Boolean getIsFreeSpawn() {
		return isFreeSpawn;
	}

	public void setIsFreeSpawn(Boolean isFreeSpawn) {
		this.isFreeSpawn = isFreeSpawn;
	}
	
    public Boolean getIsHovered() {
		return isHovered;
	}

	public void setIsHovered(Boolean isHovered) {
		this.isHovered = isHovered;
	}

	TiledMapTileLayer.Cell cell;

    public TiledMapActor(Map tiledMap, TiledMapTileLayer tiledLayer, TiledMapTileLayer.Cell cell) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
        this.cell = cell;
        this.isFreeSpawn = false;
        this.isHovered = false;
    }

	public Map getTiledMap() {
		return tiledMap;
	}
	
	public void setActorPos(TiledMapPosition pos) {
		this.actorPos = pos;
	}
	
	public TiledMapPosition getActorPos() {
		return actorPos;
	}
	
	public TiledMapTileLayer getTiledLayer() {
		return tiledLayer;
	}
}