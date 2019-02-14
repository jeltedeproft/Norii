package com.mygdx.game.Map;

import java.util.Hashtable;

public class MapFactory {
    //All maps for the game
    private static Hashtable<MapType,Map> _mapTable = new Hashtable<MapType, Map>();

    public static enum MapType{
        BATTLE_MAP
    }
    
	public static enum MapFileNames{
		BATTLE_MAP_THE_VILLAGE{
	        public String toString() {
	            return "maps/32x32 rpg battlemap(navigation).tmx";
	        }
		}
	}

    static public Map getMap(MapType mapType){
        Map map = null;
        switch(mapType){
            case BATTLE_MAP:
                map = _mapTable.get(MapType.BATTLE_MAP);
                if( map == null ){
                    map = new BattleMap(MapFileNames.BATTLE_MAP_THE_VILLAGE.toString());
                    _mapTable.put(MapType.BATTLE_MAP, map);
                }
                break;
            default:
                break;
        }
        return map;
    }
}

