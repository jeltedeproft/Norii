package com.jelte.norii.battleState;

import java.awt.Point;
import java.util.HashSet;

import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.AffectedTeams;
import com.jelte.norii.magic.Ability.AreaOfEffect;
import com.jelte.norii.magic.Ability.LineOfSight;

public class BattleStateGridHelper {
	BattleState battleState;

	public HashSet<Point> findTargets(Point center, Ability ability, BattleState battleState) {
		this.battleState = battleState;
		final AffectedTeams affectedTeams = ability.getAffectedTeams();
		final AreaOfEffect area = ability.getAreaOfEffect();
		final LineOfSight lineOfSight = ability.getLineOfSight();
		final int range = ability.getSpellData().getRange();

		final HashSet<Point> targets = new HashSet<>();
		final HashSet<Point> possibleCenterCells = getPossibleCenterCells(center, lineOfSight, range, battleState.getWidth(), battleState.getHeight());
		for (final Point point : possibleCenterCells) {
			targets.addAll(collectTargets(point, area, affectedTeams, battleState, ability.getSpellData().getAreaOfEffectRange(), battleState.getWidth(), battleState.getHeight()));
		}
		return checkpointBounds(targets, battleState);
	}

	public HashSet<Point> getPossibleCenterCells(Point center, LineOfSight lineOfSight, int range, int maxWidth, int maxheight) {
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

	private HashSet<Point> getLines(Point center, int range, int maxWidth, int maxheight) {
		final HashSet<Point> cells = new HashSet<>();
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

	private HashSet<Point> getHorizontalLineLeft(Point center, int range, int maxWidth, int maxheight) {
		final HashSet<Point> cells = new HashSet<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = 1; i <= range; i++) {
			addWithBoundariesField(cells, new Point(centerX - i, centerY), maxWidth, maxheight);
		}

		return cells;
	}

	private HashSet<Point> getHorizontalLineRight(Point center, int range, int maxWidth, int maxheight) {
		final HashSet<Point> cells = new HashSet<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = 1; i <= range; i++) {
			addWithBoundariesField(cells, new Point(centerX + i, centerY), maxWidth, maxheight);
		}

		return cells;
	}

	private HashSet<Point> getVerticalLineDown(Point center, int range, int maxWidth, int maxheight) {
		final HashSet<Point> cells = new HashSet<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = 1; i <= range; i++) {
			addWithBoundariesField(cells, new Point(centerX, centerY - i), maxWidth, maxheight);
		}

		return cells;
	}

	private HashSet<Point> getVerticalLineUp(Point center, int range, int maxWidth, int maxheight) {
		final HashSet<Point> cells = new HashSet<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = 1; i <= range; i++) {
			addWithBoundariesField(cells, new Point(centerX, centerY + i), maxWidth, maxheight);
		}

		return cells;
	}

	// makes doubles
	private HashSet<Point> getCircleCells(Point center, int range, int maxWidth, int maxheight) {
		final HashSet<Point> cells = new HashSet<>();
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

	private HashSet<Point> getSquareCells(Point center, int range, int maxWidth, int maxheight) {
		final HashSet<Point> cells = new HashSet<>();
		final int centerX = center.x;
		final int centerY = center.y;

		for (int i = centerX - range; i <= (centerX + range); i++) {
			for (int j = centerY - range; j <= (centerY + range); j++) {
				addWithBoundariesField(cells, new Point(i, j), maxWidth, maxheight);
			}
		}

		return cells;
	}

	private void addWithBoundariesField(HashSet<Point> cells, Point point, int maxWidth, int maxheight) {
		if ((point.x >= 0) && (point.x < maxWidth) && (point.y >= 0) && (point.y < maxheight)) {
			cells.add(point);
		}
	}

	private HashSet<Point> collectTargets(Point center, AreaOfEffect area, AffectedTeams affectedTeams, BattleState stateOfBattle, int areaOfEffectRange, int maxWidth, int maxheight) {
		final HashSet<Point> spotsToCheck = new HashSet<>();
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

	private HashSet<Point> getSpotsWithUnitsOn(HashSet<Point> spotsToCheck, AffectedTeams affectedTeams, BattleState stateOfBattle) {
		final HashSet<Point> units = new HashSet<>();
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

	private boolean checkIfAiOnSpot(Point point, BattleState stateOfBattle) {
		return stateOfBattle.get(point.x, point.y) > 0;
	}

	private boolean checkIfPlayerOnSpot(Point point, BattleState stateOfBattle) {
		return stateOfBattle.get(point.x, point.y) < 0;
	}

	private boolean checkIfBothOnSpot(Point point, BattleState stateOfBattle) {
		return stateOfBattle.get(point.x, point.y) != 0;
	}

	// FOUNDATION
	private boolean isCellEmpty(Point point) {
		return battleState.get(point.x, point.y) == 0;
	}

	private boolean hasCellUnit(Point point) {
		return battleState.get(point.x, point.y) != 0;
	}

	private boolean hasCellPlayerUnit(Point point) {
		return battleState.get(point.x, point.y) < 0;
	}

	private boolean hasCellAiUnit(Point point) {
		return battleState.get(point.x, point.y) > 0;
	}

	private HashSet<Point> getLineUpwards(Point centre, int range) {
		final HashSet<Point> points = new HashSet<>();
		for (int i = 1; i <= range; i++) {
			points.add(new Point(centre.x, centre.y + i));
		}
		return points;
	}

	private HashSet<Point> getLineLeft(Point centre, int range) {
		final HashSet<Point> points = new HashSet<>();
		for (int i = 1; i <= range; i++) {
			points.add(new Point(centre.x - i, centre.y));
		}
		return points;
	}

	private HashSet<Point> getLineRight(Point centre, int range) {
		final HashSet<Point> points = new HashSet<>();
		for (int i = 1; i <= range; i++) {
			points.add(new Point(centre.x + i, centre.y));
		}
		return points;
	}

	private HashSet<Point> getLineDownwards(Point centre, int range) {
		final HashSet<Point> points = new HashSet<>();
		for (int i = 1; i <= range; i++) {
			points.add(new Point(centre.x, centre.y - i));
		}
		return points;
	}

	private HashSet<Point> getUnfilledSquareAroundCentre(Point centre, int range) {
		final HashSet<Point> points = new HashSet<>();
		points.add(new Point(centre.x + range, centre.y));
		points.add(new Point(centre.x - range, centre.y));
		points.add(new Point(centre.x, centre.y + range));
		points.add(new Point(centre.x, centre.y - range));
		for (int i = 1; i <= range; i++) {
			points.add(new Point(centre.x + range, centre.y - i));
			points.add(new Point(centre.x - range, centre.y - i));
			points.add(new Point(centre.x + range, centre.y + i));
			points.add(new Point(centre.x - range, centre.y + i));

			points.add(new Point(centre.x + i, centre.y - range));
			points.add(new Point(centre.x - i, centre.y - range));
			points.add(new Point(centre.x + i, centre.y + range));
			points.add(new Point(centre.x - i, centre.y + range));
		}
		return points;
	}

	private HashSet<Point> getfilledSquareAroundCentre(Point centre, int range) {
		final HashSet<Point> points = getUnfilledSquareAroundCentre(centre, range);
		for (int i = centre.x - range; i <= (centre.x + range); i++) {
			for (int j = centre.y - range; j <= (centre.y + range); j++) {
				points.add(new Point(i, j));
			}
		}
		return points;
	}

	private HashSet<Point> getUnfilledCircleAroundCentre(Point centre, int range) {
		final HashSet<Point> points = new HashSet<>();
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 0; i <= range; i++) {
			for (int j = 0; j <= range; j++) {
				if ((i + j) == range) {
					points.add(new Point(centerX + i, centerY + j));
					points.add(new Point(centerX - i, centerY + j));
					points.add(new Point(centerX + i, centerY - j));
					points.add(new Point(centerX - i, centerY - j));
				}
			}
		}
		return points;
	}

	private HashSet<Point> getFilledCircleAroundCentre(Point centre, int range) {
		final HashSet<Point> points = new HashSet<>();
		for (int i = 1; i <= range; i++) {
			points.addAll(getUnfilledCircleAroundCentre(centre, range));
		}
		return points;
	}

	private HashSet<Point> getCrossAroundCentre(Point centre, int range) {
		final HashSet<Point> points = new HashSet<>();
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			points.add(new Point(centerX + i, centerY + i));
			points.add(new Point(centerX - i, centerY + i));
			points.add(new Point(centerX + i, centerY - i));
			points.add(new Point(centerX - i, centerY - i));
		}

		return points;
	}

	private HashSet<Point> getDiagonalTopRightAroundCentre(Point centre, int range) {
		final HashSet<Point> points = new HashSet<>();
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			points.add(new Point(centerX + i, centerY + i));
			points.add(new Point(centerX - i, centerY - i));
		}

		return points;
	}

	private HashSet<Point> getDiagonalTopLeftAroundCentre(Point centre, int range) {
		final HashSet<Point> points = new HashSet<>();
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			points.add(new Point(centerX - i, centerY + i));
			points.add(new Point(centerX + i, centerY - i));
		}

		return points;
	}

	private HashSet<Point> checkpointBounds(HashSet<Point> points, BattleState stateOfBattle) {
		int maxWidth = stateOfBattle.getWidth();
		int maxHeight = stateOfBattle.getHeight();
		for (Point point : points) {
			if ((point.x > maxWidth) || (point.y > maxHeight) || (point.x < 0) || (point.y < 0)) {
				points.remove(point);
			}
		}
		return points;
	}

}
