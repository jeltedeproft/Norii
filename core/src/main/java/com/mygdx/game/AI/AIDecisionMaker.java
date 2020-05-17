package com.mygdx.game.AI;

import java.util.ArrayList;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.MyPathFinder;

import Utility.TiledMapPosition;
import Utility.Utility;

public class AIDecisionMaker {
	private final AITeam aiTeam;

	public AIDecisionMaker(AITeam aiTeam) {
		this.aiTeam = aiTeam;
	}

	public void makeDecision(Entity unit, ArrayList<Entity> entities) {

		// can I kill someone with a basic attack
		for (final Entity entity : entities) {

			if (canMoveAttack(unit, entity) && canKill(unit, entity)) {
				moveUnitNextTo(unit, entity);
				unit.attack(entity);
			}
		}

	}

	private boolean canMoveAttack(Entity unit, Entity entity) {
		final int distance = Utility.getDistanceBetweenUnits(unit, entity) - 1;
		final int ap = unit.getAp();
		final int basicAttackPoints = unit.getEntityData().getBasicAttackCost();
		return (ap >= (distance + basicAttackPoints));
	}

	private boolean canKill(Entity unit, final Entity entity) {
		return entity.getHp() < unit.getEntityData().getAttackPower();
	}

	private void moveUnitNextTo(Entity unit, Entity entity) {
		final MyPathFinder pathFinder = aiTeam.getMyPathFinder();
		final List<GridCell> path = pathFinder.getPathFromUnitToUnit(unit, entity);
		if (path != null) {
			moveSlowlyTowards(unit, path);
		}
	}

	private void moveSlowlyTowards(Entity unit, List<GridCell> path) {
		for (final GridCell cell : path) {
			unit.setCurrentPosition(new TiledMapPosition().setPositionFromTiled(cell.x, cell.y));
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
