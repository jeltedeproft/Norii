package com.mygdx.game.Battle;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.InputController;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Map.TiledMapStage;
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
		
//		Entity currentlyActiveUnit = battlemanager.getActiveUnit();
//		InputController inputcontroller = battlemanager.get_controller();
//		InputMultiplexer multiplexer = battlemanager.getMultiplexer();
//		inputcontroller = new InputController(currentlyActiveUnit);
//		multiplexer.addProcessor(inputcontroller);
		battlemanager.giveControlToUnit(unit);
		
		//activate actions UI
//		currentlyActiveUnit.setActive(true);
//		currentlyActiveUnit.getActionsui().update();	
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
    	//CURRENT POS
    	TiledMapStage stage = (TiledMapStage) actor.getStage();
    	Entity currentUnit = stage.getBattlemanager().getActiveUnit();
    	int currentXInTiled = currentUnit.getCurrentPosition().getTileX();
    	int currentYInTiled = currentUnit.getCurrentPosition().getTileY();
    	
    	//NEW POS
    	List<GridCell> path = actor.getTiledMap().getPathfinder().getCellsWithin(currentXInTiled, currentYInTiled, currentUnit.getMp());
    	for(int i = 0;i<path.size();i++) {
    		if((path.get(i).x == currentXInTiled) && (path.get(i).y == currentYInTiled) && path.get(i).isWalkable()) {
    			ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE);
    			
    			TiledMapPosition newUnitPos = new TiledMapPosition((int)actor.getX() * Map.UNIT_SCALE, (int)actor.getY() * Map.UNIT_SCALE);
    			currentUnit.setCurrentPosition(newUnitPos);
    			currentUnit.setInActionPhase(true);
    			currentUnit.setInMovementPhase(false);

    			stage.getBattlemanager().getCurrentBattleState().exit();
    		}
    	}
    }

}
