package com.mygdx.game.Map;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Battle.BattleState;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

public class TiledMapClickListener extends ClickListener {
	private static final String TAG = TiledMapClickListener.class.getSimpleName();

    private TiledMapActor actor;

    public TiledMapClickListener(TiledMapActor actor) {
        this.actor = actor;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
    	
    	switch(actor.getBattlemanager().getBattleState()) {
    	  case UNIT_PLACEMENT:
    	    Gdx.app.debug(TAG, "deploying unit!");
    	    deployUnit();
    	    break;
    	  case MOVEMENT_PHASE:
    		  Gdx.app.debug(TAG, "you can move now!");
    	    possibleMove();
    	    break;
    	  default:
    	    // code block
    	}    
    }
    
    private void deployUnit(){
        if(actor.getIsFreeSpawn()) {
        	TiledMapPosition newPosition = new TiledMapPosition(actor.getX(),actor.getY());
        	actor.getBattlemanager().deployUnit(newPosition);
        	actor.setIsFreeSpawn(false);
        }
    }
    
    private void possibleMove() {
    	Entity currentUnit = actor.getBattlemanager().getActiveUnit();
    	int currentXInTiled = currentUnit.getCurrentPosition().getTileX();
    	int currentYInTiled = currentUnit.getCurrentPosition().getTileY();
    	List<GridCell> path = actor.getBattlemanager().getPathfinder().getCellsWithin(currentXInTiled, currentYInTiled, currentUnit.getMp());
    	//test if spot is inside path, then move unit there
    	for(int i = 0;i<path.size();i++) {
    		if((path.get(i).x == currentXInTiled) && (path.get(i).y == currentYInTiled)) {
    			//deactivate particle
    			ParticleMaker.getParticle(ParticleType.MOVE, currentUnit.getCurrentPosition());
    			
    			TiledMapPosition newUnitPos = new TiledMapPosition((int)actor.getX() * Map.UNIT_SCALE, (int)actor.getY() * Map.UNIT_SCALE);
    			currentUnit.setCurrentPosition(newUnitPos);
    			currentUnit.setInActionPhase(true);
    			currentUnit.setInMovementPhase(false);
    			
    			//set battlestate to action (a button should be made to revert this)
    			actor.getBattlemanager().setBattleState(BattleState.ACTION_PHASE);
    		}
    	}
    }
}
