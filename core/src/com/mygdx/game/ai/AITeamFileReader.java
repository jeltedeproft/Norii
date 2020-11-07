package com.mygdx.game.AI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class AITeamFileReader {
	private static boolean statsLoaded = false;
	private static ObjectMap<Integer, AITeamData> aiTeamData = new ObjectMap<Integer, AITeamData>();
	private static final String LEVELS_FILE_LOCATION = "levels/levels.json";

	private AITeamFileReader() {

	}

	public static void loadLevelsInMemory() {
		if (!statsLoaded) {
			final Json json = new Json();
			final AITeamData[] aiTeamStats = json.fromJson(AITeamData[].class, Gdx.files.internal(LEVELS_FILE_LOCATION));
			for (int i = 0; i < aiTeamStats.length; i++) {
				final AITeamData data = aiTeamStats[i];
				aiTeamData.put(data.getId(), data);
			}
			statsLoaded = true;
		}
	}

	public static ObjectMap<Integer, AITeamData> getAITeamData() {
		return aiTeamData;
	}
}
