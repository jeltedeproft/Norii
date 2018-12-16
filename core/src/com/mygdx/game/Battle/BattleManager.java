package com.mygdx.game.Battle;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utility;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.InputController;

public class BattleManager {
	private static final String TAG = BattleManager.class.getSimpleName();
	
	private BattleState battlestate = BattleState.UNIT_PLACEMENT;
	private InputController _controller;
	private InputMultiplexer multiplexer;
	private Entity activeUnit;
	private ArrayList<Vector2> spawnPoints;
	
	private Entity[] units;
	private int activeUnitIndex;
	

	public BattleManager(InputMultiplexer inputmultiplexer,Entity[] playerSortedUnits) {
		this.multiplexer = inputmultiplexer;
		this.units = playerSortedUnits;
		this.activeUnitIndex = 0;
		this.activeUnit = playerSortedUnits[0];
	}
	
	public ArrayList<Vector2> getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(ArrayList<Vector2> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	private void startNextPhase() {
		if (battlestate == BattleState.UNIT_PLACEMENT) {
			battlestate = BattleState.ACTION_SELECTION;
			Gdx.app.debug(TAG, "deployment finished, entering action selection of the first unit (highest initiative)");
			_controller = new InputController(activeUnit);
			multiplexer.addProcessor(_controller);
			
			//activate actions UI
			activeUnit.setActive(true);
			activeUnit.getActionsui().update();
			
			//unload spawn particles
			//Utility.unloadAsset("particles/spawn_effect");
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
		if((units != null) && activeUnitIndex < units.length) {
			//deploy unit
			units[activeUnitIndex].setInBattle(true);
			Vector2 spawn = new Vector2(x,y);
			units[activeUnitIndex].setCurrentPosition(x, y);
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
