
package com.mygdx.game.Map;

import java.util.ArrayList;
import java.util.List;

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

    private ArrayList<TiledMapPosition> unitSpawnPositions;
    protected Vector2 playerStartPositionRect;
    protected TiledMapPosition convertedUnits;

    BattleMap(String mapPath){
        super(MapFactory.MapType.BATTLE_MAP, mapPath);
        
        if( !Utility.isAssetLoaded(mapPath) ) {
            Gdx.app.debug(TAG, "Map not loaded");
            return;
        } 
        
        collisionLayer = currentMap.getLayers().get(MAP_COLLISION_LAYER);
        if( collisionLayer == null ){
            Gdx.app.debug(TAG, "No collision layer!");
        }
        
        initializeClassVariables();
    }
    
    private void initializeClassVariables() {
    	unitSpawnPositions = new ArrayList<TiledMapPosition>();
    	playerStartPositionRect = new Vector2(0,0);
    	convertedUnits = new TiledMapPosition();
    	  	
        spawnsLayer = currentMap.getLayers().get(MAP_SPAWNS_LAYER);
        navLayer = (MyNavigationTiledMapLayer) currentMap.getLayers().get(NAVIGATION_LAYER);
        pathfinder = new MyPathFinder(this);
    }
    
    public void setStage(BattleManager battlemanager) {
    	tiledmapstage = new TiledMapStage(this,BACKGROUND_LAYER,battlemanager);
    	
        if(spawnsLayer == null){
            Gdx.app.debug(TAG, "No spawn layer!");
        }else{
            if(unitSpawnPositions.isEmpty()){
                fillSpawnPositions(unitSpawnPositions);
            }
        }
    }

    private void fillSpawnPositions(final ArrayList<TiledMapPosition> startPositions){
        for( MapObject object: spawnsLayer.getObjects()){
            if( object.getName().equalsIgnoreCase(PLAYER_START) ){
                ((RectangleMapObject)object).getRectangle().getPosition(playerStartPositionRect);
                TiledMapPosition spawnPos = new TiledMapPosition().setPositionFromTiled(playerStartPositionRect.x,playerStartPositionRect.y);
            	startPositions.add(spawnPos);
                
            	TiledMapActor tiledactor = getActorAtScreenCoordinate(spawnPos);
                
                if(tiledactor != null) {
                	tiledactor.setIsFreeSpawn(true);
                }
            }
        }        
    }
    
	private TiledMapActor getActorAtScreenCoordinate(TiledMapPosition pos) {
		return tiledmapstage.getTiledMapActors()[pos.getTileX()][pos.getTileY()];
	}
    
    public void makeSpawnParticles() {
    	for(TiledMapPosition pos : unitSpawnPositions) {
            ParticleMaker.addParticle(ParticleType.SPAWN, pos);
    	}
    }
    
	public List<TiledMapPosition> getSpawnPositions(){
    	return unitSpawnPositions;
    }
    
    public TiledMapStage getTiledMapStage() {
    	return tiledmapstage;
    }
    
    @Override
    public MyNavigationTiledMapLayer getNavLayer() {
		return navLayer;
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
    
    public void dispose() {
    	initializeClassVariables();
    }
}

