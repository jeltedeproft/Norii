package com.mygdx.game.AI;

public class Level {
	private final LevelData levelData;

	public Level(final LevelTypes type) {
		levelData = LevelFileReader.getLevelData().get(type.ordinal());
	}
}
