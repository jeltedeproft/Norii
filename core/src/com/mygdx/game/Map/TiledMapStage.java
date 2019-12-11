package com.mygdx.game.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Battle.BattleManager;
import Utility.TiledMapPosition;

public class TiledMapStage extends Stage {
	private static final String TAG = TiledMapStage.class.getSimpleName();
    private Map tiledMap;
    private BattleManager battlemanager;
    
    public TiledMapStage(Map tiledMap, String layername, BattleManager battlemanager) {
        this.tiledMap = tiledMap;
        this.battlemanager = battlemanager;
        TiledMapTileLayer tiledLayer = (TiledMapTileLayer) tiledMap.getCurrentTiledMap().getLayers().get(layername);
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
                TiledMapActor actor = new TiledMapActor(tiledMap, tiledLayer, cell);
                
                initiateActor(x, y, actor);
                addEventListener(actor);
            }
        }
    }

	private void initiateActor(int x, int y, TiledMapActor actor) {
		actor.setBounds(x,y,1,1);
		actor.setActorPos(new TiledMapPosition().setPositionFromTiles(x, y));
		addActor(actor);
	}

	private void addEventListener(TiledMapActor actor) {
		EventListener eventListener = new TiledMapClickListener(actor);
		actor.addListener(eventListener);
	}
}