package com.jelte.norii.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.BattleStateGridHelper;
import com.jelte.norii.entities.AiEntity;
import com.jelte.norii.entities.EntityObserver;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.LineOfSight;
import com.jelte.norii.magic.Ability.Target;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.utility.TiledMapPosition;

public class AIDecisionMaker {
	private final AITeamLeader aiTeam;
	private final SortedMap<Integer, BattleState> statesWithScores;
	private final BattleStateGridHelper battleStateGridHelper;
	private MyPathFinder pathFinder;

	private static final int NUMBER_OF_LAYERS = 3;

	public AIDecisionMaker(final AITeamLeader aiTeam) {
		this.aiTeam = aiTeam;
		battleStateGridHelper = new BattleStateGridHelper();
		statesWithScores = new TreeMap<>();
	}

	public void makeDecision(List<PlayerEntity> playerUnits, List<AiEntity> aiUnits, BattleState stateOfBattle) {
		if (pathFinder == null) {
			pathFinder = aiTeam.getMyPathFinder();
		}

		for (final AiEntity ai : aiUnits) {
			generatePossibleMoves(playerUnits, ai, stateOfBattle);
		}

		if (!statesWithScores.isEmpty()) {
			final BattleState highestState = statesWithScores.get(statesWithScores.lastKey());
			highestState.getAi().notifyEntityObserver(EntityObserver.EntityCommand.FOCUS_CAMERA);

			if (highestState.getAbilityUsed() != null) {
				highestState.getAi().notifyEntityObserver(EntityObserver.EntityCommand.CAST_SPELL_AI, highestState.getAbilityUsed(), highestState.getTarget());
			}
		}
		System.out.println("move towards an enemy");
		final TiledMapPosition centerOfGravity = getClosestPlayerUnit(aiUnits.get(0), playerUnits);
		aiUnits.get(0).move(pathFinder.pathTowards(aiUnits.get(0).getCurrentPosition(), centerOfGravity, aiUnits.get(0).getAp()));
	}

	private void generatePossibleMoves(List<PlayerEntity> playerUnits, AiEntity ai, BattleState stateOfBattle) {
		for (final Ability ability : ai.getAbilities()) {
			addAllPosibilitiesForAbility(playerUnits, ai, stateOfBattle, ability);
		}
	}

	private void addAllPosibilitiesForAbility(List<PlayerEntity> playerUnits, AiEntity ai, BattleState stateOfBattle, Ability ability) {
		final Target target = ability.getTarget();
		if (target.equals(Target.SELF) || target.equals(Target.NO_TARGET)) {
			final Point targetPoint = new Point(ai.getCurrentPosition().getTileX(), ai.getCurrentPosition().getTileY());
			final BattleState newState = castAbilityOn(ability, targetPoint, stateOfBattle);
			newState.setAbilityUsed(ability);
			newState.setTarget(targetPoint);
			newState.setAi(ai);
			statesWithScores.put(newState.getScore(), newState);
		}

		if (target.equals(Target.CELL_BUT_NO_UNIT)) {
			final Point targetPoint = findRandomPointWithoutUnitOn(new Point(ai.getCurrentPosition().getTileX(), ai.getCurrentPosition().getTileY()), ability, stateOfBattle);
			final BattleState newState = castAbilityOn(ability, targetPoint, stateOfBattle);
			newState.setAbilityUsed(ability);
			newState.setTarget(targetPoint);
			newState.setAi(ai);
			statesWithScores.put(newState.getScore(), newState);
		}

		if (target.needsUnit(target)) {
			Set<Point> targets = battleStateGridHelper.findTargets(new Point(ai.getCurrentPosition().getTileX(), ai.getCurrentPosition().getTileY()), ability, stateOfBattle);

			if (targets.isEmpty()) {
				targets = tryMoveAndTarget(playerUnits, ai, ability, stateOfBattle);
			}

			// cant use spell, and also cant move and use spell
			if (targets.isEmpty()) {
				final BattleState newState = stateOfBattle.makeCopy();
				newState.setAi(ai);
				statesWithScores.put(newState.getScore(), newState);
			}

			for (final Point targetPoint : targets) {
				final BattleState newState = castAbilityOn(ability, targetPoint, stateOfBattle);
				newState.setAbilityUsed(ability);
				newState.setTarget(targetPoint);
				newState.setAi(ai);
				statesWithScores.put(newState.getScore(), newState);
			}
		}
	}

	private Point findRandomPointWithoutUnitOn(Point center, Ability ability, BattleState stateOfBattle) {
		final LineOfSight lineOfSight = ability.getLineOfSight();
		final int range = ability.getSpellData().getRange();
		final Set<Point> possibleCenterCells = new HashSet<>();
		battleStateGridHelper.getPossibleCenterCells(possibleCenterCells, center, lineOfSight, range);
		for (final Point point : possibleCenterCells) {
			if (stateOfBattle.get(point.x, point.y) == 0) {
				return point;
			}
		}
		return null;
	}

	private Set<Point> tryMoveAndTarget(List<PlayerEntity> playerUnits, AiEntity ai, Ability ability, BattleState stateOfBattle) {
		int ap = ai.getAp();
		while (ap > 0) {
			// move 1 step closer and try again
			final TiledMapPosition centerOfGravity = getClosestPlayerUnit(ai, playerUnits);
			final TiledMapPosition aiPos = ai.getCurrentPosition();
			stateOfBattle.moveUnitFromTo(ai, new Point(aiPos.getTileX(), aiPos.getTileY()), stateOfBattle.stepFromTowards(new Point(aiPos.getTileX(), aiPos.getTileY()), new Point(centerOfGravity.getTileX(), centerOfGravity.getTileY())));
			ap--;
			final Set<Point> targets = battleStateGridHelper.findTargets(new Point(ai.getCurrentPosition().getTileX(), ai.getCurrentPosition().getTileY()), ability, stateOfBattle);

			if (!targets.isEmpty()) {
				return targets;
			}
		}
		return new HashSet<>();
	}

	private BattleState castAbilityOn(Ability ability, Point point, BattleState stateOfBattle) {
		switch (ability.getAbilityEnum()) {
		case FIREBALL:
			final int originalValue = stateOfBattle.get(point.x, point.y);
			int newValue = originalValue - ability.getSpellData().getDamage();
			if (newValue < 0) {
				newValue = 0;
			}
			stateOfBattle.set(point.x, point.y, newValue);
			return stateOfBattle;
		case TURN_TO_STONE:
			return stateOfBattle;
		case SWAP:
			return stateOfBattle;
		case HAMMERBACK:
			return stateOfBattle;
		default:
			return stateOfBattle;
		}

	}

	private TiledMapPosition calculateCenterOfGravityFromPositions(final List<Point> positions) {
		final int numberOfElements = positions.size();
		int sumX = 0;
		int sumY = 0;

		for (int i = 0; i < numberOfElements; i++) {
			sumX += positions.get(i).x;
			sumY += positions.get(i).y;
		}

		return new TiledMapPosition().setPositionFromTiles(sumX / numberOfElements, sumY / numberOfElements);
	}

	private TiledMapPosition calculateCenterOfGravity(List<PlayerEntity> playerUnits) {
		final List<Point> positions = new ArrayList<>();
		for (final PlayerEntity entity : playerUnits) {
			positions.add(new Point(entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY()));
		}
		return calculateCenterOfGravityFromPositions(positions);
	}

	private TiledMapPosition getClosestPlayerUnit(AiEntity aiEntity, List<PlayerEntity> playerUnits) {
		PlayerEntity closestEntity = playerUnits.get(0);
		int distance = closestEntity.getCurrentPosition().getDistance(aiEntity.getCurrentPosition());
		for (final PlayerEntity entity : playerUnits) {
			if (entity.getCurrentPosition().getDistance(aiEntity.getCurrentPosition()) < distance) {
				distance = entity.getCurrentPosition().getDistance(aiEntity.getCurrentPosition());
				closestEntity = entity;
			}
		}
		return closestEntity.getCurrentPosition();
	}

}
