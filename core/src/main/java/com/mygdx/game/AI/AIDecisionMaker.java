package com.mygdx.game.AI;

import java.util.ArrayList;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.MyPathFinder;

import Utility.Utility;

public class AIDecisionMaker {
	private final AITeam aiTeam;

	public AIDecisionMaker(AITeam aiTeam) {
		this.aiTeam = aiTeam;
	}

	public void makeDecision(Entity unit, ArrayList<Entity> entities) {

		// can I kill someone with a basic attack
		for (final Entity target : entities) {

			if (isEnemy(unit, target) && canMoveAttack(unit, target) && canKill(unit, target)) {
				walkOverAndAttack(unit, target);
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
}
