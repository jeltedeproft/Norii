package com.mygdx.game.Map;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utility;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Battle.BattleManager;

public class BattleMap extends Map{
    private static final String TAG = BattleMap.class.getSimpleName();

    private static String _mapPath;
    private ArrayList<Vector2> _unitSpawnPositions;
    private ArrayList<Vector2> _unitSpawnPositionsScaledUnits;
    
    protected Vector2 _playerStartPositionRect;
    protected Vector2 _convertedUnits;


    BattleMap(String mapPath){
        super(MapFactory.MapType.BATTLE_MAP, mapPath);
        _mapPath = mapPath;
        
        if( Utility.isAssetLoaded(_mapPath) ) {
        	//do smthg?
        }else{
            Gdx.app.debug(TAG, "Map not loaded");
            return;
        }  
        
        _collisionLayer = _currentMap.getLayers().get(MAP_COLLISION_LAYER);
        if( _collisionLayer == null ){
            Gdx.app.debug(TAG, "No collision layer!");
        }
        
        initializeClassVariables();
    }
    
    private void initializeClassVariables() {
    	_unitSpawnPositions = new ArrayList<Vector2>();
    	_unitSpawnPositionsScaledUnits = new ArrayList<Vector2>();
    	_playerStartPositionRect = new Vector2(0,0);
    	_convertedUnits = new Vector2(0,0);
    	
        _spawnsLayer = _currentMap.getLayers().get(MAP_SPAWNS_LAYER);
        _navLayer = (MyNavigationTiledMapLayer) _currentMap.getLayers().get(NAVIGATION_LAYER);
    }
    
    public void setStage(BattleManager battlemanager) {
    	tiledmapstage = new TiledMapStage(_currentMap,BACKGROUND_LAYER,battlemanager);
    	
        if( _spawnsLayer == null ){
            Gdx.app.debug(TAG, "No spawn layer!");
        }else{
            if( _unitSpawnPositions.isEmpty() ){
                fillSpawnPositions(_unitSpawnPositions,_unitSpawnPositionsScaledUnits);
            }
        }
    }

	public ArrayList<Vector2> getSpawnPositionsFromScaledUnits(){
    	@SuppressWarnings("unchecked")
		ArrayList<Vector2> scaledUnitSpawns = (ArrayList<Vector2>) _unitSpawnPositionsScaledUnits.clone();
        return scaledUnitSpawns;
    }

    private void fillSpawnPositions(final ArrayList<Vector2> startPositions,final ArrayList<Vector2> startPositionsScaled){
        Gdx.app.debug(TAG, "fillSpawnPositions INPUT: (" + startPositions.toString() + ") ");

        //Get player start positions
        for( MapObject object: _spawnsLayer.getObjects()){
            if( object.getName().equalsIgnoreCase(PLAYER_START) ){
                ((RectangleMapObject)object).getRectangle().getPosition(_playerStartPositionRect);
                startPositions.add(_playerStartPositionRect.cpy());

                //tag tiles that can be used as spawns
                TiledMapActor tiledactor = (TiledMapActor) tiledmapstage.hit(_playerStartPositionRect.x, _playerStartPositionRect.y, false);
                tiledactor.setIsFreeSpawn(true);
                
                //scaled version of start positions
                if( UNIT_SCALE > 0 ) {
                	startPositionsScaled.add(new Vector2(_playerStartPositionRect.x*UNIT_SCALE, _playerStartPositionRect.y*UNIT_SCALE));
                }
            }
        }        
    }
    
    public TiledMapStage getTiledMapStage() {
    	return tiledmapstage;
    }


    @Override
    public void unloadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
    }

    @Override
    public void loadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
    }
}

