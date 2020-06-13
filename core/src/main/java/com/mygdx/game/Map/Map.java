package com.mygdx.game.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Audio.AudioManager;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Audio.AudioSubject;
import com.mygdx.game.Battle.BattleManager;

import Utility.Utility;

public abstract class Map implements AudioSubject {
	private static final String TAG = Map.class.getSimpleName();

	public static final float UNIT_SCALE = 1 / 32f;
	public static final int TILE_HEIGHT = 32;
	public static final int TILE_WIDTH = 32;
	private int tilemapWidthInTiles = 32;
	private int tilemapHeightInTiles = 32;

	private Array<AudioObserver> observers;

	protected static final String MAP_COLLISION_LAYER = "items";
	protected static final String MAP_SPAWNS_LAYER = "Spawn points";
	protected static final String BACKGROUND_LAYER = "background";
	protected static final String NAVIGATION_LAYER = "navigation";

	protected static final String PLAYER_START = "PLAYER_START";
	protected static final String ENEMY_START = "ENEMY_START";

	protected Json json;
	protected TiledMap currentMap = null;
	protected TiledMapStage tiledmapstage;
	protected BattleManager battlemanager;
	protected MapLayer collisionLayer = null;
	protected MapLayer spawnsLayer = null;
	protected MyNavigationTiledMapLayer navLayer = null;
	protected MyPathFinder pathfinder;

	protected MapFactory.MapType currentMapType;
	protected MapProperties prop;

	protected int mapWidth;
	protected int mapHeight;
	protected int tilePixelWidth;
	protected int tilePixelHeight;

	Map(final MapFactory.MapType mapType, final String fullMapPath) {
		if (fullMapPath == null || fullMapPath.isEmpty()) {
			Gdx.app.debug(TAG, "Map is invalid");
			return;
		}

		disposeMapAndStage();

		Utility.loadMapAsset(fullMapPath);
		if (Utility.isAssetLoaded(fullMapPath)) {
			currentMap = Utility.getMapAsset(fullMapPath);
		} else {
			Gdx.app.debug(TAG, "Map not loaded");
			return;
		}

		setupMapProperties();
		initializeClassVariables(mapType);

		addObserver(AudioManager.getInstance());
	}

	private void setupMapProperties() {
		prop = currentMap.getProperties();

		mapWidth = prop.get("width", Integer.class);
		mapHeight = prop.get("height", Integer.class);
		tilePixelWidth = prop.get("tilewidth", Integer.class);
		tilePixelHeight = prop.get("tileheight", Integer.class);

		tilemapWidthInTiles = mapWidth;
		tilemapHeightInTiles = mapHeight;
	}

	private void disposeMapAndStage() {
		if (currentMap != null) {
			currentMap.dispose();
			if (tiledmapstage != null) {
				tiledmapstage.dispose();
			}
		}
		if (pathfinder != null) {
			pathfinder.dispose();
		}
	}

	private void initializeClassVariables(final MapFactory.MapType mapType) {
		json = new Json();
		currentMapType = mapType;
		observers = new Array<AudioObserver>();
	}

	public MyNavigationTiledMapLayer getNavLayer() {
		return navLayer;
	}

	public void setNavLayer(final MyNavigationTiledMapLayer navLayer) {
		this.navLayer = navLayer;
	}

	public MapLayer getCollisionLayer() {
		return collisionLayer;
	}

	public TiledMap getCurrentTiledMap() {
		return currentMap;
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public void setMapWidth(final int mapWidth) {
		this.mapWidth = mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public void setMapHeight(final int mapHeight) {
		this.mapHeight = mapHeight;
	}

	public MyPathFinder getPathfinder() {
		return pathfinder;
	}

	public int getTilemapWidthInTiles() {
		return tilemapWidthInTiles;
	}

	public int getTilemapHeightInTiles() {
		return tilemapHeightInTiles;
	}

	public abstract void unloadMusic();

	public abstract void loadMusic();

	@Override
	public void addObserver(final AudioObserver audioObserver) {
		observers.add(audioObserver);
	}

	@Override
	public void removeObserver(final AudioObserver audioObserver) {
		observers.removeValue(audioObserver, true);
	}

	@Override
	public void removeAllObservers() {
		observers.removeAll(observers, true);
	}

	@Override
	public void notify(final AudioObserver.AudioCommand command, final AudioObserver.AudioTypeEvent event) {
		for (final AudioObserver observer : observers) {
			observer.onNotify(command, event);
		}
	}
}
