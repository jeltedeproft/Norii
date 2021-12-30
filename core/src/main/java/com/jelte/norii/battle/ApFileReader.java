package com.jelte.norii.battle;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class ApFileReader {
	private static boolean statsLoaded = false;
	private static ObjectMap<Integer, Integer> apData = new ObjectMap<>();
	private static final String AP_FILE_LOCATION = "levels/ap.json";
	public static int maxAp;

	private ApFileReader() {

	}

	@SuppressWarnings("unchecked")
	public static void loadApInMemory() {
		if (!statsLoaded) {
			final Json json = new Json();
			ArrayList<Integer> aps;
			try {
				aps = (ArrayList<Integer>) json.fromJson(ClassReflection.forName("java.util.ArrayList"), ClassReflection.forName("java.lang.Integer"), Gdx.files.internal(AP_FILE_LOCATION));
				for (int i = 0; i < aps.size(); i++) {
					final Integer ap = aps.get(i);
					apData.put(i, ap);
				}
				statsLoaded = true;

				maxAp = Collections.max(aps);

			} catch (final ReflectionException e) {
				e.printStackTrace();
			}
		}
	}

	public static Integer getApData(Integer round) {
		return apData.get(round);
	}
}
