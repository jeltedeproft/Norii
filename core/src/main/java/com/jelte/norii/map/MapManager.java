package com.jelte.norii.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class MapManager {
	private static final String TAG = MapManager.class.getSimpleName();

	private Camera camera;
	private Map currentMap;

	private boolean mapChanged = false;

	public MapManager() {
		// empty constructor
	}

	public void loadMap(MapFactory.MapType mapType) {
		final Map map = MapFactory.getMap(mapType);

		if (map == null) {
			Gdx.app.debug(TAG, "Map does not exist!  ");
			return;
		}

		if (currentMap != null) {
			currentMap.unloadMusic();
		}

		map.loadMusic();
		currentMap = map;
		mapChanged = true;
	}

	public TiledMap getCurrentTiledMap() {
		if (currentMap == null) {
			loadMap(MapFactory.MapType.BATTLE_MAP_THE_DARK_SWAMP);
		}
		return currentMap.getCurrentTiledMap();
	}

	public Map getCurrentMap() {
		if (currentMap == null) {
			loadMap(MapFactory.MapType.BATTLE_MAP_THE_DARK_SWAMP);
		}
		return currentMap;
	}

	public void setCurrentMap(Map currentMap) {
		this.currentMap = currentMap;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Camera getCamera() {
		return camera;
	}

	public boolean hasMapChanged() {
		return mapChanged;
	}

	public void setMapChanged(boolean hasMapChanged) {
		this.mapChanged = hasMapChanged;
	}

	public void disableCurrentmapMusic() {
		currentMap.unloadMusic();
	}

	public void enableCurrentmapMusic() {
		currentMap.loadMusic();
	}

}
