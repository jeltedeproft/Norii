package com.jelte.norii.battle.battleStates;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.entities.EntityObserver.EntityCommand;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.map.TiledMapActor;
import com.jelte.norii.particles.ParticleMaker;
import com.jelte.norii.particles.ParticleType;
import com.jelte.norii.utility.TiledMapPosition;

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
		final List<GridCell> path = battlemanager.getPathFinder().pathTowards(currentUnit.getCurrentPosition(),
				newUnitPos, currentUnit.getAp());
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
