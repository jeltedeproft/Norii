package com.jelte.norii.battle.battleState;

import java.awt.Point;

import com.jelte.norii.magic.Ability;

public class BattleStateGridHelperFromUnits {

	public boolean isUnitInAbilityRange(Point center, Ability ability, Point unitPos) {
		switch (ability.getLineOfSight()) {
		case LINE:
			return getLineOptions(center, ability, unitPos);
		}
	}

	private boolean getLineOptions(Point center, Ability ability, Point unitPos) {
		final int range = ability.getSpellData().getRange();
		final int areaOfEffectRange = ability.getSpellData().getAreaOfEffectRange();
		switch (ability.getAreaOfEffect()) {
		case CELL:
			return checkCross(center, unitPos, range);
		case HORIZONTAL_LINE:
			return checkLineHorizontalLine(center, unitPos, range, areaOfEffectRange) || checkLineHorizontalLine(center, unitPos, areaOfEffectRange, range);
		case VERTICAL_LINE:
			return checkCross(center, unitPos, range + areaOfEffectRange);
		case CIRCLE:
			return checkLineCircle(center, unitPos, range, areaOfEffectRange);
		case SQUARE:
			return checkLineSquare(center, unitPos, range, areaOfEffectRange);
		case CROSS:
			return checkLineHorizontalLine(center, unitPos, range, areaOfEffectRange) || checkLineHorizontalLine(center, unitPos, areaOfEffectRange, range) || checkCross(center, unitPos, range + areaOfEffectRange);
		case DIAGONAL:
			return checkLineDiagonal(center, unitPos, range, areaOfEffectRange);
		case SQUARE_BORDER:
			return checkLineSquareBorder(center, unitPos, range, areaOfEffectRange);
		case CIRCLE_BORDER:
			return checkLineCircleBorder(center, unitPos, range, areaOfEffectRange);
		default:
			return false;
		}
	}

	private boolean checkLineCircleBorder(Point center, Point unitPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(center.x - unitPos.x);
		final int deltaY = Math.abs(center.y - unitPos.y);
		return ((deltaX + deltaY) <= (range + areaOfEffectRange)) && !((deltaX == 0) && (deltaY == areaOfEffectRange));
	}

	private boolean checkLineSquareBorder(Point center, Point unitPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(center.x - unitPos.x);
		final int deltaY = Math.abs(center.y - unitPos.y);
		final int max = range + areaOfEffectRange;
		if ((Math.abs(center.x - unitPos.x) > max) || (Math.abs(center.y - unitPos.y) > max)) {
			return false;
		}

		final int limit = (areaOfEffectRange * 2) + 1;
		return !((deltaX > limit) && (deltaY > limit));
	}

	private boolean checkLineDiagonal(Point center, Point unitPos, int range, int areaOfEffectRange) {
		final int deltaX = Math.abs(center.x - unitPos.x);
		final int deltaY = Math.abs(center.y - unitPos.y);
		final int max = range + areaOfEffectRange;
		if ((Math.abs(center.x - unitPos.x) > max) || (Math.abs(center.y - unitPos.y) > max)) {
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

	private boolean checkLineSquare(Point center, Point unitPos, int range, int areaOfEffectRange) {
		return (Math.abs(center.y - unitPos.y) <= (range + areaOfEffectRange)) && (Math.abs(center.x - unitPos.x) <= (range + areaOfEffectRange));
	}

	private boolean checkLineCircle(Point center, Point unitPos, int range, int areaOfEffectRange) {
		return ((Math.abs(center.y - unitPos.y) + (Math.abs(center.x - unitPos.x))) <= (range + areaOfEffectRange));
	}

	private boolean checkLineHorizontalLine(Point center, Point unitPos, final int range, final int areaOfEffectRange) {
		return (Math.abs(center.y - unitPos.y) <= range) && (Math.abs(center.x - unitPos.x) <= areaOfEffectRange);
	}

	private boolean checkCross(Point center, Point unitPos, final int range) {
		return ((center.x == unitPos.x) && (Math.abs(center.y - unitPos.y) <= range)) || ((center.y == unitPos.y) && (Math.abs(center.x - unitPos.x) <= range));
	}
}
