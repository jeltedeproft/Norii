package com.jelte.norii.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.ui.PixelViewport;
import com.jelte.norii.utility.AssetManagerUtility;

public class PauseMenuScreen extends GameScreen {
	private Stage stage;
	private BattleScreen battleScreen;
	private Table menuTable;
	private Label title;
	private TextButton resumeButton;
	private TextButton settingButton;
	private TextButton mainMenuButton;

	private boolean isVisible;

	public PauseMenuScreen(BattleScreen battleScreen, SpriteBatch spriteBatch) {
		initVariables(battleScreen, spriteBatch);
		createUI();
		createLayout();
		addListeners();
	}

	private void initVariables(BattleScreen battleScreen, SpriteBatch spriteBatch) {
		stage = new Stage(new PixelViewport(), spriteBatch);
		menuTable = new Table();
		menuTable.setDebug(false);
		menuTable.setFillParent(true);
		this.battleScreen = battleScreen;
		this.setVisible(false);
	}

	private void createUI() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		title = new Label("Paused", statusUISkin, "bigFont");
		title.setAlignment(Align.center);
		resumeButton = new TextButton("Resume", statusUISkin);
		settingButton = new TextButton("Settings", statusUISkin);
		mainMenuButton = new TextButton("Main Menu", statusUISkin);
	}

	private void createLayout() {
		Table panel = new Table();
		panel.setBackground(AssetManagerUtility.getSkin().getDrawable("windowBackground10Patch"));
		panel.pad(18, 22, 18, 22);
		panel.add(title).padBottom(12).row();
		panel.add(resumeButton).width(200).height(32).padBottom(6).row();
		panel.add(settingButton).width(200).height(32).padBottom(6).row();
		panel.add(mainMenuButton).width(200).height(32);
		menuTable.add(panel);
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
