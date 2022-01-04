package com.jelte.norii.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.map.BattleMap;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.map.MapManager;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.ui.LoadingBar;
import com.jelte.norii.utility.AssetManagerUtility;

/**
 * @author Mats Svensson
 */
public class LoadingScreen extends GameScreen {

	private Stage stage;

	private Image logo;
	private Image loadingFrame;
	private Image loadingBarHidden;
	private Image screenBg;
	private Image loadingBg;

	private float startX;
	private float endX;
	private float percent;

	private Actor loadingBar;
	private UnitOwner unitOwner;
	private MapType mapType;

	private MapManager mapMgr;
	private BattleMap currentMap;

	public LoadingScreen(UnitOwner unitOwner, MapType mapType) {
		this.unitOwner = unitOwner;
		this.mapType = mapType;
		mapMgr = new MapManager();
		mapMgr.loadMap(mapType);
		currentMap = (BattleMap) mapMgr.getCurrentMap();
		MyPathFinder.getInstance().setMap(currentMap);
	}

	@Override
	public void show() {
		// Initialize the stage where we will place everything
		stage = new Stage();

		// Get our textureatlas from the manager
		final TextureAtlas atlas = AssetManagerUtility.getTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);

		// Grab the regions from the atlas and create some images
		logo = new Image(atlas.findRegion("libgdx-logo"));
		loadingFrame = new Image(atlas.findRegion("loading-frame"));
		loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
		screenBg = new Image(atlas.findRegion("screen-bg"));
		loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

		// Add the loading bar animation
		Animation<TextureRegion> anim = new Animation<>(0.05f, atlas.findRegions("loading-bar-anim"));
		anim.setPlayMode(PlayMode.LOOP_REVERSED);
		loadingBar = new LoadingBar(anim);

		// Or if you only need a static bar, you can do
		// loadingBar = new Image(atlas.findRegion("loading-bar1"));

		// Add all the actors to the stage
		stage.addActor(screenBg);
		stage.addActor(loadingBar);
		stage.addActor(loadingBg);
		stage.addActor(loadingBarHidden);
		stage.addActor(loadingFrame);
		stage.addActor(logo);

		// Add everything to be loaded, for instance:
		// game.manager.load("data/assets1.pack", TextureAtlas.class);
		// game.manager.load("data/assets2.pack", TextureAtlas.class);
		// game.manager.load("data/assets3.pack", TextureAtlas.class);
	}

	@Override
	public void resize(int width, int height) {
		// Make the background fill the screen
		screenBg.setSize(stage.getWidth(), stage.getHeight());

		// Place the loading frame in the middle of the screen
		loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
		loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

		// Place the loading bar at the same spot as the frame, adjusted a few px
		loadingBar.setX(loadingFrame.getX() + 15);
		loadingBar.setY(loadingFrame.getY() + 5);

		// Place the logo in the middle of the screen
		logo.setX((stage.getWidth() - logo.getWidth()) / 2);
		logo.setY(loadingFrame.getY() + loadingFrame.getHeight() + 15);

		// Place the image that will hide the bar on top of the bar, adjusted a few px
		loadingBarHidden.setX(loadingBar.getX() + 35);
		loadingBarHidden.setY(loadingBar.getY() - 3);
		// The start position and how far to move the hidden loading bar
		startX = loadingBarHidden.getX();
		endX = 440;

		// The rest of the hidden bar
		loadingBg.setSize(450, 50);
		loadingBg.setX(loadingBarHidden.getX() + 30);
		loadingBg.setY(loadingBarHidden.getY() + 3);
	}

	@Override
	public void render(float delta) {
		// Clear the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (!MyPathFinder.getInstance().isPreprocessingFinished()) {
			MyPathFinder.getInstance().preprocessMap();
		} else {
			loadLevelAssets();
			ScreenManager.getInstance().showScreen(ScreenEnum.BATTLE, unitOwner, mapType, mapMgr);
		}

		// Interpolate the percentage to make it more smooth
		System.out.println("progress = " + MyPathFinder.getInstance().getPreprocessingMapProgress());
		percent = Interpolation.linear.apply(percent, MyPathFinder.getInstance().getPreprocessingMapProgress(), 0.1f);

		// Update positions (and size) to match the percentage
		loadingBarHidden.setX(startX + (endX * percent));
		loadingBg.setX(loadingBarHidden.getX() + 30);
		loadingBg.setWidth(450 - (450 * percent));
		loadingBg.invalidate();

		// Show the loading screen
		stage.act();
		stage.draw();
	}

	private void loadLevelAssets() {
		AssetManagerUtility.loadMapAsset(MapType.BATTLE_MAP_THE_DARK_SWAMP.toString());
	}

	@Override
	public void hide() {
		// no-op
	}
}