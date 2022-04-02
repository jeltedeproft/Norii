package com.jelte.norii.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Json;
import com.jelte.norii.utility.AssetManagerUtility;

public abstract class Map {
	private static final String TAG = Map.class.getSimpleName();

	public static final float UNIT_SCALE = 1 / 32f;
	public static final int TILE_HEIGHT = 32;
	public static final int TILE_WIDTH = 32;
	private int tilemapWidthInTiles = 32;
	private int tilemapHeightInTiles = 32;

	protected static final String MAP_COLLISION_LAYER = "items";
	protected static final String MAP_SPAWNS_LAYER = "Spawn points";
	protected static final String BACKGROUND_LAYER = "background";
	protected static final String NAVIGATION_LAYER = "navigation";

	protected static final String PLAYER_START = "PLAYER_START";
	protected static final String ENEMY_START = "ENEMY_START";

	protected Json json;
	protected TiledMap currentMap = null;
	protected TiledMapStage tiledmapstage;
	protected MapLayer spawnsLayer = null;
	protected MyNavigationTiledMapLayer navLayer = null;

	protected MapFactory.MapType mapType;
	protected MapProperties prop;

	protected int mapWidth;
	protected int mapHeight;
	protected int tilePixelWidth;
	protected int tilePixelHeight;

	Map(final MapFactory.MapType mapType, final String fullMapPath) {
		if ((fullMapPath == null) || fullMapPath.isEmpty()) {
			Gdx.app.debug(TAG, "Map is invalid");
			return;
		}

		disposeMapAndStage();

		AssetManagerUtility.loadMapAsset(fullMapPath);
		if (!AssetManagerUtility.isAssetLoaded(fullMapPath)) {
			Gdx.app.debug(TAG, "Map not loaded");
			return;
		}
		currentMap = AssetManagerUtility.getMapAsset(fullMapPath);

		setupMapProperties();
		initializeClassVariables(mapType);
	}

	private void disposeMapAndStage() {
		if (currentMap != null) {
			currentMap.dispose();
			if (tiledmapstage != null) {
				tiledmapstage.dispose();
			}
		}
		if (MyPathFinder.getInstance() != null) {
			MyPathFinder.getInstance().dispose();
		}
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

	private void initializeClassVariables(final MapFactory.MapType mapType) {
		json = new Json();
		this.mapType = mapType;
	}

	public MyNavigationTiledMapLayer getNavLayer() {
		return navLayer;
	}

	public void setNavLayer(final MyNavigationTiledMapLayer navLayer) {
		this.navLayer = navLayer;
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

	public int getTilemapWidthInTiles() {
		return tilemapWidthInTiles;
	}

	public int getTilemapHeightInTiles() {
		return tilemapHeightInTiles;
	}

	public MapFactory.MapType getMapType() {
		return mapType;
	}

	public abstract void unloadMusic();

	public abstract void loadMusic();
}
