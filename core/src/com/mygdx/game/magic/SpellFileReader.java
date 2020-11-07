package com.mygdx.game.magic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class SpellFileReader {
	private static boolean statsLoaded = false;
	private static ObjectMap<Integer, SpellData> spellData = new ObjectMap<>();
	private static final String UNIT_STATS_FILE_LOCATION = "spells/spells.json";

	private SpellFileReader() {

	}

	public static void loadSpellsInMemory() {
		if (!statsLoaded) {
			final Json json = new Json();
			final SpellData[] unitStats = json.fromJson(SpellData[].class, Gdx.files.internal(UNIT_STATS_FILE_LOCATION));
			for (int i = 0; i < unitStats.length; i++) {
				final SpellData data = unitStats[i];
				spellData.put(data.getId(), data);
			}
			statsLoaded = true;
		}
	}

	public static ObjectMap<Integer, SpellData> getSpellData() {
		return spellData;
	}
}
