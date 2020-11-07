package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class EntityFileReader {
	private static boolean statsLoaded = false;
	private static ObjectMap<Integer, EntityData> unitData = new ObjectMap<>();
	private static final String UNIT_STATS_FILE_LOCATION = "units/unitStats.json";

	private EntityFileReader() {

	}

	public static void loadUnitStatsInMemory() {
		if (!statsLoaded) {
			final Json json = new Json();
			final EntityData[] unitStats = json.fromJson(EntityData[].class, Gdx.files.internal(UNIT_STATS_FILE_LOCATION));
			for (int i = 0; i < unitStats.length; i++) {
				final EntityData data = unitStats[i];
				unitData.put(data.getID(), data);
			}
			statsLoaded = true;
		}
	}

	public static ObjectMap<Integer, EntityData> getUnitData() {
		return unitData;
	}
}
