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

public class MovementBattleState implements BattleState{
	private static final String TAG = MovementBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;
	

	public MovementBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}
	
	public void clickedOnTile(TiledMapActor actor) {
		possibleMove(actor);
	}

	@Override
	public void entry() {
		Gdx.app.debug(TAG, "entering movement phase");
		battlemanager.giveControlToNextUnit();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}
	
    private void possibleMove(TiledMapActor actor) {
    	//PATH
    	Entity currentUnit = battlemanager.getActiveUnit();
    	int centreX = currentUnit.getCurrentPosition().getTileX();
    	int centreY = currentUnit.getCurrentPosition().getTileY();
    	List<GridCell> path = actor.getTiledMap().getPathfinder().getCellsWithin(centreX, centreY, currentUnit.getMp());
    	
    	//NEW POS
    	TiledMapPosition newPos = new TiledMapPosition().setPositionFromScreen(actor.getX(), actor.getY());
    	
    	//NEW POS IN PATH?
    	for(int i = 0;i<path.size();i++) {
    		if((path.get(i).x == newPos.getTileX()) && (path.get(i).y == newPos.getTileY()) && path.get(i).isWalkable()) {
    			ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE);
    			
    			TiledMapPosition newUnitPos = new TiledMapPosition().setPositionFromTiles(path.get(i).x, path.get(i).y);
    			currentUnit.setCurrentPosition(newUnitPos);
    			currentUnit.setInActionPhase(true);
    			currentUnit.setInMovementPhase(false);

    			battlemanager.getCurrentBattleState().exit();
    		}
    	}
    }
}
