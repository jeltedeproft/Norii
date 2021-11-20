package com.jelte.norii.map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.jelte.norii.utility.TiledMapPosition;

public class TiledMapActor extends Actor {
	private final Map tiledMap;
	private final TiledMapTileLayer tiledLayer;
	private Boolean isFreeSpawn = false;
	private Boolean isAISpawn = false;
	private Boolean isHovered = false;
	private TiledMapPosition actorPos = new TiledMapPosition();

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
	}

	TiledMapTileLayer.Cell cell;

	public TiledMapActor(final Map tiledMap, final TiledMapTileLayer tiledLayer, final TiledMapTileLayer.Cell cell) {
		this.tiledMap = tiledMap;
		this.tiledLayer = tiledLayer;
		this.cell = cell;
		isFreeSpawn = false;
		isHovered = false;
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
	public String toString() {
		return "tiledMapActor : (" + actorPos.getTileX() + "," + actorPos.getTileY() + ")";
	}
}