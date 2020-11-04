package com.mygdx.game.Map;

import Utility.TiledMapPosition;

public interface TiledMapObserver {
    public enum TilemapCommand {
        HOVER_CHANGED
    }
    
	void onTiledMapNotify(TilemapCommand command, TiledMapPosition pos);
}
