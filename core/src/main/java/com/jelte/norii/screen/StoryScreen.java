package com.jelte.norii.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.ui.PixelViewport;
import com.jelte.norii.campaign.CampaignData;
import com.jelte.norii.campaign.CampaignFileReader;
import com.jelte.norii.campaign.CampaignManager;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.ai.AITeamLeader;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class StoryScreen extends GameScreen {
	public enum StoryType { INTRO, OUTRO }

	private final StoryType storyType;
	private final CampaignData level;

	private Stage stage;
	private SpriteBatch batch;
	private OrthographicCamera parallaxCamera;
	private ParallaxBackground parallaxBackground;
	private ShapeRenderer shapeRenderer;
	private float totalTime = 0f;

	public StoryScreen(int levelIndex, StoryType storyType) {
		this.storyType = storyType;
		this.level = CampaignFileReader.getLevel(levelIndex);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		stage = new Stage(new PixelViewport(), batch);
		parallaxCamera = new OrthographicCamera();
		parallaxCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		buildBackground();
		buildUI();
	}

	private void buildBackground() {
		final TextureAtlas atlas = AssetManagerUtility.getTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);
		int h = Gdx.graphics.getHeight();
		TextureRegionParallaxLayer back = new TextureRegionParallaxLayer(atlas.findRegion("background-back-trees"), h, new Vector2(.1f, .1f), WH.HEIGHT);
		TextureRegionParallaxLayer lights = new TextureRegionParallaxLayer(atlas.findRegion("background-light"), h, new Vector2(.2f, .2f), WH.HEIGHT);
		TextureRegionParallaxLayer mid = new TextureRegionParallaxLayer(atlas.findRegion("background-middle-trees"), h, new Vector2(.25f, .25f), WH.HEIGHT);
		parallaxBackground = new ParallaxBackground();
		parallaxBackground.addLayers(back, lights, mid);
	}

	private void buildUI() {
		Skin skin = AssetManagerUtility.getSkin();

		Table root = new Table();
		root.setFillParent(true);
		root.pad(18);
		root.setBackground(skin.getDrawable("windowBackground10Patch"));

		Label chapterLabel = new Label(level.getChapter(), skin, "default");
		chapterLabel.setColor(new Color(0.83f, 0.67f, 0.19f, 1f));
		chapterLabel.setAlignment(Align.center);

		Label titleLabel = new Label(ascii(level.getName()), skin, "bigFont");
		titleLabel.setAlignment(Align.center);
		titleLabel.setWrap(true);

		Label subtitleLabel = new Label(ascii(level.getSubtitle()), skin, "default");
		subtitleLabel.setColor(new Color(0.94f, 0.91f, 0.82f, 0.85f));
		subtitleLabel.setAlignment(Align.center);
		subtitleLabel.setWrap(true);

		String bodyText = storyType == StoryType.INTRO ? level.getIntroText() : level.getOutroText();
		Label storyLabel = new Label(ascii(bodyText), skin, "default");
		storyLabel.setWrap(true);
		storyLabel.setAlignment(Align.topLeft);

		ScrollPane scroll = new ScrollPane(storyLabel, skin);
		scroll.setScrollingDisabled(true, false);
		scroll.setFadeScrollBars(false);

		String buttonText = (storyType == StoryType.INTRO) ? "Battle!" : "Continue";
		TextButton continueButton = new TextButton(buttonText, skin, "lumos30");

		continueButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				onContinue();
				return true;
			}
		});

		root.add(chapterLabel).padBottom(4).row();
		root.add(titleLabel).width(400).padBottom(4).row();
		root.add(subtitleLabel).width(400).padBottom(10).row();
		root.add(scroll).width(400).height(220).padBottom(10).row();
		root.add(continueButton).width(180).height(32);

		stage.addActor(root);
	}

	private static String ascii(String text) {
		if (text == null) {
			return "";
		}
		return text.replace('\u2014', '-').replace('\u2013', '-').replace('\u2026', '.');
	}

	private void onContinue() {
		if (storyType == StoryType.INTRO) {
			// Load the battle
			int levelIndex = CampaignManager.getInstance().getActiveLevelIndex();
			AITeamLeader.LevelEnum enemyLevel = CampaignManager.getInstance().getLevelEnum(levelIndex);
			UnitOwner enemy = new AITeamLeader(enemyLevel);
			AssetManagerUtility.loadMapAsset(MapType.BATTLE_MAP_THE_DARK_SWAMP.toString());
			ScreenManager.getInstance().showScreen(ScreenEnum.LOAD, enemy, MapType.BATTLE_MAP_THE_DARK_SWAMP);
		} else {
			// Outro: go to campaign screen
			ScreenManager.getInstance().showScreen(ScreenEnum.CAMPAIGN);
		}
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		parallaxCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void render(float delta) {
		totalTime += delta;
		Gdx.gl.glClearColor(0.04f, 0.02f, 0.08f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		PixelViewport.beginFullScreen();

		batch.begin();
		parallaxBackground.draw(parallaxCamera, batch);
		batch.end();
		parallaxCamera.translate(delta * 20f, 0, 0);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(parallaxCamera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0.04f, 0.02f, 0.08f, 0.55f);
		shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		stage.getViewport().apply();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		parallaxCamera.setToOrtho(false, width, height);
	}

	@Override public void hide() { Gdx.input.setInputProcessor(null); }
	@Override public void pause() {}
	@Override public void resume() {}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		shapeRenderer.dispose();
	}
}
