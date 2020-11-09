package com.jelte.norii.ai;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class AITeamFileReader {
	private static boolean statsLoaded = false;
	private static ObjectMap<Integer, AITeamData> aiTeamData = new ObjectMap<Integer, AITeamData>();
	private static final String LEVELS_FILE_LOCATION = "levels/levels.json";

	private AITeamFileReader() {

	}

	@SuppressWarnings("unchecked")
	public static void loadLevelsInMemory() {
		if (!statsLoaded) {
			final Json json = new Json();
			ArrayList<AITeamData> aiTeamStats;
			try {
				aiTeamStats = (ArrayList<AITeamData>) json.fromJson(ClassReflection.forName("java.util.ArrayList"), ClassReflection.forName("com.jelte.norii.ai.AITeamData"), Gdx.files.internal(LEVELS_FILE_LOCATION));
				for (int i = 0; i < aiTeamStats.size(); i++) {
					final AITeamData data = aiTeamStats.get(i);
					aiTeamData.put(data.getId(), data);
				}
				statsLoaded = true;
			} catch (final ReflectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static ObjectMap<Integer, AITeamData> getAITeamData() {
		return aiTeamData;
	}
}
