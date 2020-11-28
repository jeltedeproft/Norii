package com.jelte.norii.battle.battleStates;

import java.util.stream.IntStream;

public class StateOfBattle {
	private final int[][] stateOfField;
	private int score;

	public static final int NO_UNIT = 0;

	public StateOfBattle(int width, int height) {
		stateOfField = new int[width][height];
		score = 0;
	}

	public StateOfBattle(int[][] field) {
		stateOfField = field;
		score = 0;
	}

	public StateOfBattle(int[][] field, int score) {
		stateOfField = field;
		this.score = score;
	}

	public void set(int width, int height, int value) {
		final int original = stateOfField[width][height];
		final int difference = value - original;
		score += difference;
		stateOfField[width][height] = value;
	}

	public int get(int width, int height) {
		return stateOfField[width][height];
	}

	public StateOfBattle makeCopy() {
		final int[][] copy = new int[stateOfField.length][];
		for (int i = 0; i < stateOfField.length; i++) {
			final int[] row = stateOfField[i];
			final int height = row.length;
			copy[i] = new int[height];
			System.arraycopy(row, 0, copy[i], 0, height);
		}
		return new StateOfBattle(copy, score);
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
}
