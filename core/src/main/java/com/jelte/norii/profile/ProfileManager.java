package com.jelte.norii.profile;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class ProfileManager extends ProfileSubject {
	private static final String TAG = ProfileManager.class.getSimpleName();
	private final Json json;
	private static ProfileManager profileManager;
	private HashMap<String, FileHandle> profilesWithFile = null;
	private ObjectMap<String, Object> profileProperties = new ObjectMap<>();
	private String profileName;

	private static final String SAVEGAME_SUFFIX = ".sav";
	public static final String DEFAULT_PROFILE = "default";
	public static final String SAVEFILES_DIRECTORY = "properties/savefiles/";

	private ProfileManager() {
		json = new Json();
		profilesWithFile = new HashMap<>();
		profilesWithFile.clear();
		profileName = DEFAULT_PROFILE;
		storeAllProfiles();
	}

	public static final ProfileManager getInstance() {
		if (profileManager == null) {
			profileManager = new ProfileManager();
		}
		return profileManager;
	}

	public Array<String> getProfileList() {
		final Array<String> profiles = new Array<>();
		for (final Iterator<String> e = profilesWithFile.keySet().iterator(); e.hasNext();) {
			profiles.add(e.next());
		}
		return profiles;
	}

	public FileHandle getProfileFile(String profile) {
		if (!doesProfileExist(profile)) {
			return null;
		}
		return profilesWithFile.get(profile);
	}

	public void storeAllProfiles() {
		if (Gdx.files.isLocalStorageAvailable()) {
			final FileHandle listOfProfiles = Gdx.files.internal(SAVEFILES_DIRECTORY + "assets.txt");
			String text = listOfProfiles.readString();
			String arrayOfProfiles[] = text.split("\\r?\\n");
			for (String profile : arrayOfProfiles) {
				FileHandle profileFileHandle = Gdx.files.internal(SAVEFILES_DIRECTORY + profile);
				profilesWithFile.put(profileFileHandle.nameWithoutExtension(), profileFileHandle);
			}
		} else {
			// try external directory
		}
	}

	public boolean doesProfileExist(String profName) {
		return profilesWithFile.containsKey(profName);
	}

	public void writeProfileToStorage(String profName, String fileData, boolean overwrite) {
		final String fullFilename = SAVEFILES_DIRECTORY + profName + SAVEGAME_SUFFIX;

		final boolean localFileExists = Gdx.files.internal(fullFilename).exists();

		// If we cannot overwrite and the file exists, exit
		if (localFileExists && !overwrite) {
			return;
		}

		FileHandle file = null;

		if (Gdx.files.isLocalStorageAvailable()) {
			file = Gdx.files.local(fullFilename);
			file.writeString(fileData, !overwrite);

			profilesWithFile.put(profName, file);
		}
	}

	public void setProperty(String key, Object object) {
		profileProperties.put(key, object);
	}

	public <T extends Object> T getProperty(String key) {
		T property = null;
		if (!profileProperties.containsKey(key)) {
			return property;
		}
		property = (T) profileProperties.get(key);
		return property;
	}

	public void saveProfile() {
		notify(this, ProfileObserver.ProfileEvent.SAVING_PROFILE);
		final String text = json.prettyPrint(json.toJson(profileProperties));
		writeProfileToStorage(profileName, text, true);
	}

	@SuppressWarnings("unchecked")
	public void loadProfile() {
		final String fullProfileFileName = SAVEFILES_DIRECTORY + profileName + SAVEGAME_SUFFIX;
		final boolean doesProfileFileExist = Gdx.files.internal(fullProfileFileName).exists();

		if (!doesProfileFileExist) {
			Gdx.app.debug(TAG, "File doesn't exist!");
			return;
		}
		try {
			profileProperties = (ObjectMap<String, Object>) json.fromJson(ClassReflection.forName("com.badlogic.gdx.utils.ObjectMap"), profilesWithFile.get(profileName));
			notify(this, ProfileObserver.ProfileEvent.PROFILE_LOADED);
		} catch (final ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setCurrentProfile(String profName) {
		if (doesProfileExist(profName)) {
			profileName = profName;
		} else {
			profileName = DEFAULT_PROFILE;
		}
	}

}
