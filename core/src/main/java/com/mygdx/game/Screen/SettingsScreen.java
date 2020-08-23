package com.mygdx.game.Screen;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import Utility.Utility;

public class SettingsScreen implements Screen {
	private static final float FRAME_DURATION = 0.2f;

	private static String defaultBackgroundPath = "sprites/gui/gifs/forestbg.gif";

	private Label titleLabel;
	private Label settingsLabel;
	private SelectBox<String> aspectRatioSelectBox;
	private TextButton exit;
	private final Stage stage;
	private final Table table;
	private DisplayMode[] displayModes;
	private Animation<TextureRegion> bganimation;
	private SpriteBatch backgroundbatch;

	protected float frameTime = 0f;
	protected Sprite frameSprite = null;
	protected TextureRegion currentFrame = null;

	public SettingsScreen() {
		stage = new Stage();
		table = new Table();
		table.setFillParent(true);
		createBackground();
		createButtons();
		addButtons();
		addListeners();
	}

	private void createButtons() {
		final Skin statusUISkin = Utility.getSkin();

		titleLabel = new Label("SETTINGS", statusUISkin);
		titleLabel.setFontScale(3);
		titleLabel.setAlignment(Align.top);
		settingsLabel = new Label("aspect ratio", statusUISkin);

		final String[] displayModeStrings = getDisplayModeStrings();
		aspectRatioSelectBox = new SelectBox<String>(statusUISkin);
		aspectRatioSelectBox.setItems(displayModeStrings);

		exit = new TextButton("exit", statusUISkin);
	}

	private void createBackground() {
		backgroundbatch = new SpriteBatch();
		initializeBgAnimation();
	}

	private void initializeBgAnimation() {
		bganimation = Utility.getGIFAsset(defaultBackgroundPath);
		bganimation.setFrameDuration(FRAME_DURATION);
	}

	private String[] getDisplayModeStrings() {
		displayModes = getDisplayModes();
		final String[] displayModeStrings = new String[displayModes.length];
		for (int i = 0; i < displayModes.length; i++) {
			displayModeStrings[i] = displayModes[i].toString();
		}
		return displayModeStrings;
	}

	private DisplayMode[] getDisplayModes() {
		try {
			return Display.getAvailableDisplayModes();
		} catch (final LWJGLException e) {
			e.printStackTrace();
		}
		return new DisplayMode[0];
	}

	private void addButtons() {
		table.add(titleLabel).expandX().colspan(10).spaceBottom(100).height(250).width(1000).row();
		table.add(settingsLabel).height(75).width(200).row();
		table.add(settingsLabel).height(75).width(200).row();
		table.add(settingsLabel).height(75).width(200).row();
		table.add(settingsLabel).height(75).width(200);
		table.add(aspectRatioSelectBox).height(75).width(500).row();
		table.add(exit).spaceTop(100).height(100).width(100).row();

		stage.addActor(table);
	}

	private void addListeners() {
		exit.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
				return true;
			}
		});

		aspectRatioSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				@SuppressWarnings("unchecked")
				final SelectBox<String> selectBox = (SelectBox<String>) actor;
				final String displayMode = selectBox.getSelected();
				final String[] splitDisplays = displayMode.split("x");
				resize(Integer.parseInt(splitDisplays[0].trim()), Integer.parseInt(splitDisplays[1].trim()));
				Gdx.graphics.setWindowedMode(Integer.parseInt(splitDisplays[0].trim()), Integer.parseInt(splitDisplays[1].trim()));
			}
		});
	}

	@Override
	public void show() {
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updatebg(delta);
		stage.act(delta);
		stage.draw();
	}

	public void updatebg(final float delta) {
		frameTime = (frameTime + delta) % 70; // Want to avoid overflow

		currentFrame = bganimation.getKeyFrame(frameTime, true);
		backgroundbatch.begin();
		backgroundbatch.draw(currentFrame, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
		backgroundbatch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().setScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		table.setSize(width, height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
