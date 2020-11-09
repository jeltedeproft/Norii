package com.jelte.norii.magic;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class SpellFileReader {
	private static boolean statsLoaded = false;
	private static ObjectMap<Integer, SpellData> spellData = new ObjectMap<>();
	private static final String UNIT_STATS_FILE_LOCATION = "spells/spells.json";

	private SpellFileReader() {

	}

	@SuppressWarnings("unchecked")
	public static void loadSpellsInMemory() {
		if (!statsLoaded) {
			final Json json = new Json();
			ArrayList<SpellData> unitStats;
			try {
				unitStats = (ArrayList<SpellData>) json.fromJson(ClassReflection.forName("java.util.ArrayList"), ClassReflection.forName("com.jelte.norii.magic.SpellData"), Gdx.files.internal(UNIT_STATS_FILE_LOCATION));
				for (int i = 0; i < unitStats.size(); i++) {
					final SpellData data = unitStats.get(i);
					spellData.put(data.getId(), data);
				}
				statsLoaded = true;
			} catch (final ReflectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static ObjectMap<Integer, SpellData> getSpellData() {
		return spellData;
	}
}
