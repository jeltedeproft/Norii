package com.mygdx.game.Battle;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.InputController;
import com.mygdx.game.Map.BattleMap;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Map.MyPathFinder;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

public class BattleManager {
	private static final String TAG = BattleManager.class.getSimpleName();
	
	private BattleStates deploymentBattleState;
	private BattleStates movementBattleState;
	private BattleStates actionBattleState;
	private BattleStates waitOpponentBattleState;
	
	private BattleStates currentBattleState;
	
	private BattleState battlestate = BattleState.UNIT_PLACEMENT;
	private InputController _controller;
	private InputMultiplexer multiplexer;
	private Entity activeUnit;
	private ArrayList<TiledMapPosition> spawnPoints;
	private Entity[] units;
	private int activeUnitIndex;
	
	public BattleManager(InputMultiplexer inputmultiplexer,Entity[] playerSortedUnits) {
		this.multiplexer = inputmultiplexer;
		this.units = playerSortedUnits;
		this.activeUnitIndex = 0;
		this.activeUnit = playerSortedUnits[0];
		
		this.deploymentBattleState = new DeploymentBattleState(this);
		this.movementBattleState = new MovementBattleState(this);
		this.actionBattleState = new ActionBattleState(this);
		this.waitOpponentBattleState = new WaitOpponentBattleState(this);
		
		this.currentBattleState = deploymentBattleState;
		this.currentBattleState.entry();
	}
	
	public ArrayList<TiledMapPosition> getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(ArrayList<TiledMapPosition> spawnPoints2) {
		this.spawnPoints = spawnPoints2;
	}

	private void startNextPhase() {
		currentBattleState.exit();
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
	
	public InputController get_controller() {
		return _controller;
	}

	public void set_controller(InputController _controller) {
		this._controller = _controller;
	}

	public InputMultiplexer getMultiplexer() {
		return multiplexer;
	}

	public void setMultiplexer(InputMultiplexer multiplexer) {
		this.multiplexer = multiplexer;
	}

	public void deployUnit(TiledMapPosition pos) {
		if((units != null) && activeUnitIndex < units.length) {
			changeActiveUnit(pos);
			ParticleMaker.deactivateParticle(ParticleMaker.getParticle(ParticleType.SPAWN, pos));
			removeSpawnPoint(pos);
	
			activeUnitIndex++;
			checkIfLastUnit();
		}else {
			Gdx.app.debug(TAG, "can't deploy unit, units is null or activeunitindex is > the length of units");
		}
	}
	
	private void changeActiveUnit(TiledMapPosition pos) {
		Entity currentActiveUnit = units[activeUnitIndex];
		currentActiveUnit.setInBattle(true);
		currentActiveUnit.setCurrentPosition(pos);
		activeUnit = currentActiveUnit;
	}
	
	private void checkIfLastUnit() {
		if (activeUnitIndex >= units.length) {
			Gdx.app.debug(TAG, "starting next phase, current phase = " + battlestate);
			startNextPhase();
		}
	}
	
	private void removeSpawnPoint(TiledMapPosition pos) {
		for(int i = 0; i < spawnPoints.size(); i++) {
			if((spawnPoints.get(i).isEqualTo(pos))) {
				spawnPoints.remove(i);
			}
		}
	}

	public BattleStates getDeploymentBattleState() {
		return deploymentBattleState;
	}

	public void setDeploymentBattleState(BattleStates deploymentBattleState) {
		this.deploymentBattleState = deploymentBattleState;
	}

	public BattleStates getMovementBattleState() {
		return movementBattleState;
	}

	public void setMovementBattleState(BattleStates movementBattleState) {
		this.movementBattleState = movementBattleState;
	}

	public BattleStates getActionBattleState() {
		return actionBattleState;
	}

	public void setActionBattleState(BattleStates actionBattleState) {
		this.actionBattleState = actionBattleState;
	}

	public BattleStates getWaitOpponentBattleState() {
		return waitOpponentBattleState;
	}

	public void setWaitOpponentBattleState(BattleStates waitOpponentBattleState) {
		this.waitOpponentBattleState = waitOpponentBattleState;
	}

	public BattleStates getCurrentBattleState() {
		return currentBattleState;
	}

	public void setCurrentBattleState(BattleStates currentBattleState) {
		this.currentBattleState = currentBattleState;
	}
	
	
}
