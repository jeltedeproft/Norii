package com.jelte.norii.battleState;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.AffectedTeams;
import com.jelte.norii.magic.Ability.AreaOfEffect;
import com.jelte.norii.magic.Ability.LineOfSight;

public class BattleStateGridHelper {
	public Set<Point> findTargets(Point center, Ability ability, BattleState battleState) {
		final AffectedTeams affectedTeams = ability.getAffectedTeams();
		final AreaOfEffect area = ability.getAreaOfEffect();
		final LineOfSight lineOfSight = ability.getLineOfSight();
		final int range = ability.getSpellData().getRange();

		final Set<Point> possibleCenterCells = new HashSet<>();
		possibleCenterCells.addAll(getPossibleCenterCells(possibleCenterCells, center, lineOfSight, range));
		checkpointBounds(possibleCenterCells, battleState);

		final Set<Point> targets = new HashSet<>();
		for (final Point point : possibleCenterCells) {
			targets.addAll(collectTargets(point, area, affectedTeams, battleState, ability.getSpellData().getAreaOfEffectRange()));
		}
		return targets;
	}

	public Set<Point> getPossibleCenterCells(Set<Point> points, Point center, LineOfSight lineOfSight, int range) {
		switch (lineOfSight) {
		case LINE:
			return addLines(points, center, range);
		case CIRCLE_BORDER:
			return addUnfilledCircleAroundCentre(points, center, range);
		case CIRCLE:
			return addFilledCircleAroundCentre(points, center, range);
		case CROSS:
			return addCrossAroundCentre(points, center, range);
		case SQUARE_BORDER:
			return addUnfilledSquareAroundCentre(points, center, range);
		case SQUARE:
			return addFilledSquareAroundCentre(points, center, range);
		case DIAGONAL_RIGHT:
			return addDiagonalTopRightAroundCentre(points, center, range);
		case DIAGONAL_LEFT:
			return addDiagonalTopLeftAroundCentre(points, center, range);
		default:
			return new HashSet<>();
		}
	}

	private Set<Point> collectTargets(Point center, AreaOfEffect area, AffectedTeams affectedTeams, BattleState stateOfBattle, int areaOfEffectRange) {
		final Set<Point> spotsToCheck = new HashSet<>();
		switch (area) {
		case CELL:
			spotsToCheck.add(center);
			break;
		case HORIZONTAL_LINE_LEFT:
			spotsToCheck.add(center);
			addLineLeft(spotsToCheck, center, areaOfEffectRange);
			break;
		case VERTICAL_LINE_UP:
			spotsToCheck.add(center);
			addLineUpwards(spotsToCheck, center, areaOfEffectRange);
			break;
		case HORIZONTAL_LINE_RIGHT:
			spotsToCheck.add(center);
			addLineRight(spotsToCheck, center, areaOfEffectRange);
			break;
		case VERTICAL_LINE_DOWN:
			spotsToCheck.add(center);
			addLineDownwards(spotsToCheck, center, areaOfEffectRange);
			break;
		case CIRCLE:
			spotsToCheck.add(center);
			addFilledCircleAroundCentre(spotsToCheck, center, areaOfEffectRange);
			break;
		case CROSS:
			addCrossAroundCentre(spotsToCheck, center, areaOfEffectRange);
			break;
		case SQUARE:
			spotsToCheck.add(center);
			addFilledSquareAroundCentre(spotsToCheck, center, areaOfEffectRange);
			break;
		case DIAGONAL_RIGHT:
			spotsToCheck.add(center);
			addDiagonalTopRightAroundCentre(spotsToCheck, center, areaOfEffectRange);
			break;
		case DIAGONAL_LEFT:
			spotsToCheck.add(center);
			addDiagonalTopLeftAroundCentre(spotsToCheck, center, areaOfEffectRange);
			break;
		case SQUARE_BORDER:
			spotsToCheck.add(center);
			addUnfilledSquareAroundCentre(spotsToCheck, center, areaOfEffectRange);
			break;
		case CIRCLE_BORDER:
			spotsToCheck.add(center);
			addUnfilledCircleAroundCentre(spotsToCheck, center, areaOfEffectRange);
			break;
		default:
			return spotsToCheck;
		}
		checkpointBounds(spotsToCheck, stateOfBattle);
		return getSpotsWithUnitsOn(spotsToCheck, affectedTeams, stateOfBattle);
	}

	private Set<Point> getSpotsWithUnitsOn(Set<Point> spotsToCheck, AffectedTeams affectedTeams, BattleState stateOfBattle) {
		final Set<Point> units = new HashSet<>();
		switch (affectedTeams) {
		case FRIENDLY:
			for (final Point point : spotsToCheck) {
				if (hasCellAiUnit(point, stateOfBattle)) {
					units.add(point);
				}
			}
			break;
		case ENEMY:
			for (final Point point : spotsToCheck) {
				if (hasCellPlayerUnit(point, stateOfBattle)) {
					units.add(point);
				}
			}
			break;
		case BOTH:
			for (final Point point : spotsToCheck) {
				if (hasCellUnit(point, stateOfBattle)) {
					units.add(point);
				}
			}
			break;
		}
		return units;
	}

	private boolean hasCellAiUnit(Point point, BattleState stateOfBattle) {
		return stateOfBattle.get(point.x, point.y) > 0;
	}

	private boolean hasCellPlayerUnit(Point point, BattleState stateOfBattle) {
		return stateOfBattle.get(point.x, point.y) < 0;
	}

	private boolean hasCellUnit(Point point, BattleState stateOfBattle) {
		return stateOfBattle.get(point.x, point.y) != 0;
	}

	private boolean isCellEmpty(Point point, BattleState stateOfBattle) {
		return stateOfBattle.get(point.x, point.y) == 0;
	}

	private Set<Point> addLines(Set<Point> points, Point centre, int range) {
		addLineUpwards(points, centre, range);
		addLineLeft(points, centre, range);
		addLineRight(points, centre, range);
		addLineDownwards(points, centre, range);
		return points;
	}

	private void addLineUpwards(Set<Point> points, Point centre, int range) {
		for (int i = 1; i <= range; i++) {
			points.add(new Point(centre.x, centre.y + i));
		}
	}

	private void addLineLeft(Set<Point> points, Point centre, int range) {
		for (int i = 1; i <= range; i++) {
			points.add(new Point(centre.x - i, centre.y));
		}
	}

	private void addLineRight(Set<Point> points, Point centre, int range) {
		for (int i = 1; i <= range; i++) {
			points.add(new Point(centre.x + i, centre.y));
		}
	}

	private void addLineDownwards(Set<Point> points, Point centre, int range) {
		for (int i = 1; i <= range; i++) {
			points.add(new Point(centre.x, centre.y - i));
		}
	}

	private Set<Point> addUnfilledSquareAroundCentre(Set<Point> points, Point centre, int range) {
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

	private Set<Point> addFilledSquareAroundCentre(Set<Point> points, Point centre, int range) {
		addUnfilledSquareAroundCentre(points, centre, range);
		for (int i = centre.x - range; i <= (centre.x + range); i++) {
			for (int j = centre.y - range; j <= (centre.y + range); j++) {
				points.add(new Point(i, j));
			}
		}
		return points;
	}

	private Set<Point> addUnfilledCircleAroundCentre(Set<Point> points, Point centre, int range) {
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

	private Set<Point> addFilledCircleAroundCentre(Set<Point> points, Point centre, int range) {
		for (int i = 1; i <= range; i++) {
			points.addAll(addUnfilledCircleAroundCentre(points, centre, range));
		}
		return points;
	}

	private Set<Point> addCrossAroundCentre(Set<Point> points, Point centre, int range) {
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

	private Set<Point> addDiagonalTopRightAroundCentre(Set<Point> points, Point centre, int range) {
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			points.add(new Point(centerX + i, centerY + i));
			points.add(new Point(centerX - i, centerY - i));
		}

		return points;
	}

	private Set<Point> addDiagonalTopLeftAroundCentre(Set<Point> points, Point centre, int range) {
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			points.add(new Point(centerX - i, centerY + i));
			points.add(new Point(centerX + i, centerY - i));
		}

		return points;
	}

	private void checkpointBounds(Set<Point> points, BattleState stateOfBattle) {
		final int maxWidth = stateOfBattle.getWidth();
		final int maxHeight = stateOfBattle.getHeight();
		for (final Point point : points) {
			if ((point.x > maxWidth) || (point.y > maxHeight) || (point.x < 0) || (point.y < 0)) {
				points.remove(point);
			}
		}
	}

}
