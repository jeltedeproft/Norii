package com.mygdx.game.Battle;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

public class MovementBattleState implements BattleState{
	private BattleManager battlemanager;
	private Entity currentUnit;
	

	public MovementBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}
	
	public void clickedOnTile(TiledMapActor actor) {
		possibleMove(actor);
	}

	@Override
	public void entry() {
		battlemanager.getActiveUnit().setInActionPhase(false);
	}

	@Override
	public void update() {
		
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}
	
    private void possibleMove(TiledMapActor actor) {
    	List<GridCell> path = calculatePath(actor);
    	GridCell cellToMoveTo = isPositionInPath(actor,path);
    	
    	if(cellToMoveTo != null) {
    		moveUnit(cellToMoveTo);
    	}	
    }
    
    private List<GridCell> calculatePath(TiledMapActor actor){
    	currentUnit = battlemanager.getActiveUnit();
    	int centreX = currentUnit.getCurrentPosition().getTileX();
    	int centreY = currentUnit.getCurrentPosition().getTileY();
    	return actor.getTiledMap().getPathfinder().getCellsWithin(centreX, centreY, currentUnit.getMp());
    }

	private GridCell isPositionInPath(TiledMapActor actor, List<GridCell> path) {
		TiledMapPosition newPos = actor.getActorPos(); 
		for(int i = 0;i<path.size();i++) {
			if((path.get(i).x == newPos.getTileX()) && (path.get(i).y == newPos.getTileY()) && path.get(i).isWalkable()) {
				return path.get(i);
			}
		}
		return null;
	}
   
	private void moveUnit(GridCell cellToMoveTo) { 
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE); 
		updateMP(cellToMoveTo);
		updateUnit(cellToMoveTo);  			
		battlemanager.getCurrentBattleState().exit();
	}
	
	private void updateMP(GridCell cellToMoveTo) {
		int distance = calculateDistance(cellToMoveTo);
		if(distance >= currentUnit.getMp()) {
			currentUnit.setMp(0);
		}else {
			currentUnit.setMp(currentUnit.getMp() - distance);
		}
	}
	
	private int calculateDistance(GridCell to) {
    	int centreX = currentUnit.getCurrentPosition().getTileX();
    	int centreY = currentUnit.getCurrentPosition().getTileY();
		return Math.abs(centreX - to.x) + Math.abs(centreY - to.y);
	}

	private void updateUnit(GridCell cellToMoveTo) {
		TiledMapPosition newUnitPos = new TiledMapPosition().setPositionFromTiles(cellToMoveTo.x, cellToMoveTo.y);
		currentUnit.setCurrentPosition(newUnitPos);
		currentUnit.setInActionPhase(true);
		currentUnit.setInMovementPhase(false);
	}

	@Override
	public void clickedOnUnit(Entity entity) {
			
	}
}
