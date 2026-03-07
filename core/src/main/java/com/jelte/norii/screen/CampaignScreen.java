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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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

	private static final Color COLOR_COMPLETED = new Color(0.2f, 0.75f, 0.3f, 1f);
	private static final Color COLOR_UNLOCKED = new Color(0.85f, 0.75f, 0.2f, 1f);
	private static final Color COLOR_LOCKED = new Color(0.4f, 0.4f, 0.5f, 0.7f);

	public CampaignScreen() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		stage = new Stage(new ScreenViewport(), batch);
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
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		Table root = new Table();
		root.setFillParent(true);
		root.pad(h * 0.04f);

		// Title
		Label title = new Label("The Shadow Chronicles", skin, "bigFont");
		title.setAlignment(Align.center);
		root.add(title).colspan(2).padBottom(h * 0.03f).row();

		// Left column: level nodes
		Table levelsColumn = new Table();
		for (int i = 0; i < CampaignFileReader.getLevelCount(); i++) {
			levelsColumn.add(buildLevelButton(skin, i)).width(w * 0.32f).height(h * 0.09f).padBottom(h * 0.012f).row();
		}
		// Back button
		TextButton backButton = new TextButton("< Main Menu", skin, "lumos30");
		backButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
				return true;
			}
		});
		levelsColumn.add(backButton).width(w * 0.22f).height(h * 0.06f).padTop(h * 0.02f);

		// Right column: info panel
		infoPanel = new Table();
		infoPanel.setBackground(skin.getDrawable("windowBackground10Patch"));
		infoPanel.pad(h * 0.025f);
		infoPanel.align(Align.top);

		infoPanelChapter = new Label("", skin, "default");
		infoPanelChapter.setColor(new Color(0.8f, 0.7f, 0.2f, 1f));
		infoPanelChapter.setAlignment(Align.center);

		infoPanelTitle = new Label("", skin, "bigFont");
		infoPanelTitle.setAlignment(Align.center);
		infoPanelTitle.setWrap(true);

		infoPanelSubtitle = new Label("", skin, "default");
		infoPanelSubtitle.setColor(new Color(0.75f, 0.75f, 0.85f, 1f));
		infoPanelSubtitle.setAlignment(Align.center);
		infoPanelSubtitle.setWrap(true);

		Label divider = new Label("━━━━━━━━━━━━━━━━━", skin, "default");
		divider.setColor(new Color(0.4f, 0.35f, 0.2f, 0.8f));
		divider.setAlignment(Align.center);

		infoPanelPreview = new Label("", skin, "default");
		infoPanelPreview.setWrap(true);
		infoPanelPreview.setAlignment(Align.topLeft);

		infoPanelReward = new Label("", skin, "default");
		infoPanelReward.setColor(new Color(0.6f, 0.9f, 0.5f, 1f));
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

		infoPanel.add(infoPanelChapter).padBottom(h * 0.005f).row();
		infoPanel.add(infoPanelTitle).width(w * 0.38f).padBottom(h * 0.005f).row();
		infoPanel.add(infoPanelSubtitle).width(w * 0.36f).padBottom(h * 0.012f).row();
		infoPanel.add(divider).padBottom(h * 0.015f).row();
		infoPanel.add(infoPanelPreview).width(w * 0.36f).height(h * 0.28f).padBottom(h * 0.015f).row();
		infoPanel.add(infoPanelReward).width(w * 0.36f).padBottom(h * 0.02f).row();
		infoPanel.add(playButton).width(w * 0.22f).height(h * 0.07f);

		root.add(levelsColumn).top().width(w * 0.35f);
		root.add(infoPanel).top().width(w * 0.42f).padLeft(w * 0.04f);

		stage.addActor(root);
	}

	private TextButton buildLevelButton(Skin skin, final int index) {
		CampaignData data = CampaignFileReader.getLevel(index);
		boolean completed = CampaignManager.getInstance().isLevelCompleted(index);
		boolean unlocked = CampaignManager.getInstance().isLevelUnlocked(index);

		String prefix = completed ? "[✓] " : (unlocked ? "[ ] " : "[🔒] ");
		String label = prefix + data.getChapter() + ": " + data.getName();

		TextButton btn = new TextButton(label, skin, "lumos30");
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
		infoPanelSubtitle.setText(data.getSubtitle());
		// Show a preview of the intro text (first 200 chars)
		String intro = data.getIntroText();
		String preview = intro.length() > 220 ? intro.substring(0, 220) + "..." : intro;
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

		batch.begin();
		parallaxBackground.draw(parallaxCamera, batch);
		batch.end();
		parallaxCamera.translate(delta * 30f, 0, 0);

		// Dark overlay
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0f, 0f, 0f, 0.45f);
		shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

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
