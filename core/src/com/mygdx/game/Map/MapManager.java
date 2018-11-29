package com.mygdx.game.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class MapManager {
    private static final String TAG = MapManager.class.getSimpleName();

    private Camera _camera;
    private boolean _mapChanged = false;
    private Map _currentMap;

    public MapManager(){

    }

    public void loadMap(MapFactory.MapType mapType){
        Map map = MapFactory.getMap(mapType);

        if( map == null ){
            Gdx.app.debug(TAG, "Map does not exist!  ");
            return;
        }
        
        if( _currentMap != null ){
            _currentMap.unloadMusic();
        }

        map.loadMusic();


        _currentMap = map;
        _mapChanged = true;
    }

    public MapLayer getCollisionLayer(){
        return _currentMap.getCollisionLayer();
    }

    public TiledMap getCurrentTiledMap(){
        if( _currentMap == null ) {
            loadMap(MapFactory.MapType.BATTLE_MAP);
        }
        return _currentMap.getCurrentTiledMap();
    }

    public Map get_currentMap() {
        if( _currentMap == null ) {
            loadMap(MapFactory.MapType.BATTLE_MAP);
        }
		return _currentMap;
	}

	public void set_currentMap(Map _currentMap) {
		this._currentMap = _currentMap;
	}

	public void setCamera(Camera camera){
        this._camera = camera;
    }

    public Camera getCamera(){
        return _camera;
    }

    public boolean hasMapChanged(){
        return _mapChanged;
    }

    public void setMapChanged(boolean hasMapChanged){
        this._mapChanged = hasMapChanged;
    }
    
    public void disableCurrentmapMusic(){
        _currentMap.unloadMusic();
    }

    public void enableCurrentmapMusic(){
        _currentMap.loadMusic();
    }

}
