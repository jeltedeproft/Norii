
package com.mygdx.game.Map;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
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
    	_convertedUnits = new TiledMapPosition();
    	
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
                TiledMapPosition spawnPos = new TiledMapPosition().setPositionFromTiled(_playerStartPositionRect.x,_playerStartPositionRect.y);
            	startPositions.add(spawnPos);
                
                //tag tiles that can be used as spawns
            	TiledMapActor tiledactor = getActorAtScreenCoordinate(spawnPos);
                
                if(tiledactor != null) {
                	tiledactor.setIsFreeSpawn(true);
                }
            }
        }        
    }
    
	public void drawActorsDebug() {
		ShapeRenderer debugRenderer = new ShapeRenderer();
        debugRenderer.setProjectionMatrix(this.getTiledMapStage().getCamera().combined);
        debugRenderer.setColor(Color.RED);
        debugRenderer.begin(ShapeType.Line);
        
    	for(TiledMapPosition pos : _unitSpawnPositions) {
    		TiledMapActor actor = getActorAtScreenCoordinate(pos);
        	// While resizing the screen, the actor wont be available until letting go of the resize.
        	if ( actor != null ) {
        	    actor.debug();
                actor.drawDebug(debugRenderer);
            }
    	}
		debugRenderer.end();
	}

	private TiledMapActor getActorAtScreenCoordinate(TiledMapPosition pos) {
		Array<Actor> Actors = tiledmapstage.getActors();
		
		for(Actor actor : Actors) {
			TiledMapPosition actorPos = new TiledMapPosition().setPositionFromOriginal(actor.getX(), actor.getY());
			if((actorPos.getRealScreenX() == pos.getRealScreenX()) &&  (actorPos.getRealScreenY() == pos.getRealScreenY())){
				return (TiledMapActor) actor;
			}
		}
		return null;
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
    
    public void dispose() {
    	initializeClassVariables();
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

