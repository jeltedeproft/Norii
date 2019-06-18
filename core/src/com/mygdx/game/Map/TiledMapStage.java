package com.mygdx.game.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Battle.BattleManager;

public class TiledMapStage extends Stage {
	private static final String TAG = TiledMapStage.class.getSimpleName();

    private Map tiledMap;
    private String _layername;
    private BattleManager battlemanager;
    
    public TiledMapStage(Map tiledMap, String layername, BattleManager battlemanager) {
        this.tiledMap = tiledMap;
        this._layername = layername;
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
                
                float tilewidth = Gdx.graphics.getWidth() / tiledLayer.getWidth();
                float tileheight = Gdx.graphics.getHeight() / tiledLayer.getHeight();
                Gdx.app.debug(TAG, "tilewidth = " + tilewidth + " , and tileheight = " + tileheight);
                actor.setBounds(x * tilewidth, y * tileheight, tilewidth,tileheight);
                //actor.setBounds(actor.getX(), actor.getY(), tilewidth,tileheight);
                addActor(actor);

                EventListener eventListener = new TiledMapClickListener(actor);
                actor.addListener(eventListener);
            }
        }
    }
	
	public void drawActorsDebug() {
		Array<Actor> actors = this.getActors();
		for(Actor actor : actors) {
	        ShapeRenderer debugRenderer = new ShapeRenderer();
	        debugRenderer.setProjectionMatrix(this.getCamera().combined);
	        debugRenderer.setColor(Color.RED);
	        debugRenderer.begin(ShapeType.Line);
	        actor.debug();
	        actor.drawDebug(debugRenderer);
	        debugRenderer.end();
		}
	}
}