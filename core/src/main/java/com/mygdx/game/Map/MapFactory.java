package com.mygdx.game.Map;

import java.util.EnumMap;


public class MapFactory {
	private static EnumMap<MapType, Map> mapTable = new EnumMap<MapType, Map>(MapType.class);

	public enum MapType {
		BATTLE_MAP_THE_TOWN {
			@Override
			public String toString() {
				return "maps/battletown.tmx";
			}
		},
		BATTLE_MAP_THE_VILLAGE {
			@Override
			public String toString() {
				return "maps/32x32 rpg battlemap(UIseparate).tmx";
			}
		}
	}

	public static Map getMap(MapType mapType) {
		Map map = null;
		map = mapTable.get(mapType);
		if (map == null) {
			map = new BattleMap(mapType.toString());
			mapTable.put(mapType, map);
		}

		return map;
	}
}
