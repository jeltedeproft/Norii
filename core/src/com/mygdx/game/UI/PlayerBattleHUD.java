package com.mygdx.game.UI;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Profile.ProfileManager;
import com.mygdx.game.Profile.ProfileObserver;

import Utility.Utility;


public class PlayerBattleHUD implements Screen, ProfileObserver {
	private static final String TAG = PlayerBattleHUD.class.getSimpleName();
	
    private Stage stage;
    private PortraitsUI portraits;
    private StatusUI[] statusUIs;
    private BottomMenu bottomMenu;
    private ActionsUI[] actionUIs;
    private Image onTileHover;


    public PlayerBattleHUD(Camera camera,Entity[] sortedUnits) {
    	initVariables(camera, sortedUnits);
    	createTileHoverParticle();
    	createActionUIs(sortedUnits);
    	createStatusUIs(sortedUnits);
    	createBottomMenu(sortedUnits);
        createPortraits(sortedUnits);
    }

	private void initVariables(Camera camera, Entity[] sortedUnits) {
		statusUIs = new StatusUI[sortedUnits.length];
    	actionUIs = new ActionsUI[sortedUnits.length];
        stage = new Stage(new ScreenViewport(camera));
        stage.setDebugAll(false);
	}
	
	private void createTileHoverParticle() {
		Utility.loadTextureAsset(Utility.ON_TILE_HOVER_FILE_PATH);
		TextureRegion tr = new TextureRegion(Utility.getTextureAsset(Utility.ON_TILE_HOVER_FILE_PATH));
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		onTileHover = new Image(trd);
		onTileHover.setPosition(-100, -100);
		
		stage.addActor(onTileHover);
	}
	
	private void createActionUIs(Entity[] sortedUnits) {
		for (int i = 0; i < sortedUnits.length; i++) {
			Entity entity = sortedUnits[i];
			actionUIs[i] = new ActionsUI(entity);
			ActionsUI actionui = actionUIs[i];
			
			actionui.addListener(new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}
			});
			
			stage.addActor(actionui);
		}
	}
	
	private void createStatusUIs(Entity[] sortedUnits) {
		for (int i = 0; i < sortedUnits.length; i++) {
			Entity entity = sortedUnits[i];
			statusUIs[i] = new StatusUI(entity);
			StatusUI statusui = statusUIs[i];
			
			statusui.addListener(new InputListener() {
				@Override
		    	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		     		return true;
		     	}
		    });
			
			stage.addActor(statusui);
		}
	}
	
	private void createBottomMenu(Entity[] sortedUnits) {
    	bottomMenu = new BottomMenu(sortedUnits);
    	bottomMenu.setHero(sortedUnits[0]);
    	
		stage.addActor(bottomMenu);
	}

	private void createPortraits(Entity[] sortedUnits) {
    	portraits = new PortraitsUI(sortedUnits);
    	
    	portraits.addListener(new InputListener() {
    		@Override
        	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
         		return true;
         	}
        });
    	
    	stage.addActor(portraits);
	}

    public Stage getStage() {
        return stage;
    }
    
	public Image getTileHoverImage() {
		return onTileHover;
	}
	
	public PortraitsUI getPortraits() {
		return portraits;
	}
	
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        portraits.updateSizeContainer();
        bottomMenu.update();
        updateStatusUIs();
        updateActionUIs();
        updateHoverParticle();
    }
    
    private void updateStatusUIs() {
    	for(StatusUI ui : statusUIs) {
    		ui.update();
    	}
    }
    
    private void updateActionUIs() {
    	for(ActionsUI ui : actionUIs) {
    		ui.update();
    	}
    }
    
    private void updateHoverParticle() {
    	onTileHover.setSize(Map.TILE_WIDTH_PIXEL, Map.TILE_HEIGHT_PIXEL);
    	onTileHover.getDrawable().setMinHeight(Map.TILE_HEIGHT_PIXEL);
    	onTileHover.getDrawable().setMinWidth(Map.TILE_WIDTH_PIXEL);
    }

    @Override
    public void onNotify(ProfileManager profileManager, ProfileEvent event) {

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
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
        stage.dispose();
    }

}

