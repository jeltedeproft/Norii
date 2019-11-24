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
	private Entity currentUnit;
	

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
    	List<GridCell> path = calculatePath(actor);
    	
    	GridCell cellToMoveTo = newPositionInPath(actor,path);
    	if(cellToMoveTo != null) {
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
   
	private void moveUnit(TiledMapActor actor, GridCell cellToMoveTo) { 
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE);
		
		setOldCellWalkable(actor);		
		updateUnit(cellToMoveTo);   			
		setNewCellNotWalkable(actor);
		
		battlemanager.getCurrentBattleState().exit();
	}

	private void setOldCellWalkable(TiledMapActor actor) {
		int centreX = currentUnit.getCurrentPosition().getTileX();
		int centreY = currentUnit.getCurrentPosition().getTileY();
		actor.getTiledMap().getNavLayer().getCell(centreX,centreY).setWalkable(true);
	}

	private void updateUnit(GridCell cellToMoveTo) {
		TiledMapPosition newUnitPos = new TiledMapPosition().setPositionFromTiles(cellToMoveTo.x, cellToMoveTo.y);
		currentUnit.setCurrentPosition(newUnitPos);
		currentUnit.setInActionPhase(true);
		currentUnit.setInMovementPhase(false);
	}

	private void setNewCellNotWalkable(TiledMapActor actor) {
		int newCentreX = currentUnit.getCurrentPosition().getTileX();
		int newCentreY = currentUnit.getCurrentPosition().getTileY();
		actor.getTiledMap().getNavLayer().getCell(newCentreX,newCentreY).setWalkable(false);
	}
	

}
