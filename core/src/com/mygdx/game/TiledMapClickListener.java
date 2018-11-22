package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TiledMapClickListener extends ClickListener {
	private static final String TAG = TiledMapClickListener.class.getSimpleName();

    private TiledMapActor actor;

    public TiledMapClickListener(TiledMapActor actor) {
        this.actor = actor;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        Gdx.app.debug(TAG, "(" + actor.getX() + " , " + actor.getY() + ") has been clicked.");
    	BattleState battlestate = actor.getBattlemanager().getBattleState();
        if(actor.getIsFreeSpawn() && battlestate == BattleState.UNIT_PLACEMENT) {
        	actor.getBattlemanager().deployUnit(actor.getX() * MapManager.UNIT_SCALE,actor.getY() * MapManager.UNIT_SCALE);
        	actor.setIsFreeSpawn(false);
        }
    }
}
