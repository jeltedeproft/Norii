package com.mygdx.game.Battle;

import com.mygdx.game.Map.TiledMapActor;

public interface BattleState {
	public void entry();
	public void update();
	public void clickedOnTile(TiledMapActor actor);
	public void exit();	
}
