package com.jelte.norii.battle.battleState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.AIDecisionMaker;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.AffectedTeams;
import com.jelte.norii.magic.Ability.AreaOfEffect;
import com.jelte.norii.magic.Ability.LineOfSight;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;

public class BattleStateGridHelper {
	private static BattleStateGridHelper instance;
	private static final String TAG = BattleStateGridHelper.class.getSimpleName();

	public Set<MyPoint> findTargets(MyPoint caster, MyPoint center, Ability ability, BattleState battleState) {
		final AffectedTeams affectedTeams = ability.getAffectedTeams();
		final AreaOfEffect area = ability.getAreaOfEffect();
		final LineOfSight lineOfSight = ability.getLineOfSight();
		final int range = ability.getSpellData().getRange();

		final Set<MyPoint> possibleCenterCells = getPossibleCenterCells(center, lineOfSight, range);

		final Set<MyPoint> targets = new HashSet<>();
		for (final MyPoint MyPoint : possibleCenterCells) {
			targets.addAll(collectTargets(caster, MyPoint, area, affectedTeams, battleState, ability.getSpellData().getAreaOfEffectRange()));
		}
		return targets;
	}

	public Set<MyPoint> getAllPointsASpellCanHit(MyPoint center, LineOfSight lineOfSight, int range, BattleState battleState) {
		final Set<MyPoint> points = getPossibleCenterCells(center, lineOfSight, range);
		checkMyPointBounds(points, battleState);
		return points;
	}

	public Set<MyPoint> getPossibleCenterCellsFiltered(MyPoint center, LineOfSight lineOfSight, int range, BattleState battleState) {
		final Set<MyPoint> points = getPossibleCenterCells(center, lineOfSight, range);
		return filter(points, battleState.getWidth(), battleState.getHeight());
	}

	public Set<MyPoint> getPossibleCenterCells(MyPoint center, LineOfSight lineOfSight, int range) {
		final Set<MyPoint> points = new HashSet<>();
		switch (lineOfSight) {
		case LINE:
			return addLines(points, center, range);
		case CIRCLE_BORDER:
			return addUnfilledCircleAroundCentre(points, center, range);
		case CIRCLE:
			return addFilledCircleAroundCentre(points, center, range);
		case CROSS:
			return addDiagonalCrossAroundCentre(points, center, range);
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

	public Set<MyPoint> collectTargets(MyPoint caster, MyPoint center, AreaOfEffect area, AffectedTeams affectedTeams, BattleState stateOfBattle, int areaOfEffectRange) {
		final Set<MyPoint> spotsToCheck = getSpotsAreaOfEffect(caster, center, area, areaOfEffectRange);

		if (!spotsToCheck.isEmpty()) {
			checkMyPointBounds(spotsToCheck, stateOfBattle);
			return getSpotsWithUnitsOn(spotsToCheck, affectedTeams, stateOfBattle);
		} else {
			return spotsToCheck;
		}
	}

	public Set<MyPoint> getAllPointsASpellCanHit(MyPoint caster, MyPoint center, AreaOfEffect areaOfEffect, int range, BattleState battleState) {
		final Set<MyPoint> points = getSpotsAreaOfEffect(caster, center, areaOfEffect, range);
		checkMyPointBounds(points, battleState);
		return points;
	}

	private Set<MyPoint> getSpotsAreaOfEffect(MyPoint caster, MyPoint center, AreaOfEffect area, int areaOfEffectRange) {
		final Set<MyPoint> spotsToCheck = new HashSet<>();
		switch (area) {
		case CELL:
			spotsToCheck.add(center);
			break;
		case STRAIGHT_LINE:
			spotsToCheck.add(center);
			spotsToCheck.addAll(AIDecisionMaker.findLine(center.x, center.y, caster.x, caster.y));
			break;
		case HORIZONTAL_LINE:
			spotsToCheck.add(center);
			addLineLeft(spotsToCheck, center, areaOfEffectRange);
			addLineRight(spotsToCheck, center, areaOfEffectRange);
			break;
		case HORIZONTAL_LINE_LEFT:
			spotsToCheck.add(center);
			addLineLeft(spotsToCheck, center, areaOfEffectRange);
			break;
		case VERTICAL_LINE:
			spotsToCheck.add(center);
			addLineUpwards(spotsToCheck, center, areaOfEffectRange);
			addLineDownwards(spotsToCheck, center, areaOfEffectRange);
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
		case DIAGONAL:
			addDiagonalCrossAroundCentre(spotsToCheck, center, areaOfEffectRange);
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
		return spotsToCheck;
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
		final BattleCell cell = stateOfBattle.get(MyPoint.x, MyPoint.y);
		if (cell.isOccupied()) {
			return !cell.getUnit().isPlayerUnit();
		}
		return false;
	}

	private boolean hasCellPlayerUnit(MyPoint MyPoint, BattleState stateOfBattle) {
		final BattleCell cell = stateOfBattle.get(MyPoint.x, MyPoint.y);
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

	private Set<MyPoint> addFilledCircleAroundCentre(Set<MyPoint> points, MyPoint centre, int range) {
		for (int i = 1; i <= range; i++) {
			points.addAll(addUnfilledCircleAroundCentre(points, centre, i));
		}
		return points;
	}

	private Set<MyPoint> addDiagonalCrossAroundCentre(Set<MyPoint> points, MyPoint centre, int range) {
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			points.add(new MyPoint(centerX + i, centerY + i));
			points.add(new MyPoint(centerX - i, centerY + i));
			points.add(new MyPoint(centerX + i, centerY - i));
			points.add(new MyPoint(centerX - i, centerY - i));
		}

		return points;
	}

	private Set<MyPoint> addCrossAroundCentre(Set<MyPoint> points, MyPoint centre, int range) {
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			points.add(new MyPoint(centerX + i, centerY));
			points.add(new MyPoint(centerX - i, centerY));
			points.add(new MyPoint(centerX, centerY + i));
			points.add(new MyPoint(centerX, centerY - i));
		}

		return points;
	}

	private Set<MyPoint> addDiagonalTopRightAroundCentre(Set<MyPoint> points, MyPoint centre, int range) {
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			points.add(new MyPoint(centerX + i, centerY + i));
			points.add(new MyPoint(centerX - i, centerY - i));
		}

		return points;
	}

	private Set<MyPoint> addDiagonalTopLeftAroundCentre(Set<MyPoint> points, MyPoint centre, int range) {
		final int centerX = centre.x;
		final int centerY = centre.y;

		for (int i = 1; i <= range; i++) {
			points.add(new MyPoint(centerX - i, centerY + i));
			points.add(new MyPoint(centerX + i, centerY - i));
		}

		return points;
	}

	private void checkMyPointBounds(Set<MyPoint> points, BattleState stateOfBattle) {
		final int maxWidth = stateOfBattle.getWidth() - 1;
		final int maxHeight = stateOfBattle.getHeight() - 1;
		points.removeIf(point -> (point.x > maxWidth) || (point.y > maxHeight) || (point.x < 0) || (point.y < 0));
	}

	public Array<MyPoint> getTargetPositionsInRangeAbility(MyPoint casterPos, Ability ability, Array<MyPoint> unitPositions) {
		final Array<MyPoint> results = new Array<>();
		for (final MyPoint targetPos : unitPositions) {
			if (isUnitInAbilityRange(casterPos, ability, targetPos)) {
				results.add(targetPos);
			}
		}
		return results;
	}

	public boolean isUnitInAbilityRange(MyPoint casterPos, Ability ability, MyPoint targetPos) {
		switch (ability.getLineOfSight()) {
		case LINE:
			return getLineOptions(casterPos, ability, targetPos);
		case CIRCLE:
			return getCircleOptions(casterPos, ability, targetPos);
		case CROSS:
			return getLineOptions(casterPos, ability, targetPos);
		case SQUARE:
			return getSquareOptions(casterPos, ability, targetPos);
		case DIAGONAL_RIGHT:
			return getDiagonalRightOptions(casterPos, ability, targetPos);
		case DIAGONAL_LEFT:
			return getDiagonalLeftOptions(casterPos, ability, targetPos);
		case SQUARE_BORDER:
			return getSquareBorderOptions(casterPos, ability, targetPos);
		case CIRCLE_BORDER:
			return getCircleBorderOptions(casterPos, ability, targetPos);
		default:
			return false;
		}
	}

	private boolean getLineOptions(MyPoint casterPos, Ability ability, MyPoint targetPos) {
		final int range = ability.getSpellData().getRange();
		final int areaOfEffectRange = ability.getSpellData().getAreaOfEffectRange();
		switch (ability.getAreaOfEffect()) {
		case CELL:
			return checkCrossNoCenter(casterPos, targetPos, range);
		case STRAIGHT_LINE:
			return checkCrossNoCenter(casterPos, targetPos, range);
		case HORIZONTAL_LINE:
			return checkLineHorizontalLine(casterPos, targetPos, range, areaOfEffectRange) || checkLineHorizontalLine(casterPos, targetPos, areaOfEffectRange, range);
		case VERTICAL_LINE:
			return checkCross(casterPos, targetPos, range + areaOfEffectRange);
		case CIRCLE:
			return checkLineCircle(casterPos, targetPos, range, areaOfEffectRange);
		case SQUARE:
			return checkLineSquare(casterPos, targetPos, range, areaOfEffectRange);
		case CROSS:
			return checkLineHorizontalLine(casterPos, targetPos, range, areaOfEffectRange) || checkLineHorizontalLine(casterPos, targetPos, areaOfEffectRange, range) || checkCross(casterPos, targetPos, range + areaOfEffectRange);
		case DIAGONAL:
			return checkLineDiagonal(casterPos, targetPos, range, areaOfEffectRange);
		case SQUARE_BORDER:
			return checkLineSquareBorder(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE_BORDER:
			return checkLineCircleBorder(casterPos, targetPos, range, areaOfEffectRange);
		default:
			return false;
		}
	}

	private boolean checkLineCircleBorder(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		return ((deltaX + deltaY) <= (range + areaOfEffectRange)) && !((deltaX == 0) && (deltaY == areaOfEffectRange));
	}

	private boolean checkLineSquareBorder(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > max)) {
			return false;
		}

		final int limit = (areaOfEffectRange * 2) + 1;
		return !((deltaX > limit) && (deltaY > limit));
	}

	private boolean checkLineDiagonal(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > max)) {
			return false;
		}

		return !(((deltaX == deltaY) && (deltaX >= range)) || (((deltaX == 0) && (deltaY > range)) || ((deltaY == 0) && (deltaX > range))) || checkLShapesDiagonal(deltaX, deltaY, range, areaOfEffectRange)
				|| checkCorners(deltaX, deltaY, areaOfEffectRange));
	}

	private boolean checkCorners(int deltaX, int deltaY, int areaOfEffectRange) {
		return ((deltaX > areaOfEffectRange) && (deltaY > areaOfEffectRange));
	}

	private boolean checkLShapesDiagonal(int deltaX, int deltaY, int range, int areaOfEffectRange) {
		final int max = range + areaOfEffectRange;
		while (areaOfEffectRange > 1) {
			for (int i = 1; i < areaOfEffectRange; i++) {
				if (((deltaX == max) && (deltaY == i)) || ((deltaY == max) && (deltaX == i))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkLineSquare(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		return (Math.abs(casterPos.y - targetPos.y) <= (range + areaOfEffectRange)) && (Math.abs(casterPos.x - targetPos.x) <= (range + areaOfEffectRange));
	}

	private boolean checkLineCircle(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		return ((Math.abs(casterPos.y - targetPos.y) + (Math.abs(casterPos.x - targetPos.x))) <= (range + areaOfEffectRange));
	}

	private boolean checkLineHorizontalLine(MyPoint casterPos, MyPoint targetPos, final int range, final int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = (areaOfEffectRange < range) ? range : areaOfEffectRange;
		final int min = (areaOfEffectRange < range) ? areaOfEffectRange : range;

		if ((deltaX == 0) || (deltaY == 0)) {
			return false;
		}

		if ((deltaX > max) || (deltaY > max) || ((deltaX > min) && (deltaY > min))) {
			return false;
		}
		return true;
	}

	private boolean checkCross(MyPoint casterPos, MyPoint targetPos, final int range) {
		return ((casterPos.x == targetPos.x) && (Math.abs(casterPos.y - targetPos.y) <= range)) || ((casterPos.y == targetPos.y) && (Math.abs(casterPos.x - targetPos.x) <= range));
	}

	private boolean checkCrossNoCenter(MyPoint casterPos, MyPoint targetPos, final int range) {
		if ((casterPos.x == targetPos.x) && (casterPos.y == targetPos.y)) {
			return false;
		}
		return ((casterPos.x == targetPos.x) && (Math.abs(casterPos.y - targetPos.y) <= range)) || ((casterPos.y == targetPos.y) && (Math.abs(casterPos.x - targetPos.x) <= range));
	}

	private boolean getCircleOptions(MyPoint casterPos, Ability ability, MyPoint targetPos) {
		final int range = ability.getSpellData().getRange();
		final int areaOfEffectRange = ability.getSpellData().getAreaOfEffectRange();
		switch (ability.getAreaOfEffect()) {
		case CELL:
			return checkCircle(casterPos, targetPos, range);
		case STRAIGHT_LINE:
			return checkCircle(casterPos, targetPos, range);
		case HORIZONTAL_LINE:
			return checkCircleHorizontalLine(casterPos, targetPos, range, areaOfEffectRange);
		case VERTICAL_LINE:
			return checkCircleVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE:
			return checkCircle(casterPos, targetPos, range + areaOfEffectRange);
		case SQUARE:
			return checkCircleSquare(casterPos, targetPos, range, areaOfEffectRange);
		case CROSS:
			return checkCircleHorizontalLine(casterPos, targetPos, range, areaOfEffectRange) || checkCircleVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case DIAGONAL:
			return checkLineDiagonal(casterPos, targetPos, range, areaOfEffectRange);
		case SQUARE_BORDER:
			return checkCircleSquare(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE_BORDER:
			return checkCircle(casterPos, targetPos, range + areaOfEffectRange);
		default:
			return false;
		}
	}

	private boolean checkCircleSquare(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > max)) {
			return false;
		}

		return (deltaX + deltaY) <= (range + (2 * areaOfEffectRange));
	}

	private boolean checkCircleVerticalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > range) || (Math.abs(casterPos.y - targetPos.y) > max) || ((deltaY == 0) && (deltaX == range))) {
			return false;
		}

		return (deltaX + deltaY) <= max;
	}

	private boolean checkCircleHorizontalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > range) || ((deltaX == 0) && (deltaY == range))) {
			return false;
		}

		return (deltaX + deltaY) <= max;
	}

	private boolean checkCircle(MyPoint casterPos, MyPoint targetPos, int range) {
		return ((Math.abs(casterPos.y - targetPos.y) + (Math.abs(casterPos.x - targetPos.x))) <= range);
	}

	private boolean getSquareOptions(MyPoint casterPos, Ability ability, MyPoint targetPos) {
		final int range = ability.getSpellData().getRange();
		final int areaOfEffectRange = ability.getSpellData().getAreaOfEffectRange();
		switch (ability.getAreaOfEffect()) {
		case CELL:
			return checkSquare(casterPos, targetPos, range);
		case STRAIGHT_LINE:
			return checkSquare(casterPos, targetPos, range);
		case HORIZONTAL_LINE:
			return checkSquareHorizontalLine(casterPos, targetPos, range, areaOfEffectRange);
		case VERTICAL_LINE:
			return checkSquareVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE:
			return checkCircleSquare(casterPos, targetPos, range, areaOfEffectRange);
		case SQUARE:
			return checkSquare(casterPos, targetPos, range + areaOfEffectRange);
		case CROSS:
			return checkSquareHorizontalLine(casterPos, targetPos, range, areaOfEffectRange) || checkSquareVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case DIAGONAL:
			return checkSquare(casterPos, targetPos, range + areaOfEffectRange);
		case SQUARE_BORDER:
			return checkSquare(casterPos, targetPos, range + areaOfEffectRange);
		case CIRCLE_BORDER:
			return checkCircleSquare(casterPos, targetPos, range, areaOfEffectRange);
		default:
			return false;
		}
	}

	private boolean checkSquareVerticalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);

		return (deltaY <= (range + areaOfEffectRange)) && (deltaX <= range);
	}

	private boolean checkSquareHorizontalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);

		return (deltaX <= (range + areaOfEffectRange)) && (deltaY <= range);
	}

	private boolean checkSquare(MyPoint casterPos, MyPoint targetPos, int range) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);

		return (deltaX <= range) && (deltaY <= range);
	}

	private boolean getDiagonalRightOptions(MyPoint casterPos, Ability ability, MyPoint targetPos) {
		final int range = ability.getSpellData().getRange();
		final int areaOfEffectRange = ability.getSpellData().getAreaOfEffectRange();
		switch (ability.getAreaOfEffect()) {
		case CELL:
			return checkDiagonalRight(casterPos, targetPos, range);
		case STRAIGHT_LINE:
			return checkDiagonalRight(casterPos, targetPos, range);
		case HORIZONTAL_LINE:
			return checkDiagonalRightHorizontalLine(casterPos, targetPos, range, areaOfEffectRange);
		case VERTICAL_LINE:
			return checkDiagonalRightVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE:
			return checkDiagonalRightCircle(casterPos, targetPos, range, areaOfEffectRange);
		case SQUARE:
			return checkDiagonalRightSquare(casterPos, targetPos, range, areaOfEffectRange);
		case CROSS:
			return checkDiagonalRightHorizontalLine(casterPos, targetPos, range, areaOfEffectRange) || checkDiagonalRightVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case DIAGONAL:
			return checkDiagonalDiagonal(casterPos, targetPos, range, areaOfEffectRange);
		case SQUARE_BORDER:
			return checkDiagonalRightSquare(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE_BORDER:
			return checkDiagonalRightCircle(casterPos, targetPos, range, areaOfEffectRange);
		default:
			return false;
		}
	}

	private boolean checkDiagonalDiagonal(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > max)) {
			return false;
		}

		if (deltaX == deltaY) {
			return true;
		}
		return ((Math.abs(deltaX - deltaY) == 4) || (Math.abs(deltaX - deltaY) == 2));
	}

	private boolean checkDiagonalRightSquare(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = casterPos.x - targetPos.x;
		final int deltaY = casterPos.y - targetPos.y;
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > range)) {
			return false;
		}

		return Math.abs(deltaX - deltaY) <= (2 * areaOfEffectRange);
	}

	private boolean checkDiagonalRightCircle(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = casterPos.x - targetPos.x;
		final int deltaY = casterPos.y - targetPos.y;
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > max)) {
			return false;
		}

		return (Math.abs(deltaX - deltaY) < areaOfEffectRange) && ((deltaX + deltaY) < (areaOfEffectRange + (2 * range)));
	}

	private boolean checkDiagonalRightHorizontalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = casterPos.x - targetPos.x;
		final int deltaY = casterPos.y - targetPos.y;
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > range)) {
			return false;
		}

		if (deltaX == deltaY) {
			return false;
		}

		return Math.abs(deltaX - deltaY) < areaOfEffectRange;
	}

	private boolean checkDiagonalRightVerticalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = casterPos.x - targetPos.x;
		final int deltaY = casterPos.y - targetPos.y;
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > range) || (Math.abs(casterPos.y - targetPos.y) > max)) {
			return false;
		}

		if (deltaX == deltaY) {
			return false;
		}

		return Math.abs(deltaY - deltaX) < areaOfEffectRange;
	}

	private boolean checkDiagonalRight(MyPoint casterPos, MyPoint targetPos, int range) {
		final int deltaX = casterPos.x - targetPos.x;
		final int deltaY = casterPos.y - targetPos.y;
		return ((deltaX == deltaY) && (deltaX <= range) && (deltaX != 0));
	}

	private boolean getDiagonalLeftOptions(MyPoint casterPos, Ability ability, MyPoint targetPos) {
		final int range = ability.getSpellData().getRange();
		final int areaOfEffectRange = ability.getSpellData().getAreaOfEffectRange();
		switch (ability.getAreaOfEffect()) {
		case CELL:
			return checkDiagonalLeft(casterPos, targetPos, range);
		case STRAIGHT_LINE:
			return checkDiagonalLeft(casterPos, targetPos, range);
		case HORIZONTAL_LINE:
			return checkDiagonalLeftHorizontalLine(casterPos, targetPos, range, areaOfEffectRange);
		case VERTICAL_LINE:
			return checkDiagonalLeftVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE:
			return checkDiagonalLeftCircle(casterPos, targetPos, range, areaOfEffectRange);
		case SQUARE:
			return checkDiagonalLeftSquare(casterPos, targetPos, range, areaOfEffectRange);
		case CROSS:
			return checkDiagonalLeftHorizontalLine(casterPos, targetPos, range, areaOfEffectRange) || checkDiagonalLeftVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case DIAGONAL:
			return checkDiagonalLeftDiagonal(casterPos, targetPos, range, areaOfEffectRange);
		case SQUARE_BORDER:
			return checkDiagonalLeftSquare(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE_BORDER:
			return checkDiagonalLeftCircle(casterPos, targetPos, range, areaOfEffectRange);
		default:
			return false;
		}
	}

	private boolean checkDiagonalLeft(MyPoint casterPos, MyPoint targetPos, int range) {
		final int deltaX = casterPos.x - targetPos.x;
		final int deltaY = casterPos.y - targetPos.y;
		return ((deltaX == (deltaY * -1)) && (deltaX <= range) && (deltaX != 0));
	}

	private boolean checkDiagonalLeftHorizontalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = casterPos.x - targetPos.x;
		final int deltaY = casterPos.y - targetPos.y;
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > range)) {
			return false;
		}

		if (deltaX == deltaY) {
			return false;
		}

		return Math.abs(deltaX + deltaY) < areaOfEffectRange;
	}

	private boolean checkDiagonalLeftVerticalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = casterPos.x - targetPos.x;
		final int deltaY = casterPos.y - targetPos.y;
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > range) || (Math.abs(casterPos.y - targetPos.y) > max)) {
			return false;
		}

		if (deltaX == deltaY) {
			return false;
		}

		return Math.abs(deltaY + deltaX) < areaOfEffectRange;
	}

	private boolean checkDiagonalLeftDiagonal(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > max)) {
			return false;
		}

		if (deltaX == (deltaY * -1)) {
			return true;
		}
		return ((Math.abs(deltaX - deltaY) == 4) || (Math.abs(deltaX - deltaY) == 2));
	}

	private boolean checkDiagonalLeftSquare(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = casterPos.x - targetPos.x;
		final int deltaY = casterPos.y - targetPos.y;
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > range)) {
			return false;
		}

		return Math.abs(deltaX + deltaY) <= (2 * areaOfEffectRange);
	}

	private boolean checkDiagonalLeftCircle(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = casterPos.x - targetPos.x;
		final int deltaY = casterPos.y - targetPos.y;
		final int max = range + areaOfEffectRange;
		if ((Math.abs(casterPos.x - targetPos.x) > max) || (Math.abs(casterPos.y - targetPos.y) > max)) {
			return false;
		}

		return (Math.abs(deltaX + deltaY) < areaOfEffectRange) && ((deltaX + deltaY) < (areaOfEffectRange + (2 * range)));
	}

	private boolean getSquareBorderOptions(MyPoint casterPos, Ability ability, MyPoint targetPos) {
		final int range = ability.getSpellData().getRange();
		final int areaOfEffectRange = ability.getSpellData().getAreaOfEffectRange();
		switch (ability.getAreaOfEffect()) {
		case CELL:
			return checkSquareBorder(casterPos, targetPos, range);
		case STRAIGHT_LINE:
			return checkSquareBorder(casterPos, targetPos, range);
		case HORIZONTAL_LINE:
			return checkSquareBorderHorizontalLine(casterPos, targetPos, range, areaOfEffectRange);
		case VERTICAL_LINE:
			return checkSquareBorderVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE:
			return checkCircleSquare(casterPos, targetPos, range, areaOfEffectRange);
		case SQUARE:
			return checkSquare(casterPos, targetPos, range + areaOfEffectRange);
		case CROSS:
			return checkSquareBorderHorizontalLine(casterPos, targetPos, range, areaOfEffectRange) || checkSquareBorderVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case DIAGONAL:
			return checkSquare(casterPos, targetPos, range + areaOfEffectRange);
		case SQUARE_BORDER:
			return checkSquare(casterPos, targetPos, range + areaOfEffectRange);
		case CIRCLE_BORDER:
			return checkCircleSquare(casterPos, targetPos, range, areaOfEffectRange);
		default:
			return false;
		}
	}

	private boolean checkSquareBorderVerticalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((deltaX > range) || (deltaY > max)) {
			return false;
		}

		return ((deltaX == range) || (targetPos.y >= ((casterPos.y + range) - areaOfEffectRange)) || (targetPos.y <= (casterPos.y + range + areaOfEffectRange)));
	}

	private boolean checkSquareBorderHorizontalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((deltaX > max) || (deltaY > range)) {
			return false;
		}

		return ((deltaY == range) || (targetPos.x >= ((casterPos.x + range) - areaOfEffectRange)) || (targetPos.x <= (casterPos.x + range + areaOfEffectRange)));
	}

	private boolean checkSquareBorder(MyPoint casterPos, MyPoint targetPos, int range) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		return ((deltaX == range) || (deltaY == range));
	}

	private boolean getCircleBorderOptions(MyPoint casterPos, Ability ability, MyPoint targetPos) {
		final int range = ability.getSpellData().getRange();
		final int areaOfEffectRange = ability.getSpellData().getAreaOfEffectRange();
		switch (ability.getAreaOfEffect()) {
		case CELL:
			return checkCircleBorder(casterPos, targetPos, range);
		case HORIZONTAL_LINE:
			return checkCircleBorderHorizontalLine(casterPos, targetPos, range, areaOfEffectRange);
		case VERTICAL_LINE:
			return checkCircleBorderVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE:
			return checkCircle(casterPos, targetPos, range + areaOfEffectRange);
		case SQUARE:
			return checkCircleBorderSquare(casterPos, targetPos, range, areaOfEffectRange);
		case CROSS:
			return checkCircleBorderHorizontalLine(casterPos, targetPos, range, areaOfEffectRange) || checkCircleBorderVerticalLine(casterPos, targetPos, range, areaOfEffectRange);
		case DIAGONAL:
			return checkCircleBorderDiagonal(casterPos, targetPos, range, areaOfEffectRange);
		case SQUARE_BORDER:
			return checkCircleBorderSquare(casterPos, targetPos, range, areaOfEffectRange);
		case CIRCLE_BORDER:
			return checkCircle(casterPos, targetPos, range + areaOfEffectRange);
		default:
			return false;
		}
	}

	private boolean checkCircleBorderDiagonal(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((deltaX > max) || (deltaY > max)) {
			return false;
		}

		return ((deltaX + deltaY) % 2) == 0;
	}

	private boolean checkCircleBorderSquare(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((deltaX > max) || (deltaY > max)) {
			return false;
		}

		return (deltaX + deltaY) <= (range + (2 * areaOfEffectRange));
	}

	private boolean checkCircleBorder(MyPoint casterPos, MyPoint targetPos, int range) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		return ((deltaX + deltaY) == range);
	}

	private boolean checkCircleBorderVerticalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((deltaX > range) || (deltaY > max) || ((deltaY == 0) && (deltaX == range))) {
			return false;
		}

		return ((targetPos.y >= ((casterPos.y + range) - areaOfEffectRange)) || (targetPos.y <= ((casterPos.y + range + areaOfEffectRange) - deltaX)));
	}

	private boolean checkCircleBorderHorizontalLine(MyPoint casterPos, MyPoint targetPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(casterPos.x - targetPos.x);
		final int deltaY = Math.abs(casterPos.y - targetPos.y);
		final int max = range + areaOfEffectRange;
		if ((deltaX > max) || (deltaY > range) || ((deltaX == 0) && (deltaY == range))) {
			return false;
		}

		return ((targetPos.x >= ((casterPos.x + range) - areaOfEffectRange)) || (targetPos.x <= ((casterPos.x + range + areaOfEffectRange) - deltaY)));
	}

	public Set<MyPoint> getAllCastPointsWhereTargetIsHit(Ability ability, MyPoint targetPosition, MyPoint casterPosition, BattleState battleState) {
		Set<MyPoint> castingMyPoints = new HashSet<>();
		switch (ability.getLineOfSight()) {
		case LINE:
			castingMyPoints = tryLines(ability, targetPosition, casterPosition);
			break;
		case CIRCLE:
			castingMyPoints = tryCircles(ability, targetPosition, casterPosition);
			break;
		case CROSS:
			castingMyPoints = tryLines(ability, targetPosition, casterPosition);
			break;
		case SQUARE:
			castingMyPoints = trySquares(ability, targetPosition, casterPosition);
			break;
		case DIAGONAL_RIGHT:
			castingMyPoints = tryDiagonalRightCells(ability, targetPosition, casterPosition);
			break;
		case DIAGONAL_LEFT:
			castingMyPoints = tryDiagonalLeftCells(ability, targetPosition, casterPosition);
			break;
		case CIRCLE_BORDER:
			castingMyPoints = tryCircleBorderCells(ability, targetPosition, casterPosition);
			break;
		case SQUARE_BORDER:
			castingMyPoints = trySquareBorderCells(ability, targetPosition, casterPosition);
			break;
		default:
			return castingMyPoints;
		}

		return filter(castingMyPoints, battleState.getWidth(), battleState.getHeight());
	}

	private Set<MyPoint> filter(Set<MyPoint> castingMyPoints, int width, int height) {
		castingMyPoints.removeIf(point -> ((point.x < 0) || (point.x >= width) || (point.y < 0) || (point.y >= height)));
		return castingMyPoints;
	}

	private Array<MyPoint> filterDoubles(Array<MyPoint> targets) {
		final Array<MyPoint> result = new Array<>();
		final Set<MyPoint> singles = new HashSet<>();
		for (final MyPoint point : targets) {
			singles.add(point);
		}
		for (final MyPoint point : singles) {
			result.add(point);
		}
		return result;
	}

	private Set<MyPoint> trySquareBorderCells(Ability ability, MyPoint targetPosition, MyPoint casterPosition) {
		final Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		final int range = ability.getSpellData().getRange();
		final int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		final AreaOfEffect area = ability.getAreaOfEffect();

		for (int i = 0; i <= range; i++) {
			for (int j = 0; j <= range; j++) {
				if ((j == aoeRange) || (i == aoeRange)) {
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y + j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y + j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y - j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y - j), spotsToCastAbilityHittingTarget, aoeRange, area);
				}
			}
		}
		return spotsToCastAbilityHittingTarget;
	}

	private Set<MyPoint> tryCircleBorderCells(Ability ability, MyPoint targetPosition, MyPoint casterPosition) {
		final Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		final int range = ability.getSpellData().getRange();
		final int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		final AreaOfEffect area = ability.getAreaOfEffect();

		for (int i = 0; i <= range; i++) {
			for (int j = 0; j <= range; j++) {
				if (((i + j) == aoeRange) && !((i == 0) && (j == 0))) {
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y + j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y + j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y - j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y - j), spotsToCastAbilityHittingTarget, aoeRange, area);
				}
			}
		}
		return spotsToCastAbilityHittingTarget;
	}

	private Set<MyPoint> tryDiagonalRightCells(Ability ability, MyPoint targetPosition, MyPoint casterPosition) {
		final Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		final int range = ability.getSpellData().getRange();
		final int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		final AreaOfEffect area = ability.getAreaOfEffect();

		for (int i = 1; i <= range; i++) {
			for (int j = 1; j <= range; j++) {
				if (i == j) {
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y + j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y - j), spotsToCastAbilityHittingTarget, aoeRange, area);
				}
			}
		}
		return spotsToCastAbilityHittingTarget;
	}

	private Set<MyPoint> tryDiagonalLeftCells(Ability ability, MyPoint targetPosition, MyPoint casterPosition) {
		final Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		final int range = ability.getSpellData().getRange();
		final int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		final AreaOfEffect area = ability.getAreaOfEffect();

		for (int i = 1; i <= range; i++) {
			for (int j = 1; j <= range; j++) {
				if (i == j) {
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y + j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y - j), spotsToCastAbilityHittingTarget, aoeRange, area);
				}
			}
		}
		return spotsToCastAbilityHittingTarget;
	}

	private Set<MyPoint> trySquares(Ability ability, MyPoint targetPosition, MyPoint casterPosition) {
		final Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		final int range = ability.getSpellData().getRange();
		final int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		final AreaOfEffect area = ability.getAreaOfEffect();

		for (int i = 0; i <= range; i++) {
			for (int j = 0; j <= range; j++) {
				if ((j <= aoeRange) && (i <= aoeRange) && !((i == 0) && (j == 0))) {
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y + j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y + j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y - j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y - j), spotsToCastAbilityHittingTarget, aoeRange, area);
				}
			}
		}
		return spotsToCastAbilityHittingTarget;
	}

	private Set<MyPoint> tryCircles(Ability ability, MyPoint targetPosition, MyPoint casterPosition) {
		final Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		final int range = ability.getSpellData().getRange();
		final int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		final AreaOfEffect area = ability.getAreaOfEffect();

		for (int i = 0; i <= range; i++) {
			for (int j = 0; j <= range; j++) {
				if (((i + j) <= aoeRange) && !((i == 0) && (j == 0))) {
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y + j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y + j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y - j), spotsToCastAbilityHittingTarget, aoeRange, area);
					tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y - j), spotsToCastAbilityHittingTarget, aoeRange, area);
				}
			}
		}
		return spotsToCastAbilityHittingTarget;
	}

	private Set<MyPoint> tryLines(Ability ability, MyPoint targetPosition, MyPoint casterPosition) {
		final Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		final int range = ability.getSpellData().getRange();
		final int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		final AreaOfEffect area = ability.getAreaOfEffect();

		for (int i = 1; i <= range; i++) {
			tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x, casterPosition.y + i), spotsToCastAbilityHittingTarget, aoeRange, area);
			tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x, casterPosition.y - i), spotsToCastAbilityHittingTarget, aoeRange, area);
			tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x + i, casterPosition.y), spotsToCastAbilityHittingTarget, aoeRange, area);
			tryToAddPotentialCenter(targetPosition, new MyPoint(casterPosition.x - i, casterPosition.y), spotsToCastAbilityHittingTarget, aoeRange, area);
		}

		return spotsToCastAbilityHittingTarget;
	}

	private void tryToAddPotentialCenter(MyPoint targetPosition, MyPoint center, Set<MyPoint> spotsToCastAbilityHittingTarget, int aoeRange, AreaOfEffect area) {
		if (checkIfTargetInAreaOfEffect(center, targetPosition, area, aoeRange)) {
			spotsToCastAbilityHittingTarget.add(center);
		}
	}

	private boolean checkIfTargetInAreaOfEffect(MyPoint center, MyPoint target, AreaOfEffect area, int aoeRange) {
		switch (area) {
		case CELL:
			return center.equals(target);
		case STRAIGHT_LINE:
			return center.equals(target);
		case HORIZONTAL_LINE:
			return (center.y == target.y) && (Math.abs(center.x - target.x) <= aoeRange);
		case VERTICAL_LINE:
			return (center.x == target.x) && (Math.abs(center.y - target.y) <= aoeRange);
		case CIRCLE:
			return Math.abs((center.x + center.y) - (target.x + target.y)) <= aoeRange;
		case CROSS:
			return ((center.y == target.y) && (Math.abs(center.x - target.x) <= aoeRange)) || ((center.x == target.x) && (Math.abs(center.y - target.y) <= aoeRange));
		case SQUARE:
			return (Math.abs(center.x - target.x) <= aoeRange) && (Math.abs(center.y - target.y) <= aoeRange);
		case DIAGONAL:
			final int diffX = Math.abs(center.x - target.x);
			final int diffY = Math.abs(center.y - target.y);
			return (diffX == diffY) && (diffX <= aoeRange);
		case SQUARE_BORDER:
			return (Math.abs(center.x - target.x) == aoeRange) && (Math.abs(center.y - target.y) == aoeRange);
		case CIRCLE_BORDER:
			return Math.abs((center.x + center.y) - (target.x + target.y)) == aoeRange;
		default:
			return false;
		}
	}

	public Array<MyPoint> getTargetsAbility(Ability ability, MyPoint point, Array<MyPoint> targetPositions) {
		final Array<MyPoint> targets = new Array<>();
		for (final MyPoint targetPosition : targetPositions) {
			if (checkIfTargetInAreaOfEffect(point, targetPosition, ability.getAreaOfEffect(), ability.getSpellData().getAreaOfEffectRange())) {
				targets.add(targetPosition);
			}
		}
		return filterDoubles(targets);
	}

	public Set<MyPoint> calculateSpellPath(final Entity unit, final Ability ability, final List<TiledMapPosition> positions, BattleState battleState) {
		Set<MyPoint> spellPath = null;
		switch (ability.getLineOfSight()) {
		case SQUARE:
			spellPath = getPossibleCenterCells(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.SQUARE, ability.getSpellData().getRange());
			break;
		case DIAGONAL_LEFT:
			spellPath = getPossibleCenterCells(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.DIAGONAL_LEFT, ability.getSpellData().getRange());
			break;
		case SQUARE_BORDER:
			spellPath = getPossibleCenterCells(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.SQUARE_BORDER, ability.getSpellData().getRange());
			break;
		case CIRCLE_BORDER:
			spellPath = getPossibleCenterCells(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.CIRCLE_BORDER, ability.getSpellData().getRange());
			break;
		case CIRCLE:
			spellPath = getPossibleCenterCells(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.CIRCLE, ability.getSpellData().getRange());
			break;
		case DIAGONAL_RIGHT:
			spellPath = getPossibleCenterCells(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.DIAGONAL_RIGHT, ability.getSpellData().getRange());
			break;
		case CROSS:
			spellPath = getPossibleCenterCells(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.CROSS, ability.getSpellData().getRange());
			break;
		case LINE:
			spellPath = getPossibleCenterCells(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.LINE, ability.getSpellData().getRange());
			break;
		case SELF:
			spellPath = new HashSet<>();
			spellPath.add(new MyPoint(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY()));
			break;
		default:
			spellPath = new HashSet<>();
			break;
		}
		filter(spellPath, battleState.getWidth(), battleState.getHeight());
		MyPathFinder.getInstance().filterUnwalkablePositions(spellPath);
		if (!ability.getGoesTroughObstacles()) {
			MyPathFinder.getInstance().filterPositionsByLineOfSight(unit, spellPath, positions, !ability.getGoesTroughUnits());
		}

		return spellPath;
	}

	public boolean isNextToButNotSelf(Entity unit, Entity currentUnit) {
		int unitX = unit.getCurrentPosition().getTileX();
		int unitY = unit.getCurrentPosition().getTileY();
		int currentUnitX = currentUnit.getCurrentPosition().getTileX();
		int currentUnitY = currentUnit.getCurrentPosition().getTileY();

		return (Math.abs(unitX - currentUnitX) <= 1) && (Math.abs(unitY - currentUnitY) <= 1) && !((unitX == currentUnitX) && (unitY == currentUnitY));
	}

	public static BattleStateGridHelper getInstance() {
		if (instance == null) {
			instance = new BattleStateGridHelper();
		}
		return instance;
	}

	private BattleStateGridHelper() {

	}
}
