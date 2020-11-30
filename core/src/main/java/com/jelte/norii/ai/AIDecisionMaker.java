package com.jelte.norii.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleStates.StateOfBattle;
import com.jelte.norii.entities.AiEntity;
import com.jelte.norii.entities.EntityObserver;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.AffectedTeams;
import com.jelte.norii.magic.Ability.AreaOfEffect;
import com.jelte.norii.magic.Ability.LineOfSight;
import com.jelte.norii.magic.Ability.Target;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.utility.TiledMapPosition;

public class AIDecisionMaker {
	private final AITeamLeader aiTeam;
	private final boolean actionTaken = false;
	private final SortedMap<Integer, StateOfBattle> statesWithScores;
	private MyPathFinder pathFinder;

	private static final int NUMBER_OF_LAYERS = 3;

	public AIDecisionMaker(final AITeamLeader aiTeam) {
		this.aiTeam = aiTeam;
		statesWithScores = new TreeMap<>();
	}

	public void makeDecision(List<PlayerEntity> playerUnits, List<AiEntity> aiUnits, StateOfBattle stateOfBattle) {
		final int iteration = NUMBER_OF_LAYERS;

		if (pathFinder == null) {
			pathFinder = aiTeam.getMyPathFinder();
		}

		// while (iteration > 0) {
		for (final AiEntity ai : aiUnits) {
			generatePossibleMoves(ai, stateOfBattle);
		}
		// iteration--;
		// }

		if (!statesWithScores.isEmpty()) {
			final StateOfBattle highestState = statesWithScores.get(statesWithScores.lastKey());
			highestState.getAi().notifyEntityObserver(EntityObserver.EntityCommand.FOCUS_CAMERA);
			System.out.println("casting : " + highestState.getAbilityUsed().getName() + " on position : (" + highestState.getTarget().getX() + "," + highestState.getTarget().getY() + ")");
			highestState.getAi().notifyEntityObserver(EntityObserver.EntityCommand.CAST_SPELL_AI, highestState.getAbilityUsed(), highestState.getTarget());
		}
		System.out.println("and also moving now after maybe an action");
		final TiledMapPosition centerOfGravity = calculateCenterOfGravity(playerUnits);
		aiUnits.get(0).move(pathFinder.pathTowards(aiUnits.get(0).getCurrentPosition(), centerOfGravity, aiUnits.get(0).getAp()));
	}

	private void generatePossibleMoves(AiEntity ai, StateOfBattle stateOfBattle) {
		for (final Ability ability : ai.getAbilities()) {
			addAllPosibilitiesForAbility(ai, stateOfBattle, ability);
		}
	}

	private void addAllPosibilitiesForAbility(AiEntity ai, StateOfBattle stateOfBattle, Ability ability) {
		final Target target = ability.getTarget();
		if (target.equals(Target.SELF)) {
			final Point targetPoint = new Point(ai.getCurrentPosition().getTileX(), ai.getCurrentPosition().getTileY());
			final StateOfBattle newState = castAbilityOn(ability, targetPoint, stateOfBattle);
			newState.setAbilityUsed(ability);
			newState.setTarget(targetPoint);
			newState.setAi(ai);
			statesWithScores.put(newState.getScore(), newState);
		}
		if (target.needsUnit(target)) {
			final Array<Point> targets = findTargets(new Point(ai.getCurrentPosition().getTileX(), ai.getCurrentPosition().getTileY()), ability, stateOfBattle);
			for (final Point targetPoint : targets) {
				final StateOfBattle newState = castAbilityOn(ability, targetPoint, stateOfBattle);
				newState.setAbilityUsed(ability);
				newState.setTarget(targetPoint);
				newState.setAi(ai);
				statesWithScores.put(newState.getScore(), newState);
			}
		}

		if (target.equals(Target.NO_TARGET)) {
			final Point targetPoint = new Point(ai.getCurrentPosition().getTileX(), ai.getCurrentPosition().getTileY());
			final StateOfBattle newState = castAbilityOn(ability, targetPoint, stateOfBattle);
			newState.setAbilityUsed(ability);
			newState.setTarget(targetPoint);
			newState.setAi(ai);
			statesWithScores.put(newState.getScore(), newState);
		}
	}

	private StateOfBattle castAbilityOn(Ability ability, Point point, StateOfBattle stateOfBattle) {
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

	private Array<Point> findTargets(Point center, Ability ability, StateOfBattle stateOfBattle) {
		final AffectedTeams affectedTeams = ability.getAffectedTeams();
		final AreaOfEffect area = ability.getAreaOfEffect();
		final LineOfSight lineOfSight = ability.getLineOfSight();
		final int range = ability.getSpellData().getRange();

		final Array<Point> targets = new Array<>();
		final Array<Point> possibleCenterCells = getPossibleCenterCells(center, lineOfSight, range, stateOfBattle.getWidth(), stateOfBattle.getHeight());
		for (final Point point : possibleCenterCells) {
			targets.addAll(collectTargets(point, area, affectedTeams, stateOfBattle, ability.getSpellData().getAreaOfEffectRange(), stateOfBattle.getWidth(), stateOfBattle.getHeight()));
		}
		return targets;
	}

	private Array<Point> getPossibleCenterCells(Point center, LineOfSight lineOfSight, int range, int maxWidth, int maxheight) {
		switch (lineOfSight) {
		case LINE:
			return getLines(center, range, maxWidth, maxheight);
		case CIRCLE:
			return getCircleCells(center, range, maxWidth, maxheight);
		case CROSS:
			return getLines(center, range, maxWidth, maxheight);
		case SQUARE:
			return getSquareCells(center, range, maxWidth, maxheight);
		default:
			return null;
		}
	}

	private Array<Point> getLines(Point center, int range, int maxWidth, int maxheight) {
		final Array<Point> cells = new Array<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = 1; i <= range; i++) {
			addWithBoundariesField(cells, new Point(centerX, centerY + i), maxWidth, maxheight);
			addWithBoundariesField(cells, new Point(centerX, centerY - i), maxWidth, maxheight);
			addWithBoundariesField(cells, new Point(centerX + i, centerY), maxWidth, maxheight);
			addWithBoundariesField(cells, new Point(centerX - i, centerY), maxWidth, maxheight);
		}

		return cells;
	}

	private Array<Point> getHorizontalLineLeft(Point center, int range, int maxWidth, int maxheight) {
		final Array<Point> cells = new Array<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = 1; i <= range; i++) {
			addWithBoundariesField(cells, new Point(centerX - i, centerY), maxWidth, maxheight);
		}

		return cells;
	}

	private Array<Point> getHorizontalLineRight(Point center, int range, int maxWidth, int maxheight) {
		final Array<Point> cells = new Array<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = 1; i <= range; i++) {
			addWithBoundariesField(cells, new Point(centerX + i, centerY), maxWidth, maxheight);
		}

		return cells;
	}

	private Array<Point> getVerticalLineDown(Point center, int range, int maxWidth, int maxheight) {
		final Array<Point> cells = new Array<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = 1; i <= range; i++) {
			addWithBoundariesField(cells, new Point(centerX, centerY - i), maxWidth, maxheight);
		}

		return cells;
	}

	private Array<Point> getVerticalLineUp(Point center, int range, int maxWidth, int maxheight) {
		final Array<Point> cells = new Array<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = 1; i <= range; i++) {
			addWithBoundariesField(cells, new Point(centerX, centerY + i), maxWidth, maxheight);
		}

		return cells;
	}

	// makes doubles
	private Array<Point> getCircleCells(Point center, int range, int maxWidth, int maxheight) {
		final Array<Point> cells = new Array<>();
		final int centerX = center.x;
		final int centerY = center.y;
		final int distance = 0;

		while (distance <= range) {
			for (int i = 0; i <= range; i++) {
				for (int j = 0; j <= range; j++) {
					if ((i + j) == distance) {
						addWithBoundariesField(cells, new Point(centerX + i, centerY + j), maxWidth, maxheight);
						addWithBoundariesField(cells, new Point(centerX - i, centerY + j), maxWidth, maxheight);
						addWithBoundariesField(cells, new Point(centerX + i, centerY - j), maxWidth, maxheight);
						addWithBoundariesField(cells, new Point(centerX - i, centerY - j), maxWidth, maxheight);
					}
				}
			}
		}

		return cells;
	}

	private Array<Point> getSquareCells(Point center, int range, int maxWidth, int maxheight) {
		final Array<Point> cells = new Array<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = centerX - range; i <= (centerX + range); i++) {
			for (int j = centerY - range; j <= (centerY + range); j++) {
				addWithBoundariesField(cells, new Point(i, j), maxWidth, maxheight);
			}
		}

		return cells;
	}

	private void addWithBoundariesField(Array<Point> cells, Point point, int maxWidth, int maxheight) {
		if ((point.x >= 0) && (point.x < maxWidth) && (point.y >= 0) && (point.y < maxheight)) {
			cells.add(point);
		}
	}

	private Array<Point> collectTargets(Point center, AreaOfEffect area, AffectedTeams affectedTeams, StateOfBattle stateOfBattle, int areaOfEffectRange, int maxWidth, int maxheight) {
		final Array<Point> spotsToCheck = new Array<>();
		switch (area) {
		case CELL:
			spotsToCheck.add(center);
			return getSpotsWithUnitsOn(spotsToCheck, affectedTeams, stateOfBattle);
		case HORIZONTAL_LINE_LEFT:
			spotsToCheck.add(center);
			return getSpotsWithUnitsOn(getHorizontalLineLeft(center, areaOfEffectRange, maxWidth, maxheight), affectedTeams, stateOfBattle);
		case VERTICAL_LINE_UP:
			spotsToCheck.add(center);
			return getSpotsWithUnitsOn(getVerticalLineUp(center, areaOfEffectRange, maxWidth, maxheight), affectedTeams, stateOfBattle);
		case HORIZONTAL_LINE_RIGHT:
			spotsToCheck.add(center);
			return getSpotsWithUnitsOn(getHorizontalLineRight(center, areaOfEffectRange, maxWidth, maxheight), affectedTeams, stateOfBattle);
		case VERTICAL_LINE_DOWN:
			spotsToCheck.add(center);
			return getSpotsWithUnitsOn(getVerticalLineDown(center, areaOfEffectRange, maxWidth, maxheight), affectedTeams, stateOfBattle);
		case CIRCLE:
			spotsToCheck.add(center);
			return getSpotsWithUnitsOn(getCircleCells(center, areaOfEffectRange, maxWidth, maxheight), affectedTeams, stateOfBattle);
		case CROSS:
			return getSpotsWithUnitsOn(getLines(center, areaOfEffectRange, maxWidth, maxheight), affectedTeams, stateOfBattle);
		case SQUARE:
			spotsToCheck.add(center);
			return getSpotsWithUnitsOn(getSquareCells(center, areaOfEffectRange, maxWidth, maxheight), affectedTeams, stateOfBattle);
		default:
			return spotsToCheck;
		}
	}

	private Array<Point> getSpotsWithUnitsOn(Array<Point> spotsToCheck, AffectedTeams affectedTeams, StateOfBattle stateOfBattle) {
		final Array<Point> units = new Array<>();
		switch (affectedTeams) {
		case FRIENDLY:
			for (final Point point : spotsToCheck) {
				if (checkIfAiOnSpot(point, stateOfBattle)) {
					units.add(point);
				}
			}
			break;
		case ENEMY:
			for (final Point point : spotsToCheck) {
				if (checkIfPlayerOnSpot(point, stateOfBattle)) {
					units.add(point);
				}
			}
			break;
		case BOTH:
			for (final Point point : spotsToCheck) {
				if (checkIfBothOnSpot(point, stateOfBattle)) {
					units.add(point);
				}
			}
			break;
		}
		return units;
	}

	private boolean checkIfAiOnSpot(Point point, StateOfBattle stateOfBattle) {
		return stateOfBattle.get(point.x, point.y) > 0;
	}

	private boolean checkIfPlayerOnSpot(Point point, StateOfBattle stateOfBattle) {
		return stateOfBattle.get(point.x, point.y) < 0;
	}

	private boolean checkIfBothOnSpot(Point point, StateOfBattle stateOfBattle) {
		return stateOfBattle.get(point.x, point.y) != 0;
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

}
