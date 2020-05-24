package com.mygdx.game.AI;

import java.util.ArrayList;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;
import com.mygdx.game.Map.MyPathFinder;

import Utility.TiledMapPosition;
import Utility.Utility;

public class AIDecisionMaker {
	private final AITeam aiTeam;
	private boolean actionTaken = false;

	public AIDecisionMaker(AITeam aiTeam) {
		this.aiTeam = aiTeam;
	}

	public void makeDecision(Entity unit, ArrayList<Entity> entities) {

		rule1CanKill(unit, entities);

		if (!actionTaken) {
			rule2ShouldRun(unit, entities);
		}

		
		if (!actionTaken) {
			rule3SpellSpecific(unit, entities);
		}

		if (!actionTaken) {
			unit.notifyEntityObserver(EntityCommand.AI_FINISHED_TURN);
		}
		actionTaken = false;
	}

	private void rule1CanKill(Entity unit, ArrayList<Entity> entities) {
		for (final Entity target : entities) {
			if (isEnemy(unit, target) && canMoveAttack(unit, target) && canKill(unit, target)) {
				walkOverAndAttack(unit, target);
				actionTaken = true;
			}
		}
	}

	private boolean isEnemy(Entity attacker, Entity target) {
		return attacker.isPlayerUnit() != target.isPlayerUnit();
	}

	private boolean canMoveAttack(Entity attacker, Entity target) {
		final int distance = Utility.getDistanceBetweenUnits(attacker, target) - 1;
		final int ap = attacker.getAp();
		final int basicAttackPoints = attacker.getEntityData().getBasicAttackCost();
		return (ap >= (distance + basicAttackPoints));
	}

	private boolean canKill(Entity attacker, final Entity target) {
		return target.getHp() < attacker.getEntityData().getAttackPower();
	}

	private void walkOverAndAttack(Entity attacker, Entity target) {
		final MyPathFinder pathFinder = aiTeam.getMyPathFinder();
		final List<GridCell> path = pathFinder.getPathFromUnitToUnit(attacker, target);
		attacker.moveAttack(path, target);
	}

	private void rule2ShouldRun(Entity unit, ArrayList<Entity> entities) {
		if (healthIsLow(unit)) {
			runToSafety(unit, entities);
			actionTaken = true;
		}
	}

	private boolean healthIsLow(Entity unit) {
		final int currentHP = unit.getHp();
		final int maxHP = unit.getEntityData().getMaxHP();
		return (currentHP / maxHP) <= 0.1f;
	}

	private void runToSafety(Entity unit, ArrayList<Entity> entities) {
		final ArrayList<TiledMapPosition> positions = Utility.collectPositionsEnemeyUnits(entities, unit.isPlayerUnit());
		unit.move(getSafestPath(unit, positions));
	}

	private List<GridCell> getSafestPath(Entity unit, ArrayList<TiledMapPosition> positions) {
		final TiledMapPosition centerOfGravity = calculateCenterOfGravity(positions);
		final TiledMapPosition furthestPoint = aiTeam.getMyPathFinder().getPositionFurthestAwayFrom(centerOfGravity);
		return aiTeam.getMyPathFinder().pathTowards(unit.getCurrentPosition(), furthestPoint, unit.getAp());
	}

	private TiledMapPosition calculateCenterOfGravity(ArrayList<TiledMapPosition> positions) {
		final int numberOfElements = positions.size();
		int sumX = 0;
		int sumY = 0;

		for (int i = 0; i < numberOfElements; i++) {
			sumX += positions.get(i).getTileX();
			sumY += positions.get(i).getTileY();
		}

		return new TiledMapPosition().setPositionFromTiles(sumX / numberOfElements, sumY / numberOfElements);
	}

	private void rule3SpellSpecific(Entity unit, ArrayList<Entity> entities) {

	}
}
