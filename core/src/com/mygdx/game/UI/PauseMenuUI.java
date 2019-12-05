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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
	
    private Stage stage;
    private Viewport viewport;
    private Table menuTable;
    private Label title;
	private TextButton resumeButton;
	private TextButton settingButton;
	private TextButton mainMenuButton;
	
	private boolean visible;

    public PauseMenuUI(Camera camera) {
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);
        menuTable = new Table();
		menuTable.setDebug(false);
		menuTable.setFillParent(true);
		
        createUI();
        createLayout();
        
        addListeners();
    }
    
    private void createUI() {       
    	Skin statusUISkin = Utility.getStatusUISkin();
    	title = new Label("Pause", statusUISkin);
    	title.setFontScale(2);
        resumeButton = new TextButton("Resume",statusUISkin);
        settingButton = new TextButton("Settings", statusUISkin);
        mainMenuButton = new TextButton("Main Menu", statusUISkin);
	}
    
	private void createLayout() {
		menuTable.background(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(defaultBackgroundPath)))));
		menuTable.row();
		menuTable.add(title).spaceBottom(10).padTop(50).row();
		menuTable.add(resumeButton).spaceBottom(10).row();
		menuTable.add(settingButton).spaceBottom(10).row();
		menuTable.add(mainMenuButton).spaceBottom(10).row();
		stage.addActor(menuTable);
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
        return stage;
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
	        stage.act(delta);
	        stage.draw();
    	}
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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

