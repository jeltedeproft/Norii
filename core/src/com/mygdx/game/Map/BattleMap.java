package com.mygdx.game.Map;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Battle.BattleManager;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;
import Utility.Utility;

public class BattleMap extends Map{
    private static final String TAG = BattleMap.class.getSimpleName();

    private static String _mapPath;
    private ArrayList<TiledMapPosition> _unitSpawnPositions;
    
    protected Vector2 _playerStartPositionRect;
    protected TiledMapPosition _convertedUnits;


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
    	_unitSpawnPositions = new ArrayList<TiledMapPosition>();
    	_playerStartPositionRect = new Vector2(0,0);
    	_convertedUnits = new TiledMapPosition(0,0);
    	
        _spawnsLayer = _currentMap.getLayers().get(MAP_SPAWNS_LAYER);
        _navLayer = (MyNavigationTiledMapLayer) _currentMap.getLayers().get(NAVIGATION_LAYER);
        pathfinder = new MyPathFinder(this);
    }
    
    public void setStage(BattleManager battlemanager) {
    	tiledmapstage = new TiledMapStage(this,BACKGROUND_LAYER,battlemanager);
    	
        if( _spawnsLayer == null ){
            Gdx.app.debug(TAG, "No spawn layer!");
        }else{
            if( _unitSpawnPositions.isEmpty() ){
                fillSpawnPositions(_unitSpawnPositions);
            }
        }
    }

    private void fillSpawnPositions(final ArrayList<TiledMapPosition> startPositions){
        for( MapObject object: _spawnsLayer.getObjects()){
            if( object.getName().equalsIgnoreCase(PLAYER_START) ){
            	
                ((RectangleMapObject)object).getRectangle().getPosition(_playerStartPositionRect);
                TiledMapPosition newPos = new TiledMapPosition(_playerStartPositionRect.x,_playerStartPositionRect.y);
                startPositions.add(newPos);
                
                //tag tiles that can be used as spawns
                TiledMapActor tiledactor = (TiledMapActor) tiledmapstage.hit(TiledMapPosition.getUpScaledX(_playerStartPositionRect.x), TiledMapPosition.getUpScaledY(_playerStartPositionRect.y), false);
                tiledactor.setIsFreeSpawn(true);
            }
        }        
    }
    
    public void makeSpawnParticles() {
    	for(TiledMapPosition pos : _unitSpawnPositions) {
            ParticleMaker.addParticle(ParticleType.SPAWN, pos);
    	}
    }
    
	public ArrayList<TiledMapPosition> getSpawnPositions(){
    	@SuppressWarnings("unchecked")
		ArrayList<TiledMapPosition> UnitSpawns = (ArrayList<TiledMapPosition>) _unitSpawnPositions.clone();
        return UnitSpawns;
    }
    
    public TiledMapStage getTiledMapStage() {
    	return tiledmapstage;
    }
    
    public MyNavigationTiledMapLayer  get_navLayer() {
		return _navLayer;
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

