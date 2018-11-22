package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class TiledMapStage extends Stage {
	private static final String TAG = TiledMapStage.class.getSimpleName();

    private TiledMap tiledMap;
    private String _layername;
    private BattleManager battlemanager;

    public TiledMapStage(TiledMap tiledMap, String layername, BattleManager battlemanager) {
        this.tiledMap = tiledMap;
        this._layername = layername;
        this.battlemanager = battlemanager;

        TiledMapTileLayer tiledLayer = (TiledMapTileLayer) tiledMap.getLayers().get(layername);
        createActorsForLayer(tiledLayer);
        
    }

    public BattleManager getBattlemanager() {
		return battlemanager;
	}

	public void setBattlemanager(BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	private void createActorsForLayer(TiledMapTileLayer tiledLayer) {
        for (int x = 0; x < tiledLayer.getWidth(); x++) {
            for (int y = 0; y < tiledLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                TiledMapActor actor = new TiledMapActor(tiledMap, tiledLayer, cell, battlemanager);
                actor.setBounds(x * tiledLayer.getTileWidth(), y * tiledLayer.getTileHeight(), tiledLayer.getTileWidth(),tiledLayer.getTileHeight());
                addActor(actor);
                EventListener eventListener = new TiledMapClickListener(actor);
                actor.addListener(eventListener);
            }
        }
    }
}