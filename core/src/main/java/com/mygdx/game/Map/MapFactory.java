package com.mygdx.game.Map;

import java.util.EnumMap;

public class MapFactory {
	private static EnumMap<MapType, Map> mapTable = new EnumMap<>(MapType.class);

	public enum MapType {
		BATTLE_MAP_THE_TOWN {
			@Override
			public String toString() {
				return "maps/battle-raining.tmx";
			}
		},
		BATTLE_MAP_THE_VILLAGE {
			@Override
			public String toString() {
				return "maps/32x32 rpg battlemap(UIseparate).tmx";
			}
		},
		BATTLE_MAP_THE_CELLS {
			@Override
			public String toString() {
				return "maps/tactical.tmx";
			}
		},
		BATTLE_MAP_THE_DARK_SWAMP {
			@Override
			public String toString() {
				return "maps/Field.tmx";
			}
		}
	}

	public static Map getMap(MapType mapType) {
		return mapTable.computeIfAbsent(mapType, k -> computeMap(k));
	}

	private static BattleMap computeMap(MapType k) {
		BattleMap map = new BattleMap(k.toString());
		mapTable.put(k, map);
		return map;
	}
}
