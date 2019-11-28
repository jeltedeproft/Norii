package com.mygdx.game.Battle;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

public class AttackBattleState implements BattleState{
	private static final String TAG = AttackBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;
	private Entity currentUnit;
	

	public AttackBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}
	
	public void clickedOnTile(TiledMapActor actor) {
		possibleAttack(actor);
	}

	@Override
	public void entry() {
		Gdx.app.debug(TAG, "entering attack phase");
	}

	@Override
	public void update() {
		
	}

	@Override
	public void exit() {
		Gdx.app.debug(TAG, "exiting attack phase");
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}
	
    private void possibleAttack(TiledMapActor actor) {
    	List<GridCell> path = calculatePath(actor);
    	
    	GridCell cellToAttack = newPositionInPath(actor,path);
    	if(cellToAttack != null) {
    		moveUnit(actor, cellToMoveTo);
    	}	
    }
    
    private List<GridCell> calculatePath(TiledMapActor actor){
    	currentUnit = battlemanager.getActiveUnit();
    	int centreX = currentUnit.getCurrentPosition().getTileX();
    	int centreY = currentUnit.getCurrentPosition().getTileY();
    	return actor.getTiledMap().getPathfinder().getCellsWithin(centreX, centreY, currentUnit.getMp());
    }

    
	private GridCell newPositionInPath(TiledMapActor actor, List<GridCell> path) {
		TiledMapPosition newPos = actor.getActorPos(); 
		for(int i = 0;i<path.size();i++) {
			if((path.get(i).x == newPos.getTileX()) && (path.get(i).y == newPos.getTileY()) && path.get(i).isWalkable()) {
				return path.get(i);
			}
		}
		return null;
	}
}
