package com.jelte.norii.battleState;

import java.awt.Point;
import java.util.stream.IntStream;

import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;

public class BattleState {
	private final int[][] stateOfField;
	private int score;
	private Ability abilityUsed;
	private Point target;
	private Point moveTo;
	private Entity ai;

	public static final int NO_UNIT = 0;

	public BattleState(int width, int height) {
		stateOfField = new int[width][height];
		score = 0;
	}

	public BattleState(int[][] field) {
		stateOfField = field;
		score = 0;
	}

	public BattleState(int[][] field, int score) {
		stateOfField = field;
		this.score = score;
	}

	public void set(int width, int height, int value) {
		if ((height > 0) && (width > 0)) {
			final int original = stateOfField[width][height];
			final int difference = value - original;
			score += difference;
			stateOfField[width][height] = value;
		}
	}

	public int get(int width, int height) {
		return stateOfField[width][height];
	}

	public BattleState makeCopy() {
		final int[][] copy = new int[stateOfField.length][];
		for (int i = 0; i < stateOfField.length; i++) {
			final int[] row = stateOfField[i];
			final int height = row.length;
			copy[i] = new int[height];
			System.arraycopy(row, 0, copy[i], 0, height);
		}
		return new BattleState(copy, score);
	}

	public int getScore() {
		return score;
	}

	public int calculateScore() {
		int sum = 0;
		for (final int[] row : stateOfField) {
			sum += IntStream.of(row).sum();
		}
		return sum;
	}

	public int getWidth() {
		return stateOfField[0].length;
	}

	public int getHeight() {
		return stateOfField.length;
	}

	public Ability getAbilityUsed() {
		return abilityUsed;
	}

	public void setAbilityUsed(Ability abilityUsed) {
		this.abilityUsed = abilityUsed;
	}

	public Point getTarget() {
		return target;
	}

	public void setTarget(Point target) {
		this.target = target;
	}

	public Entity getAi() {
		return ai;
	}

	public void setAi(Entity ai) {
		this.ai = ai;
	}

	public Point getMoveTo() {
		return moveTo;
	}

	public void setMoveTo(Point moveTo) {
		this.moveTo = moveTo;
	}
}
