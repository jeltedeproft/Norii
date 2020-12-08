package com.jelte.norii.map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jelte.norii.screen.BattleScreen;
import com.jelte.norii.utility.TiledMapPosition;

public class TiledMapStage extends Stage {
	private final TiledMapActor[][] actors;
	private final Map tiledMap;
	private final BattleScreen battleScreen;

	public TiledMapStage(Map tiledMap, String layername, BattleScreen battleScreen) {
		this.tiledMap = tiledMap;
		this.battleScreen = battleScreen;
		final TiledMapTileLayer tiledLayer = (TiledMapTileLayer) tiledMap.getCurrentTiledMap().getLayers().get(layername);
		actors = new TiledMapActor[tiledLayer.getWidth()][tiledLayer.getHeight()];
		createActorsForLayer(tiledLayer);
	}

	private void createActorsForLayer(TiledMapTileLayer tiledLayer) {
		for (int x = 0; x < tiledLayer.getWidth(); x++) {
			for (int y = 0; y < tiledLayer.getHeight(); y++) {
				final TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
				final TiledMapActor actor = new TiledMapActor(tiledMap, tiledLayer, cell);
				actors[x][y] = actor;

				initiateActor(x, y, actor);
				addEventListener(actor);
			}
		}
	}

	private void initiateActor(int x, int y, TiledMapActor actor) {
		actor.setBounds(x, y, 1, 1);
		actor.setActorPos(new TiledMapPosition().setPositionFromTiles(x, y));
		addActor(actor);
	}

	private void addEventListener(TiledMapActor actor) {
		final EventListener eventListener = new TiledMapClickListener(actor);
		actor.addListener(eventListener);
	}

	public TiledMapActor[][] getTiledMapActors() {
		return this.actors;
	}

	public BattleScreen getBattleScreen() {
		return battleScreen;
	}
}