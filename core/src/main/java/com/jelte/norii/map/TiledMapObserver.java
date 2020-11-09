package com.jelte.norii.map;

import com.jelte.norii.utility.TiledMapPosition;

public interface TiledMapObserver {
	public enum TilemapCommand {
		HOVER_CHANGED
	}

	void onTiledMapNotify(TilemapCommand command, TiledMapPosition pos);
}
