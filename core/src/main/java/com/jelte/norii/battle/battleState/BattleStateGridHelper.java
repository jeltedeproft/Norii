package com.jelte.norii.battle.battleState;

import java.util.HashSet;
import java.util.Set;

import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.AffectedTeams;
import com.jelte.norii.magic.Ability.AreaOfEffect;
import com.jelte.norii.magic.Ability.LineOfSight;
import com.jelte.norii.utility.MyPoint;

public class BattleStateGridHelper {
	public Set<MyPoint> findTargets(MyPoint center, Ability ability, BattleState battleState) {
		final AffectedTeams affectedTeams = ability.getAffectedTeams();
		final AreaOfEffect area = ability.getAreaOfEffect();
		final LineOfSight lineOfSight = ability.getLineOfSight();
		final int range = ability.getSpellData().getRange();

		final Set<MyPoint> possibleCenterCells = new HashSet<>();
		possibleCenterCells.addAll(getPossibleCenterCells(possibleCenterCells, center, lineOfSight, range));
		checkMyPointBounds(possibleCenterCells, battleState);

		final Set<MyPoint> targets = new HashSet<>();
		for (final MyPoint MyPoint : possibleCenterCells) {
			targets.addAll(collectTargets(MyPoint, area, affectedTeams, battleState, ability.getSpellData().getAreaOfEffectRange()));
		}
		return targets;
	}

	public Set<MyPoint> getPossibleCenterCells(Set<MyPoint> MyPoints, MyPoint center, LineOfSight lineOfSight, int range) {
		switch (lineOfSight) {
		case LINE:
			return addLines(MyPoints, center, range);
		case CIRCLE_BORDER:
			return addUnfilledCircleAroundCentre(MyPoints, center, range);
		case CIRCLE:
			return addFilledCircleAroundCentre(MyPoints, center, range);
		case CROSS:
			return addCrossAroundCentre(MyPoints, center, range);
		case SQUARE_BORDER:
			return addUnfilledSquareAroundCentre(MyPoints, center, range);
		case SQUARE:
			return addFilledSquareAroundCentre(MyPoints, center, range);
		case DIAGONAL_RIGHT:
			return addDiagonalTopRightAroundCentre(MyPoints, center, range);
		case DIAGONAL_LEFT:
			return addDiagonalTopLeftAroundCentre(MyPoints, center, range);
		default:
			return new HashSet<>();
		}
	}

	private Set<MyPoint> collectTargets(MyPoint center, AreaOfEffect area, AffectedTeams affectedTeams, BattleState stateOfBattle, int areaOfEffectRange) {
		final Set<MyPoint> spotsToCheck = new HashSet<>();
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
		checkMyPointBounds(spotsToCheck, stateOfBattle);
		return getSpotsWithUnitsOn(spotsToCheck, affectedTeams, stateOfBattle);
	}

	private Set<MyPoint> getSpotsWithUnitsOn(Set<MyPoint> spotsToCheck, AffectedTeams affectedTeams, BattleState stateOfBattle) {
		final Set<MyPoint> units = new HashSet<>();
		switch (affectedTeams) {
		case FRIENDLY:
			for (final MyPoint MyPoint : spotsToCheck) {
				if (hasCellAiUnit(MyPoint, stateOfBattle)) {
					units.add(MyPoint);
				}
			}
			break;
		case ENEMY:
			for (final MyPoint MyPoint : spotsToCheck) {
				if (hasCellPlayerUnit(MyPoint, stateOfBattle)) {
					units.add(MyPoint);
				}
			}
			break;
		case BOTH:
			for (final MyPoint MyPoint : spotsToCheck) {
				if (hasCellUnit(MyPoint, stateOfBattle)) {
					units.add(MyPoint);
				}
			}
			break;
		}
		return units;
	}

	private boolean hasCellAiUnit(MyPoint MyPoint, BattleState stateOfBattle) {
		BattleCell cell = stateOfBattle.get(MyPoint.x, MyPoint.y);
		if (cell.isOccupied()) {
			return !cell.getUnit().isPlayerUnit();
		}
		return false;
	}

	private boolean hasCellPlayerUnit(MyPoint MyPoint, BattleState stateOfBattle) {
		BattleCell cell = stateOfBattle.get(MyPoint.x, MyPoint.y);
		if (cell.isOccupied()) {
			return cell.getUnit().isPlayerUnit();
		}
		return false;
	}

	private boolean hasCellUnit(MyPoint MyPoint, BattleState stateOfBattle) {
		return stateOfBattle.get(MyPoint.x, MyPoint.y).isOccupied();
	}

	private boolean isCellEmpty(MyPoint MyPoint, BattleState stateOfBattle) {
		return !stateOfBattle.get(MyPoint.x, MyPoint.y).isOccupied();
	}

	private Set<MyPoint> addLines(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		addLineUpwards(MyPoints, centre, range);
		addLineLeft(MyPoints, centre, range);
		addLineRight(MyPoints, centre, range);
		addLineDownwards(MyPoints, centre, range);
		return MyPoints;
	}

	private void addLineUpwards(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		for (int i = 1; i <= range; i++) {
			MyPoints.add(new MyPoint(centre.x, centre.y + i));
		}
	}

	private void addLineLeft(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		for (int i = 1; i <= range; i++) {
			MyPoints.add(new MyPoint(centre.x - i, centre.y));
		}
	}

	private void addLineRight(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		for (int i = 1; i <= range; i++) {
			MyPoints.add(new MyPoint(centre.x + i, centre.y));
		}
	}

	private void addLineDownwards(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		for (int i = 1; i <= range; i++) {
			MyPoints.add(new MyPoint(centre.x, centre.y - i));
		}
	}

	private Set<MyPoint> addUnfilledSquareAroundCentre(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		MyPoints.add(new MyPoint(centre.x + range, centre.y));
		MyPoints.add(new MyPoint(centre.x - range, centre.y));
		MyPoints.add(new MyPoint(centre.x, centre.y + range));
		MyPoints.add(new MyPoint(centre.x, centre.y - range));
		for (int i = 1; i <= range; i++) {
			MyPoints.add(new MyPoint(centre.x + range, centre.y - i));
			MyPoints.add(new MyPoint(centre.x - range, centre.y - i));
			MyPoints.add(new MyPoint(centre.x + range, centre.y + i));
			MyPoints.add(new MyPoint(centre.x - range, centre.y + i));

			MyPoints.add(new MyPoint(centre.x + i, centre.y - range));
			MyPoints.add(new MyPoint(centre.x - i, centre.y - range));
			MyPoints.add(new MyPoint(centre.x + i, centre.y + range));
			MyPoints.add(new MyPoint(centre.x - i, centre.y + range));
		}
		return MyPoints;
	}

	private Set<MyPoint> addFilledSquareAroundCentre(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		addUnfilledSquareAroundCentre(MyPoints, centre, range);
		for (int i = centre.x - range; i <= (centre.x + range); i++) {
			for (int j = centre.y - range; j <= (centre.y + range); j++) {
				MyPoints.add(new MyPoint(i, j));
			}
		}
		return MyPoints;
	}

	private Set<MyPoint> addUnfilledCircleAroundCentre(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 0; i <= range; i++) {
			for (int j = 0; j <= range; j++) {
				if ((i + j) == range) {
					MyPoints.add(new MyPoint(centerX + i, centerY + j));
					MyPoints.add(new MyPoint(centerX - i, centerY + j));
					MyPoints.add(new MyPoint(centerX + i, centerY - j));
					MyPoints.add(new MyPoint(centerX - i, centerY - j));
				}
			}
		}
		return MyPoints;
	}

	private Set<MyPoint> addFilledCircleAroundCentre(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		for (int i = 1; i <= range; i++) {
			MyPoints.addAll(addUnfilledCircleAroundCentre(MyPoints, centre, range));
		}
		return MyPoints;
	}

	private Set<MyPoint> addCrossAroundCentre(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			MyPoints.add(new MyPoint(centerX + i, centerY + i));
			MyPoints.add(new MyPoint(centerX - i, centerY + i));
			MyPoints.add(new MyPoint(centerX + i, centerY - i));
			MyPoints.add(new MyPoint(centerX - i, centerY - i));
		}

		return MyPoints;
	}

	private Set<MyPoint> addDiagonalTopRightAroundCentre(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			MyPoints.add(new MyPoint(centerX + i, centerY + i));
			MyPoints.add(new MyPoint(centerX - i, centerY - i));
		}

		return MyPoints;
	}

	private Set<MyPoint> addDiagonalTopLeftAroundCentre(Set<MyPoint> MyPoints, MyPoint centre, int range) {
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			MyPoints.add(new MyPoint(centerX - i, centerY + i));
			MyPoints.add(new MyPoint(centerX + i, centerY - i));
		}

		return MyPoints;
	}

	private void checkMyPointBounds(Set<MyPoint> MyPoints, BattleState stateOfBattle) {
		final int maxWidth = stateOfBattle.getWidth() - 1;
		final int maxHeight = stateOfBattle.getHeight() - 1;
		MyPoints.removeIf(MyPoint -> (MyPoint.x > maxWidth) || (MyPoint.y > maxHeight) || (MyPoint.x < 0) || (MyPoint.y < 0));
	}

}
