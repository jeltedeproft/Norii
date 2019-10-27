package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Screen.ScreenEnum;
import com.mygdx.game.Screen.ScreenManager;

import Utility.Utility;


public class PauseMenuUI implements Screen {
	private static final String TAG = PauseMenuUI.class.getSimpleName();
	private static String defaultBackgroundPath = "maps/rpg/tiles/grass.png";
	
    private Stage _stage;
    private Viewport _viewport;
    private Camera _camera;
    private Table menuTable;
    private Label title;
	private TextButton resumeButton;
	private TextButton settingButton;
	private TextButton mainMenuButton;
	
	private boolean visible;

    public PauseMenuUI(Camera camera) {
        _camera = camera;
        _viewport = new ScreenViewport(_camera);
        _stage = new Stage(_viewport);
        menuTable = new Table();
		menuTable.setDebug(false);
		menuTable.setFillParent(true);
		
        createUI();
        createLayout();
        
        addListeners();
    }
    
    private void createUI() {        
    	title = new Label("Pause", Utility.STATUSUI_SKIN);
    	title.setFontScale(2);
        resumeButton = new TextButton("Resume",Utility.STATUSUI_SKIN);
        settingButton = new TextButton("Settings", Utility.STATUSUI_SKIN);
        mainMenuButton = new TextButton("Main Menu", Utility.STATUSUI_SKIN);
	}
    
	private void createLayout() {
		menuTable.background(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(defaultBackgroundPath)))));
		menuTable.row();
		menuTable.add(title).spaceBottom(10).padTop(50).row();
		menuTable.add(resumeButton).spaceBottom(10).row();
		menuTable.add(settingButton).spaceBottom(10).row();
		menuTable.add(mainMenuButton).spaceBottom(10).row();
		_stage.addActor(menuTable);
	}

	private void addListeners() {
		//Listeners
		resumeButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				setVisible(false);
				return true;
			}
		});
		settingButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		mainMenuButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
				return true;
			}
		});
	}
	
    public Stage getStage() {
        return _stage;
    }
    
    public boolean getVisible() {
    	return visible;
    }
    
    public void setVisible(boolean visible) {
    	this.visible = visible;
		menuTable.setVisible(visible);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
	    if(visible) {
	        _stage.act(delta);
	        _stage.draw();
    	}
    }

    @Override
    public void resize(int width, int height) {
    	Gdx.app.debug(TAG, "resizing with : (" + width + " , " + height + ")");
        _stage.getViewport().update(width, height, true);
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

