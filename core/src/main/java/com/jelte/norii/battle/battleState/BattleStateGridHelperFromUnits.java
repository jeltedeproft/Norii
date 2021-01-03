package com.jelte.norii.battle.battleState;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.AreaOfEffect;
import com.jelte.norii.utility.MyPoint;

public class BattleStateGridHelperFromUnits {

	public Array<MyPoint> getTargetPositionsInRangeAbility(MyPoint casterPos, Ability ability, Array<MyPoint> unitPositions) {
		Array<MyPoint> results = new Array<>();
		for (MyPoint targetPos : unitPositions) {
			if (isUnitInAbilityRange(casterPos, ability, targetPos)) {
				results.add(targetPos);
			}
		}
		return results;
	}

	private boolean isUnitInAbilityRange(MyPoint casterPos, Ability ability, MyPoint targetPos) {
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

		// filter castingMyPoints for field boundaries and doubles
		return filter(castingMyPoints, battleState.getWidth(), battleState.getHeight());
	}

	private Set<MyPoint> filter(Set<MyPoint> castingMyPoints, int width, int height) {
		for (MyPoint MyPoint : castingMyPoints) {
			if (!((MyPoint.x >= 0) && (MyPoint.x < width) && (MyPoint.y >= 0) && (MyPoint.y < height))) {
				castingMyPoints.removeIf(setMyPoint -> !((setMyPoint.x < 0) || (setMyPoint.x > width) || (setMyPoint.y < 0) || (setMyPoint.y > height)));
			}
		}
		return castingMyPoints;
	}

	private Set<MyPoint> trySquareBorderCells(Ability ability, MyPoint targetPosition, MyPoint casterPosition) {
		Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		int range = ability.getSpellData().getRange();
		int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		AreaOfEffect area = ability.getAreaOfEffect();

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
		Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		int range = ability.getSpellData().getRange();
		int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		AreaOfEffect area = ability.getAreaOfEffect();

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
		Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		int range = ability.getSpellData().getRange();
		int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		AreaOfEffect area = ability.getAreaOfEffect();

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
		Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		int range = ability.getSpellData().getRange();
		int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		AreaOfEffect area = ability.getAreaOfEffect();

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
		Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		int range = ability.getSpellData().getRange();
		int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		AreaOfEffect area = ability.getAreaOfEffect();

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
		Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		int range = ability.getSpellData().getRange();
		int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		AreaOfEffect area = ability.getAreaOfEffect();

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
		Set<MyPoint> spotsToCastAbilityHittingTarget = new HashSet<>();
		int range = ability.getSpellData().getRange();
		int aoeRange = ability.getSpellData().getAreaOfEffectRange();
		AreaOfEffect area = ability.getAreaOfEffect();

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
			int diffX = Math.abs(center.x - target.x);
			int diffY = Math.abs(center.y - target.y);
			return (diffX == diffY) && (diffX <= aoeRange);
		case SQUARE_BORDER:
			return (Math.abs(center.x - target.x) == aoeRange) && (Math.abs(center.y - target.y) == aoeRange);
		case CIRCLE_BORDER:
			return Math.abs((center.x + center.y) - (target.x + target.y)) == aoeRange;
		default:
			return false;
		}
	}

	public Array<MyPoint> getTargetsAbility(Ability ability, MyPoint MyPoint, Array<MyPoint> targetPositions) {
		Array<MyPoint> targets = new Array<>();
		for (MyPoint targetPosition : targetPositions) {
			if (checkIfTargetInAreaOfEffect(MyPoint, targetPosition, ability.getAreaOfEffect(), ability.getSpellData().getAreaOfEffectRange())) {
				targets.add(targetPosition);
			}
		}
		return targets;
	}
}
