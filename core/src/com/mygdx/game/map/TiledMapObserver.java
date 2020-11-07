package com.mygdx.game.map;

import utility.TiledMapPosition;

public interface TiledMapObserver {
    public enum TilemapCommand {
        HOVER_CHANGED
    }
    
	void onTiledMapNotify(TilemapCommand command, TiledMapPosition pos);
}
