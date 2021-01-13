package com.jelte.norii.entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class EntityFileReader {
	private static boolean statsLoaded = false;
	private static ObjectMap<Integer, EntityData> unitData = new ObjectMap<>();
	private static final String UNIT_STATS_FILE_LOCATION = "units/unitStats.json";

	private EntityFileReader() {

	}

	@SuppressWarnings("unchecked")
	public static void loadUnitStatsInMemory() {
		if (!statsLoaded) {
			final Json json = new Json();
			ArrayList<EntityData> unitStats;
			try {
				unitStats = (ArrayList<EntityData>) json.fromJson(ClassReflection.forName("java.util.ArrayList"), ClassReflection.forName("com.jelte.norii.entities.EntityData"), Gdx.files.internal(UNIT_STATS_FILE_LOCATION));
				for (int i = 0; i < unitStats.size(); i++) {
					final EntityData data = unitStats.get(i);
					unitData.put(data.getID(), data);
				}
				statsLoaded = true;
			} catch (final ReflectionException e) {
				e.printStackTrace();
			}
		}
	}

	public static ObjectMap<Integer, EntityData> getUnitData() {
		return unitData;
	}
}
