package com.mygdx.game.Map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.Battle.BattleManager;

public class TiledMapActor extends Actor {
	private static final String TAG = TiledMapActor.class.getSimpleName();

    private Map tiledMap;

    private TiledMapTileLayer tiledLayer;
    
    private Boolean isFreeSpawn;
    
    private BattleManager battlemanager;

    public Boolean getIsFreeSpawn() {
		return isFreeSpawn;
	}

	public void setIsFreeSpawn(Boolean isFreeSpawn) {
		this.isFreeSpawn = isFreeSpawn;
	}

	TiledMapTileLayer.Cell cell;

    public TiledMapActor(Map tiledMap, TiledMapTileLayer tiledLayer, TiledMapTileLayer.Cell cell, BattleManager battlemanager) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
        this.cell = cell;
        this.isFreeSpawn = false;
        this.battlemanager = battlemanager;
    }

	public BattleManager getBattlemanager() {
		return battlemanager;
	}

	public void setBattlemanager(BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	public Map getTiledMap() {
		return tiledMap;
	}
}