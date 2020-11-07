package com.mygdx.game.map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.map.TiledMapObserver.TilemapCommand;

import utility.TiledMapPosition;

public class TiledMapActor extends Actor implements TiledMapSubject {
	private final Map tiledMap;
	private final TiledMapTileLayer tiledLayer;
	private Boolean isFreeSpawn;
	private Boolean isAISpawn;
	private Boolean isHovered;
	private TiledMapPosition actorPos = new TiledMapPosition();
	private final Array<TiledMapObserver> observers;

	public Boolean getIsFreeSpawn() {
		return isFreeSpawn;
	}

	public void setIsFreeSpawn(final Boolean isFreeSpawn) {
		this.isFreeSpawn = isFreeSpawn;
	}

	public Boolean getIsAISpawn() {
		return isAISpawn;
	}

	public void setIsAISpawn(final Boolean isAISpawn) {
		this.isAISpawn = isAISpawn;
	}

	public Boolean getIsHovered() {
		return isHovered;
	}

	public void setIsHovered(final Boolean isHovered) {
		this.isHovered = isHovered;
		notifyTilemapObserver(TilemapCommand.HOVER_CHANGED);
	}

	TiledMapTileLayer.Cell cell;

	public TiledMapActor(final Map tiledMap, final TiledMapTileLayer tiledLayer, final TiledMapTileLayer.Cell cell) {
		this.tiledMap = tiledMap;
		this.tiledLayer = tiledLayer;
		this.cell = cell;
		isFreeSpawn = false;
		isHovered = false;
		observers = new Array<TiledMapObserver>();
	}

	public Map getTiledMap() {
		return tiledMap;
	}

	public void setActorPos(final TiledMapPosition pos) {
		actorPos = pos;
	}

	public TiledMapPosition getActorPos() {
		return actorPos;
	}

	public TiledMapTileLayer getTiledLayer() {
		return tiledLayer;
	}

	@Override
	public void addTilemapObserver(final TiledMapObserver tileMapObserver) {
		observers.add(tileMapObserver);
	}

	@Override
	public void removeObserver(final TiledMapObserver tileMapObserver) {
		observers.removeValue(tileMapObserver, true);
	}

	@Override
	public void removeAllObservers() {
		observers.removeAll(observers, true);
	}

	@Override
	public void notifyTilemapObserver(final TilemapCommand command) {
		for (final TiledMapObserver observer : observers) {
			observer.onTiledMapNotify(command, actorPos);
		}
	}

	@Override
	public String toString() {
		return "tiledMapActor : (" + actorPos.getTileX() + "," + actorPos.getTileY() + ")";
	}
}