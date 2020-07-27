package com.mygdx.game.AI;

import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.xguzm.pathfinding.grid.GridCell;

import com.mygdx.game.Entities.AiEntity;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;
import com.mygdx.game.Entities.PlayerEntity;
import com.mygdx.game.Map.MyPathFinder;

import Utility.TiledMapPosition;
import Utility.Utility;

public class AIDecisionMaker {
	private final AITeamLeader aiTeam;
	private boolean actionTaken = false;

	public AIDecisionMaker(final AITeamLeader aiTeam) {
		this.aiTeam = aiTeam;
	}

	public void makeDecision(AiEntity unit, List<PlayerEntity> playerUnits, List<AiEntity> aiUnits) {
		rule1CanKill(unit, playerUnits, aiUnits);

		if (!actionTaken) {
			rule2ShouldRun(unit, playerUnits, aiUnits);
		}

		if (!actionTaken) {
			rule3Engage(unit, playerUnits, aiUnits);
		}

		if (!actionTaken) {
			rule4SpellSpecific(unit, playerUnits, aiUnits);
		}

		if (!actionTaken) {
			unit.notifyEntityObserver(EntityCommand.AI_FINISHED_TURN);
		}
		actionTaken = false;

	}

	private void rule1CanKill(final AiEntity unit, List<PlayerEntity> playerUnits, List<AiEntity> aiUnits) {
		final List<Entity> entities = ListUtils.union(playerUnits, aiUnits);
		for (final Entity target : entities) {
			if (isEnemy(unit, target) && canMoveAttack(unit, target) && canKill(unit, target)) {
				walkOverAndAttack(unit, target);
				actionTaken = true;
			}
		}
	}

	private boolean isEnemy(final AiEntity attacker, final Entity target) {
		return attacker.isPlayerUnit() != target.isPlayerUnit();
	}

	private boolean canMoveAttack(final AiEntity attacker, final Entity target) {
		final int distance = Utility.getDistanceBetweenUnits(attacker, target) - 1;
		final int ap = attacker.getAp();
		final int basicAttackPoints = attacker.getEntityData().getBasicAttackCost();
		return (ap >= (distance + basicAttackPoints));
	}

	private boolean canKill(final AiEntity attacker, final Entity target) {
		return target.getHp() < attacker.getEntityData().getAttackPower();
	}

	private void walkOverAndAttack(final AiEntity attacker, final Entity target) {
		final MyPathFinder pathFinder = aiTeam.getMyPathFinder();
		final List<GridCell> path = pathFinder.getPathFromUnitToUnit(attacker, target);
		attacker.moveAttack(path, target);
	}

	private void rule2ShouldRun(final AiEntity unit, List<PlayerEntity> playerUnits, List<AiEntity> aiUnits) {
		if (healthIsLow(unit)) {
			runToSafety(unit, playerUnits, aiUnits);
			actionTaken = true;
		}
	}

	private boolean healthIsLow(final Entity unit) {
		final int currentHP = unit.getHp();
		final int maxHP = unit.getEntityData().getMaxHP();
		return (currentHP / maxHP) <= 0.1f;
	}

	private void runToSafety(final AiEntity unit, List<PlayerEntity> playerUnits, List<AiEntity> aiUnits) {
		final List<Entity> entities = ListUtils.union(playerUnits, aiUnits);
		final List<TiledMapPosition> positions = Utility.collectPositionsEnemyUnits(entities, unit.isPlayerUnit());
		unit.move(getSafestPath(unit, positions));
	}

	private List<GridCell> getSafestPath(final AiEntity unit, final List<TiledMapPosition> positions) {
		final TiledMapPosition centerOfGravity = calculateCenterOfGravity(positions);
		final TiledMapPosition furthestPoint = aiTeam.getMyPathFinder().getPositionFurthestAwayFrom(centerOfGravity);
		return aiTeam.getMyPathFinder().pathTowards(unit.getCurrentPosition(), furthestPoint, unit.getAp());
	}

	private TiledMapPosition calculateCenterOfGravity(final List<TiledMapPosition> positions) {
		final int numberOfElements = positions.size();
		int sumX = 0;
		int sumY = 0;

		for (int i = 0; i < numberOfElements; i++) {
			sumX += positions.get(i).getTileX();
			sumY += positions.get(i).getTileY();
		}

		return new TiledMapPosition().setPositionFromTiles(sumX / numberOfElements, sumY / numberOfElements);
	}

	private void rule3Engage(final AiEntity unit, List<PlayerEntity> playerUnits, List<AiEntity> aiUnits) {
		final List<Entity> entities = ListUtils.union(playerUnits, aiUnits);
		final List<TiledMapPosition> positions = Utility.collectPositionsEnemyUnits(entities, unit.isPlayerUnit());
		final TiledMapPosition centerOfGravity = calculateCenterOfGravity(positions);
		unit.move(aiTeam.getMyPathFinder().pathTowards(unit.getCurrentPosition(), centerOfGravity, unit.getAp()));
		actionTaken = true;
	}

	private void rule4SpellSpecific(final AiEntity unit, List<PlayerEntity> playerUnits, List<AiEntity> aiUnits) {
		unit.notifyEntityObserver(EntityCommand.AI_FINISHED_TURN);
		actionTaken = true;
	}
}
