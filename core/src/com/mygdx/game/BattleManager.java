package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;

public class BattleManager {
	private static final String TAG = BattleManager.class.getSimpleName();
	
	//this class organizes the flow of battle : unit ordering and action ordering
	private BattleState battlestate = BattleState.UNIT_PLACEMENT;
	private InputController _controller;
	private InputMultiplexer multiplexer;
	private Entity activeUnit;
	private ArrayList<Vector2> spawnPoints;
	
	private Entity[] units;
	private int activeUnitIndex;
	
	//TO-DO : at the start of battle decide on unit ordering and initiate the inputprocessor
	public BattleManager(InputMultiplexer inputmultiplexer,Entity[] playerSortedUnits) {
		this.multiplexer = inputmultiplexer;
		this.units = playerSortedUnits;
		this.activeUnitIndex = 0;
	}
	
	public ArrayList<Vector2> getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(ArrayList<Vector2> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	private void startNextPhase() {
		if (battlestate == BattleState.UNIT_PLACEMENT) {
			battlestate = BattleState.MOVEMENT_PHASE;
			Gdx.app.debug(TAG, "deployment finished, entering movementphase of the first unit (highest initiative)");
			_controller = new InputController(activeUnit);
			multiplexer.addProcessor(_controller);
		}
	}
	
	public BattleState getBattleState() {
		return battlestate;
	}
	
	public void setBattleState(BattleState battleState) {
		battlestate = battleState;
	}
	
	public void giveControlToUnit(Entity unit) {
		_controller.ChangePlayer(unit);
	}
	
	public void updateController(float delta) {
		if(_controller != null) {
			_controller.update(delta);
		}
	}
	
	public void dispose() {
		multiplexer.removeProcessor(_controller);
		if(_controller != null) {
			_controller.dispose();
		}
	}

	public Entity getActiveUnit() {
		return activeUnit;
	}

	public void setActiveUnit(Entity activeUnit) {
		this.activeUnit = activeUnit;
	}
	
	public void deployUnit(float x, float y) {
		Gdx.app.debug(TAG, "clicked on : " + x + " , " + y + " )");
		if((units != null) && activeUnitIndex < units.length) {
			//deploy unit
			Vector2 spawn = new Vector2(x,y);
			units[activeUnitIndex]._nextPlayerPosition = spawn;
			units[activeUnitIndex].setNextPositionToCurrent();
			units[activeUnitIndex].setInBattle(true);
			activeUnit = units[activeUnitIndex];
			removeSpawnPoint(spawn);
			
			//check if this is the last unit
			activeUnitIndex = ++activeUnitIndex;
			if (activeUnitIndex >= units.length) {
				startNextPhase();
			}
		}else {
			Gdx.app.debug(TAG, "can't deploy unit, units is null or activeunitindex is > the length of units");
		}

	}
	
	private void removeSpawnPoint(Vector2 spawnpoint) {
		Gdx.app.debug(TAG, "spawnPoints.size() = " + spawnPoints.size());
		for(int x = 0; x < spawnPoints.size(); x++) {
			if((spawnPoints.get(x).x == spawnpoint.x) && (spawnPoints.get(x).y == spawnpoint.y)) {
				spawnPoints.remove(x);
			}
		}
	}
}
