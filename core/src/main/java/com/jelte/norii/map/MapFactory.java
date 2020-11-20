package com.jelte.norii.map;

import java.util.EnumMap;

public class MapFactory {
	private static EnumMap<MapType, Map> mapTable = new EnumMap<>(MapType.class);

	public enum MapType {
		BATTLE_MAP_THE_DARK_SWAMP {
			@Override
			public String toString() {
				return "maps/Field.tmx";
			}
		}
	}

	public static Map getMap(MapType mapType) {
		return mapTable.computeIfAbsent(mapType, MapFactory::computeMap);
	}

	private static BattleMap computeMap(MapType k) {
		final BattleMap map = new BattleMap(k, k.toString());
		mapTable.put(k, map);
		return map;
	}
}
