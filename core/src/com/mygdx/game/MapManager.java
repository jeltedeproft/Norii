package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.*;

import java.util.ArrayList;
import java.util.Hashtable;

public class MapManager {
    private static final String TAG = MapManager.class.getSimpleName();

    //All maps for the game
    private Hashtable<String,String> _mapTable;
    private Hashtable<String, ArrayList<Vector2>> _spawnLocationsTable;

    //maps
    private final static String BATTLE_MAP = "BATTLE_MAP";
    private static final String BATTLE_MAP_SCALED = "BATTLE_MAP_SCALED";


    //Map layers
    private final static String MAP_COLLISION_LAYER = "items";
    private final static String MAP_SPAWNS_LAYER = "Spawn points";
    private final static String BACKGROUND_LAYER = "background";
    
    private final static String PLAYER_START = "PLAYER_START";

    private Vector2 _playerStartPositionRect;
    private Vector2 _convertedUnits;
    private ArrayList<Vector2> _unitSpawnPositions;
    private ArrayList<Vector2> _unitSpawnPositionsScaledUnits;

    private TiledMap _currentMap = null;
    private TiledMapStage tiledmapstage;
    private String _currentMapName;
    private MapLayer _collisionLayer = null;
    private MapLayer _spawnsLayer = null;

    public final static float UNIT_SCALE  = 1/32f;

    public MapManager(){
    	_unitSpawnPositions = new ArrayList<Vector2>();
    	_unitSpawnPositionsScaledUnits = new ArrayList<Vector2>();
    	
        _mapTable = new Hashtable<String, String>();
        _mapTable.put(BATTLE_MAP, "maps/32x32 rpg battlemap.tmx");
        
        _spawnLocationsTable = new Hashtable<String, ArrayList<Vector2>>();
        _spawnLocationsTable.put(BATTLE_MAP, _unitSpawnPositions);
        _spawnLocationsTable.put(BATTLE_MAP_SCALED, _unitSpawnPositionsScaledUnits);
        
        _playerStartPositionRect = new Vector2(0,0);
        _convertedUnits = new Vector2(0,0);
    }

    public void loadMap(String mapName){
        String mapFullPath = _mapTable.get(mapName);

        if( mapFullPath == null || mapFullPath.isEmpty() ) {
            Gdx.app.debug(TAG, "Map is invalid");
            return;
        }

        if( _currentMap != null ){
            _currentMap.dispose();
            tiledmapstage.dispose();
        }

        Utility.loadMapAsset(mapFullPath);
        if( Utility.isAssetLoaded(mapFullPath) ) {
            _currentMap = Utility.getMapAsset(mapFullPath);
            _currentMapName = mapName;
            tiledmapstage = new TiledMapStage(_currentMap,BACKGROUND_LAYER);
        }else{
            Gdx.app.debug(TAG, "Map not loaded");
            return;
        }

        _collisionLayer = _currentMap.getLayers().get(MAP_COLLISION_LAYER);
        if( _collisionLayer == null ){
            Gdx.app.debug(TAG, "No collision layer!");
        }

        _spawnsLayer = _currentMap.getLayers().get(MAP_SPAWNS_LAYER);
        if( _spawnsLayer == null ){
            Gdx.app.debug(TAG, "No spawn layer!");
        }else{
        	ArrayList<Vector2> startPositions = _spawnLocationsTable.get(_currentMapName);
        	ArrayList<Vector2> startPositionsScaled = _spawnLocationsTable.get(_currentMapName + "_SCALED");
            if( startPositions.isEmpty() ){
                fillSpawnPositions(startPositions,startPositionsScaled);
                startPositions = _spawnLocationsTable.get(_currentMapName);
            }
        }  

    }

    public TiledMap getCurrentMap(){
        if( _currentMap == null ) {
            _currentMapName = BATTLE_MAP;
            loadMap(_currentMapName);
        }
        return _currentMap;
    }

    public MapLayer getCollisionLayer(){
        return _collisionLayer;
    }
    
    public TiledMapStage getTiledMapStage(){
    	return tiledmapstage;
    }


    public ArrayList<Vector2> getSpawnPositionsFromScaledUnits(){
    	ArrayList<Vector2> scaledUnitSpawns = (ArrayList<Vector2>) _unitSpawnPositionsScaledUnits.clone();
        return scaledUnitSpawns;
    }

    private void fillSpawnPositions(final ArrayList<Vector2> startPositions,final ArrayList<Vector2> startPositionsScaled){
        Gdx.app.debug(TAG, "fillSpawnPositions INPUT: (" + startPositions.toString() + ") " + _currentMapName);

        //Go through all player start positions and add them to the arraylist
        for( MapObject object: _spawnsLayer.getObjects()){
            if( object.getName().equalsIgnoreCase(PLAYER_START) ){
                ((RectangleMapObject)object).getRectangle().getPosition(_playerStartPositionRect);
                startPositions.add(_playerStartPositionRect.cpy());
                
                //scale and add
                if( UNIT_SCALE > 0 ) {
                	_convertedUnits.set(_playerStartPositionRect.x*UNIT_SCALE, _playerStartPositionRect.y*UNIT_SCALE);
                	startPositionsScaled.add(_convertedUnits.cpy());
                }
            }
        }        
    }
}

