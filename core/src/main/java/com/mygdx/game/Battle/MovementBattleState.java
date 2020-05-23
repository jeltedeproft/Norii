package com.mygdx.game.Battle;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;
import Utility.Utility;

public class MovementBattleState extends BattleState {
	private final BattleManager battlemanager;

	public MovementBattleState(BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	@Override
	public void clickedOnTile(TiledMapActor actor) {
		possibleMove(actor);
	}

	@Override
	public void entry() {
		battlemanager.getActiveUnit().setInActionPhase(false);
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	private void possibleMove(TiledMapActor actor) {
		if (actor.getTiledMap().getPathfinder().canUnitWalkTo(battlemanager.getActiveUnit(), actor.getActorPos())) {
			moveUnit(actor.getActorPos());
		}
	}

	private void moveUnit(TiledMapPosition pos) {
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE);
		updateMP(pos);
		updateUnit(pos);
		battlemanager.getCurrentBattleState().exit();
	}

	private void updateMP(TiledMapPosition pos) {
		final Entity currentUnit = battlemanager.getActiveUnit();
		final int distance = Utility.getDistance(pos, battlemanager.getActiveUnit());
		if (distance >= currentUnit.getAp()) {
			currentUnit.setAp(0);
		} else {
			currentUnit.setAp(currentUnit.getAp() - distance);
		}
	}

	private void updateUnit(TiledMapPosition pos) {
		final Entity currentUnit = battlemanager.getActiveUnit();
		final TiledMapPosition newUnitPos = new TiledMapPosition().setPositionFromTiles(pos.getTileX(), pos.getTileY());
		currentUnit.setCurrentPosition(newUnitPos);
		currentUnit.setInActionPhase(true);
		currentUnit.setInMovementPhase(false);
	}

	@Override
	public void keyPressed(int key) {
		switch (key) {
		case Keys.Q:
			break;
		default:
			break;
		}
	}

	@Override
	public void buttonPressed(int button) {
		switch (button) {
		case Buttons.RIGHT:
			ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE);
			exit();
			break;
		case Buttons.LEFT:
			break;
		case Buttons.MIDDLE:
			break;
		default:
			break;
		}
	}
}
