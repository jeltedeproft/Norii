package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class EntityFileReader {
	private static boolean statsLoaded = false;
	private static ObjectMap<Integer, EntityData> unitData = new ObjectMap<Integer, EntityData>();
	
	private EntityFileReader() {
		
	}
	
	public static void loadUnitStatsInMemory() {
		if(!statsLoaded) {
			Json json = new Json();
			EntityData[] unitStats = json.fromJson(EntityData[].class, Gdx.files.internal("units/unitStats.json"));
			for (int i = 0; i < unitStats.length; i++) {
				EntityData data = unitStats[i];
	            unitData.put(data.getID(), data);
	        }
		}
	}
	
	public static ObjectMap<Integer, EntityData> getUnitData(){
		return unitData;
	}
}
