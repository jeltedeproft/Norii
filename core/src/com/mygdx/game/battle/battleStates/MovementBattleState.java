package com.mygdx.game.battle.battleStates;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.mygdx.game.battle.BattleManager;
import com.mygdx.game.entities.EntityObserver.EntityCommand;
import com.mygdx.game.entities.PlayerEntity;
import com.mygdx.game.map.TiledMapActor;
import com.mygdx.game.particles.ParticleMaker;
import com.mygdx.game.particles.ParticleType;

import utility.TiledMapPosition;

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
		battlemanager.getActiveUnit().setLocked(true);
		battlemanager.getActiveUnit().notifyEntityObserver(EntityCommand.UNIT_LOCKED);
		updateUnit(pos);
		battlemanager.getCurrentBattleState().exit();
	}

	private void updateUnit(TiledMapPosition pos) {
		final PlayerEntity currentUnit = battlemanager.getActiveUnit();
		final TiledMapPosition newUnitPos = new TiledMapPosition().setPositionFromTiles(pos.getTileX(), pos.getTileY());
		List<GridCell> path = battlemanager.getPathFinder().pathTowards(currentUnit.getCurrentPosition(), newUnitPos, currentUnit.getAp());
		currentUnit.move(path);
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