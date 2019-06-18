package com.mygdx.game.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import Utility.TiledMapPosition;

public class TiledMapClickListener extends ClickListener {
	private static final String TAG = TiledMapClickListener.class.getSimpleName();

    private TiledMapActor actor;

    public TiledMapClickListener(TiledMapActor actor) {
        this.actor = actor;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
    	Gdx.app.debug(TAG, "clicked on : (" + actor.getX() + " , " + actor.getY());
    	TiledMapStage stage = (TiledMapStage) actor.getStage();
    	stage.getBattlemanager().getCurrentBattleState().clickedOnTile(actor);   
    }
}
