package com.jelte.norii.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.ui.PixelViewport;
import com.jelte.norii.campaign.CampaignData;
import com.jelte.norii.campaign.CampaignFileReader;
import com.jelte.norii.campaign.CampaignManager;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class CampaignScreen extends GameScreen {

	private Stage stage;
	private SpriteBatch batch;
	private OrthographicCamera parallaxCamera;
	private ParallaxBackground parallaxBackground;
	private ShapeRenderer shapeRenderer;

	private Table infoPanel;
	private Label infoPanelChapter;
	private Label infoPanelTitle;
	private Label infoPanelSubtitle;
	private Label infoPanelPreview;
	private Label infoPanelReward;
	private TextButton playButton;

	private int selectedLevel = -1;
	private float totalTime = 0f;

	private static final Color COLOR_COMPLETED = new Color(0.45f, 0.58f, 0.40f, 1f);
	private static final Color COLOR_UNLOCKED = new Color(0.83f, 0.67f, 0.19f, 1f);
	private static final Color COLOR_LOCKED = new Color(0.45f, 0.44f, 0.52f, 0.7f);

	public CampaignScreen() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		stage = new Stage(new PixelViewport(), batch);
		parallaxCamera = new OrthographicCamera();
		parallaxCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		buildBackground();
		buildUI();
		// Select the first non-completed level by default
		int progress = CampaignManager.getInstance().getHighestCompletedLevel();
		selectLevel(Math.min(progress + 1, CampaignFileReader.getLevelCount() - 1));
	}

	private void buildBackground() {
		final TextureAtlas atlas = AssetManagerUtility.getTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);
		int h = Gdx.graphics.getHeight();
		parallaxBackground = new ParallaxBackground();
		parallaxBackground.addLayers(
				new TextureRegionParallaxLayer(atlas.findRegion("background-back-trees"), h, new Vector2(.15f, .15f), WH.HEIGHT),
				new TextureRegionParallaxLayer(atlas.findRegion("background-light"), h, new Vector2(.3f, .3f), WH.HEIGHT),
				new TextureRegionParallaxLayer(atlas.findRegion("background-middle-trees"), h, new Vector2(.4f, .4f), WH.HEIGHT),
				new TextureRegionParallaxLayer(atlas.findRegion("foreground"), h, new Vector2(.5f, .5f), WH.HEIGHT));
	}

	private void buildUI() {
		Skin skin = AssetManagerUtility.getSkin();

		Table root = new Table();
		root.setFillParent(true);
		root.pad(12);

		Label title = new Label("The Shadow\nChronicles", skin, "bigFont");
		title.setAlignment(Align.center);
		root.add(title).colspan(2).padBottom(10).row();

		Table levelsColumn = new Table();
		for (int i = 0; i < CampaignFileReader.getLevelCount(); i++) {
			levelsColumn.add(buildLevelButton(skin, i)).width(248).height(50).padBottom(4).row();
		}
		TextButton backButton = new TextButton("Main Menu", skin, "lumos30");
		backButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
				return true;
			}
		});
		levelsColumn.add(backButton).width(180).height(30).padTop(8);

		infoPanel = new Table();
		infoPanel.setBackground(skin.getDrawable("windowBackground10Patch"));
		infoPanel.pad(12);
		infoPanel.align(Align.top);

		infoPanelChapter = new Label("", skin, "default");
		infoPanelChapter.setColor(new Color(0.83f, 0.67f, 0.19f, 1f));
		infoPanelChapter.setAlignment(Align.center);

		infoPanelTitle = new Label("", skin, "default");
		infoPanelTitle.setAlignment(Align.center);
		infoPanelTitle.setWrap(true);

		infoPanelSubtitle = new Label("", skin, "default");
		infoPanelSubtitle.setColor(new Color(0.94f, 0.91f, 0.82f, 1f));
		infoPanelSubtitle.setAlignment(Align.center);
		infoPanelSubtitle.setWrap(true);

		Image divider = new Image(skin.getDrawable("rule"));

		infoPanelPreview = new Label("", skin, "default");
		infoPanelPreview.setWrap(true);
		infoPanelPreview.setAlignment(Align.topLeft);

		infoPanelReward = new Label("", skin, "default");
		infoPanelReward.setColor(new Color(0.45f, 0.58f, 0.40f, 1f));
		infoPanelReward.setAlignment(Align.center);

		playButton = new TextButton("Begin Battle", skin, "lumos30");
		playButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (selectedLevel >= 0 && CampaignManager.getInstance().isLevelUnlocked(selectedLevel)) {
					startLevel(selectedLevel);
				}
				return true;
			}
		});

		infoPanel.add(infoPanelChapter).padBottom(4).row();
		infoPanel.add(infoPanelTitle).width(168).padBottom(4).row();
		infoPanel.add(infoPanelSubtitle).width(168).padBottom(8).row();
		infoPanel.add(divider).width(150).height(4).padBottom(8).row();
		infoPanel.add(infoPanelPreview).width(168).height(120).padBottom(8).row();
		infoPanel.add(infoPanelReward).width(168).padBottom(8).row();
		infoPanel.add(playButton).width(160).height(32);

		root.add(levelsColumn).top().width(248);
		root.add(infoPanel).top().width(196).padLeft(8).expandY().fillY();

		stage.addActor(root);
	}

	private TextButton buildLevelButton(Skin skin, final int index) {
		CampaignData data = CampaignFileReader.getLevel(index);
		boolean completed = CampaignManager.getInstance().isLevelCompleted(index);
		boolean unlocked = CampaignManager.getInstance().isLevelUnlocked(index);

		String mark = completed ? "+  " : (unlocked ? ">  " : "x  ");
		String label = mark + data.getName();

		TextButton btn = new TextButton(label, skin, "lumos30");
		btn.getLabel().setWrap(true);
		btn.getLabel().setAlignment(Align.center);
		if (completed) {
			btn.getLabel().setColor(COLOR_COMPLETED);
		} else if (unlocked) {
			btn.getLabel().setColor(COLOR_UNLOCKED);
		} else {
			btn.getLabel().setColor(COLOR_LOCKED);
		}

		btn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (CampaignManager.getInstance().isLevelUnlocked(index)) {
					selectLevel(index);
				}
				return true;
			}
		});
		return btn;
	}

	private void selectLevel(int index) {
		selectedLevel = index;
		CampaignData data = CampaignFileReader.getLevel(index);
		infoPanelChapter.setText(data.getChapter());
		infoPanelTitle.setText(data.getName());
		infoPanelSubtitle.setText(ascii(data.getSubtitle()));
		// Show a preview of the intro text (first 200 chars)
		String intro = ascii(data.getIntroText());
		String preview = intro.length() > 180 ? intro.substring(0, 180) + "..." : intro;
		infoPanelPreview.setText(preview);
		// Reward info
		String reward = data.getRewardUnit();
		if (reward != null && !reward.isEmpty()) {
			infoPanelReward.setText("Victory reward: " + reward + " joins your roster");
		} else {
			infoPanelReward.setText("");
		}
		boolean completed = CampaignManager.getInstance().isLevelCompleted(index);
		playButton.setText(completed ? "Play Again" : "Begin Battle");
	}

	private static String ascii(String text) {
		if (text == null) {
			return "";
		}
		return text.replace('\u2014', '-').replace('\u2013', '-').replace('\u2026', '.');
	}

	private void startLevel(int index) {
		CampaignManager.getInstance().startCampaignBattle(index);
		AssetManagerUtility.loadMapAsset(com.jelte.norii.map.MapFactory.MapType.BATTLE_MAP_THE_DARK_SWAMP.toString());
		ScreenManager.getInstance().showScreen(ScreenEnum.STORY, index, StoryScreen.StoryType.INTRO);
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
		parallaxCamera.translate(delta * 30f, 0, 0);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(parallaxCamera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0.04f, 0.02f, 0.08f, 0.45f);
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
