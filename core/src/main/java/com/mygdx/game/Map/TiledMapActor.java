package com.mygdx.game.Map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Map.TiledMapObserver.TilemapCommand;

import Utility.TiledMapPosition;

public class TiledMapActor extends Actor implements TiledMapSubject{
    private Map tiledMap;
    private TiledMapTileLayer tiledLayer;   
    private Boolean isFreeSpawn;
    private Boolean isHovered;
	private TiledMapPosition actorPos = new TiledMapPosition();
	private Array<TiledMapObserver> observers;

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
		notifyTilemapObserver(TilemapCommand.HOVER_CHANGED);
	}

	TiledMapTileLayer.Cell cell;

    public TiledMapActor(Map tiledMap, TiledMapTileLayer tiledLayer, TiledMapTileLayer.Cell cell) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
        this.cell = cell;
        this.isFreeSpawn = false;
        this.isHovered = false;
        this.observers = new Array<TiledMapObserver>();
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

	@Override
	public void addTilemapObserver(TiledMapObserver tileMapObserver) {
		observers.add(tileMapObserver);
	}

	@Override
	public void removeObserver(TiledMapObserver tileMapObserver) {
		observers.removeValue(tileMapObserver, true);
	}

	@Override
	public void removeAllObservers() {
		observers.removeAll(observers, true);
	}

	@Override
	public void notifyTilemapObserver(TilemapCommand command) {
        for(TiledMapObserver observer: observers){
            observer.onTiledMapNotify(command,actorPos);
        }
	}
}