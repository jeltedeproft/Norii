package com.mygdx.game.AI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class LevelFileReader {
	private static boolean statsLoaded = false;
	private static ObjectMap<Integer, LevelData> levelData = new ObjectMap<Integer, LevelData>();
	private static final String LEVELS_FILE_LOCATION = "levels/levels.json";

	private LevelFileReader() {

	}

	public static void loadLevelsInMemory() {
		if (!statsLoaded) {
			final Json json = new Json();
			final LevelData[] levelStats = json.fromJson(LevelData[].class, Gdx.files.internal(LEVELS_FILE_LOCATION));
			for (int i = 0; i < levelStats.length; i++) {
				final LevelData data = levelStats[i];
				levelData.put(data.getId(), data);
			}
			statsLoaded = true;
		}
	}

	public static ObjectMap<Integer, LevelData> getLevelData() {
		return levelData;
	}
}
