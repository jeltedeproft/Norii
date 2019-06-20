package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Profile.ProfileManager;
import com.mygdx.game.Profile.ProfileObserver;


public class PlayerBattleHUD implements Screen, ProfileObserver {
	private static final String TAG = PlayerBattleHUD.class.getSimpleName();
	
    private Stage _stage;
    private Viewport _viewport;
    private Entity[] SortedUnits;
    private PortraitsUI _portraits;
    private StatusUI[] _statusUIs;
    private ActionsUI[] _actionUIs;
//  private InventoryUI _inventoryUI;
    private Camera _camera;

    public PlayerBattleHUD(Camera camera,Entity[] SortedUnits) {
    	this.SortedUnits = SortedUnits;
    	_statusUIs = new StatusUI[SortedUnits.length];
    	_actionUIs = new ActionsUI[SortedUnits.length];
        _camera = camera;
        _viewport = new ScreenViewport(_camera);
        _stage = new Stage(_viewport);
        _stage.setDebugAll(true);//!!!!!!!!!!!!!!!! on for debug
        
        //create a status & actions window for every unit
        for (int i = 0; i < SortedUnits.length; i++) {
        	Entity entity = SortedUnits[i];
        	_statusUIs[i] = new StatusUI(entity);
        	_actionUIs[i] = new ActionsUI(entity);
        	StatusUI statusui = _statusUIs[i];
        	ActionsUI actionui = _actionUIs[i];
	        
	        //status window blocks input beneath
        	statusui.addListener(new InputListener() {
	        	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	         		return true;
	         	}
	        });
        	
        	//action window blocks input beneath
        	actionui.addListener(new InputListener() {
        		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        			return true;
        		}
        	});
	        
	        _stage.addActor(statusui);
	        _stage.addActor(actionui);
        }
        
        //add portraits
    	_portraits = new PortraitsUI(SortedUnits);
    	
        //portrait window blocks input beneath
    	_portraits.addListener(new InputListener() {
        	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
         		return true;
         	}
        });
    	
    	_stage.addActor(_portraits);
    }

    public Stage getStage() {
        return _stage;
    }

    @Override
    public void onNotify(ProfileManager profileManager, ProfileEvent event) {

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        _stage.act(delta);
        _stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    	Gdx.app.debug(TAG, "resizing with : (" + width + " , " + height + ")");
        _stage.getViewport().update(width, height, true);
        _portraits.updateSizeContainer();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        _stage.dispose();
    }

}

