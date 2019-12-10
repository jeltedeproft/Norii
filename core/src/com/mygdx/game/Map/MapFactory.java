package com.mygdx.game.Map;

import java.util.EnumMap;

public class MapFactory {
    private static EnumMap<MapType,Map> mapTable = new EnumMap<MapType, Map>(MapType.class);

    public enum MapType{
        BATTLE_MAP
    }
    
	public enum MapFileNames{
		BATTLE_MAP_THE_VILLAGE{
			@Override
	        public String toString() {
	            return "maps/field.tmx";
	        }
		}
	}

    public static Map getMap(MapType mapType){
        Map map = null;
        switch(mapType){
            case BATTLE_MAP:
                map = mapTable.get(MapType.BATTLE_MAP);
                if( map == null ){
                    map = new BattleMap(MapFileNames.BATTLE_MAP_THE_VILLAGE.toString());
                    mapTable.put(MapType.BATTLE_MAP, map);
                }
                break;
            default:
                break;
        }
        return map;
    }
}

