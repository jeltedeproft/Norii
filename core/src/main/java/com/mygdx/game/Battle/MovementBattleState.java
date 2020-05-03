package com.mygdx.game.Battle;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

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

	private List<GridCell> calculatePath(TiledMapActor actor) {
		final Entity currentUnit = battlemanager.getActiveUnit();
		final int centreX = currentUnit.getCurrentPosition().getTileX();
		final int centreY = currentUnit.getCurrentPosition().getTileY();
		return actor.getTiledMap().getPathfinder().getCellsWithinCircle(centreX, centreY, currentUnit.getAp());
	}

	private GridCell isPositionInPath(TiledMapActor actor, List<GridCell> path) {
		final TiledMapPosition newPos = actor.getActorPos();
		for (int i = 0; i < path.size(); i++) {
			if ((path.get(i).x == newPos.getTileX()) && (path.get(i).y == newPos.getTileY()) && path.get(i).isWalkable()) {
				return path.get(i);
			}
		}
		return null;
	}

	private void moveUnit(TiledMapPosition pos) {
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE);
		updateMP(pos);
		updateUnit(pos);
		battlemanager.getCurrentBattleState().exit();
	}

	private void updateMP(TiledMapPosition pos) {
		final Entity currentUnit = battlemanager.getActiveUnit();
		final int distance = calculateDistance(pos);
		if (distance >= currentUnit.getAp()) {
			currentUnit.setAp(0);
		} else {
			currentUnit.setAp(currentUnit.getAp() - distance);
		}
	}

	private int calculateDistance(TiledMapPosition pos) {
		final Entity currentUnit = battlemanager.getActiveUnit();
		final int centreX = currentUnit.getCurrentPosition().getTileX();
		final int centreY = currentUnit.getCurrentPosition().getTileY();
		return Math.abs(centreX - pos.getTileX()) + Math.abs(centreY - pos.getTileY());
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
