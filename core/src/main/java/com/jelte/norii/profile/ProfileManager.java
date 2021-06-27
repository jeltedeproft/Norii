package com.jelte.norii.profile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.jelte.norii.entities.EntityData;
import com.jelte.norii.entities.EntityFileReader;

public class ProfileManager {
	private static final String TAG = ProfileManager.class.getSimpleName();
	private static ProfileManager profileManager;
	private static final String PROPERTIES = "properties";
	private Preferences preferences;
	private final Json json;
	private boolean isInitialised = false;

	private ProfileManager() {
		preferences = Gdx.app.getPreferences(PROPERTIES);
		json = new Json();
	}

	public static final ProfileManager getInstance() {
		if (profileManager == null) {
			profileManager = new ProfileManager();
		}
		return profileManager;
	}

	public Array<String> getTeamHeroes() {
		String serializedHeroes = preferences.getString(PropertiesEnum.TEAM_HEROES.getPropertyName());
		Array<String> deserializedHeroes = json.fromJson(Array.class, serializedHeroes);
		return deserializedHeroes;
	}

	public void setTeamHeroes(Array<String> heroes) {
		preferences.putString(PropertiesEnum.TEAM_HEROES.getPropertyName(), json.toJson(heroes));
	}

	public Array<String> getAvailableHeroes() {
		String serializedHeroes = preferences.getString(PropertiesEnum.AVAILABLE_HEROES.getPropertyName());
		Array<String> deserializedHeroes = json.fromJson(Array.class, serializedHeroes);
		return deserializedHeroes;
	}

	public void setAvailableHeroes(Array<String> heroes) {
		preferences.putString(PropertiesEnum.AVAILABLE_HEROES.getPropertyName(), json.toJson(heroes));
	}

	public int getMaxHeroCount() {
		return preferences.getInteger(PropertiesEnum.MAX_HERO_COUNT.getPropertyName());
	}

	public void setMaxHeroCount(int count) {
		preferences.putInteger(PropertiesEnum.MAX_HERO_COUNT.getPropertyName(), count);
	}

	public void saveSettings() {
		preferences.flush();
	}

	public void initialise() {
		if (!isInitialised()) {
			setMaxHeroCount(10);

			Array<String> availableHeroes = new Array<>();
			ObjectMap<Integer, EntityData> entityData = EntityFileReader.getUnitData();
			for (final EntityData data : entityData.values()) {
				availableHeroes.add(data.getName());
			}

			setAvailableHeroes(availableHeroes);
			Array<String> teamHeroes = new Array<>();
			teamHeroes.add("Black Slime Green Eyes");
			setTeamHeroes(teamHeroes);

			preferences.putBoolean("initialised", true);
			saveSettings();
		}
	}

	private boolean isInitialised() {
		return preferences.getBoolean("initialised");
	}
}
