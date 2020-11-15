package com.jelte.norii.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.jelte.norii.utility.AssetManagerUtility;

public class PauseMenuScreen implements Screen {
	private static final String PAUSE_BACKGROUND = "bg";

	private Stage stage;
	private SpriteBatch spriteBatch;
	private BattleScreen battleScreen;
	private Table menuTable;
	private Label title;
	private TextButton resumeButton;
	private TextButton settingButton;
	private TextButton mainMenuButton;

	private boolean isVisible;

	private static final int TITLE_FONT_SCALE = 2;
	private static final int BOTTOM_PAD = 10;
	private static final int TOP_PAD = 50;

	public PauseMenuScreen(Camera camera, BattleScreen battleScreen, SpriteBatch spriteBatch) {
		initVariables(camera, battleScreen, spriteBatch);
		createUI();
		createLayout();
		addListeners();
	}

	private void initVariables(Camera camera, BattleScreen battleScreen, SpriteBatch spriteBatch) {
		this.spriteBatch = spriteBatch;
		stage = new Stage(new ScreenViewport(camera), spriteBatch);
		menuTable = new Table();
		menuTable.setDebug(false);
		menuTable.setFillParent(true);
		this.battleScreen = battleScreen;
		this.setVisible(false);
	}

	private void createUI() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		title = new Label("Pause", statusUISkin);
		title.setFontScale(TITLE_FONT_SCALE);
		resumeButton = new TextButton("Resume", statusUISkin);
		settingButton = new TextButton("Settings", statusUISkin);
		mainMenuButton = new TextButton("Main Menu", statusUISkin);
	}

	private void createLayout() {

		menuTable.background(new TextureRegionDrawable(new TextureRegion(AssetManagerUtility.getSprite(PAUSE_BACKGROUND))));
		menuTable.row();
		menuTable.add(title).spaceBottom(BOTTOM_PAD).padTop(TOP_PAD).row();
		menuTable.add(resumeButton).spaceBottom(BOTTOM_PAD).row();
		menuTable.add(settingButton).spaceBottom(BOTTOM_PAD).row();
		menuTable.add(mainMenuButton).spaceBottom(BOTTOM_PAD).row();
		stage.addActor(menuTable);
	}

	private void addListeners() {
		resumeButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				setVisible(false);
				battleScreen.resume();
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
		return isVisible;
	}

	public void setVisible(boolean visible) {
		this.isVisible = visible;
		menuTable.setVisible(visible);
	}

	@Override
	public void show() {
		// no-op
	}

	@Override
	public void render(float delta) {
		if (isVisible) {
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
		// no-op
	}

	@Override
	public void resume() {
		// no-op
	}

	@Override
	public void hide() {
		// no-op
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
