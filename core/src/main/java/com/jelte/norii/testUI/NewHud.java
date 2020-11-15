
package com.jelte.norii.testUI;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jelte.norii.entities.AiEntity;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.profile.ProfileObserver;
import com.jelte.norii.utility.AssetManagerUtility;

public class NewHud implements ProfileObserver {
	private Stage stage;
	private Image onTileHover;
	private ArrayList<HpBar> hpBars;
	private PortraitAndStats portraitAndStats;

	private final int mapWidth;
	private final int mapHeight;

	public static final int UI_VIEWPORT_WIDTH = 100;
	public static final int UI_VIEWPORT_HEIGHT = 100;

	public NewHud(List<PlayerEntity> playerUnits, List<AiEntity> aiUnits, SpriteBatch spriteBatch, int mapWidth, int mapHeight) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		final List<Entity> allUnits = Stream.concat(playerUnits.stream(), aiUnits.stream()).collect(Collectors.toList());
		initVariables(allUnits, spriteBatch);
		createTileHoverParticle();
		createHpBars(allUnits);
		createCharacterHUDs(allUnits);
	}

	private void initVariables(List<Entity> allUnits, SpriteBatch spriteBatch) {
		stage = new Stage(new FitViewport(UI_VIEWPORT_WIDTH, UI_VIEWPORT_HEIGHT), spriteBatch);
	}

	private void createTileHoverParticle() {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(AssetManagerUtility.TILE_HOVER_IMAGE));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		onTileHover = new Image(trd);
		onTileHover.setPosition(-1, -1);
		onTileHover.setSize(UI_VIEWPORT_WIDTH / mapWidth, UI_VIEWPORT_HEIGHT / mapHeight);
		onTileHover.getDrawable().setMinHeight(UI_VIEWPORT_HEIGHT / mapHeight);
		onTileHover.getDrawable().setMinWidth(UI_VIEWPORT_WIDTH / mapWidth);

		stage.addActor(onTileHover);
	}

	private void createHpBars(List<Entity> allUnits) {
		hpBars = new ArrayList<>();
		for (int i = 0; i < allUnits.size(); i++) {
			final Entity entity = allUnits.get(i);
			final HpBar hpBar = new HpBar(entity, mapWidth, mapHeight);

			stage.addActor(hpBar.getHealthBar());
			hpBars.add(hpBar);
		}
	}

	private void createCharacterHUDs(List<Entity> allUnits) {
		portraitAndStats = new PortraitAndStats(allUnits, mapWidth, mapHeight);
		portraitAndStats.setHero(allUnits.get(0));
		stage.addActor(portraitAndStats.getHorizontalGroup());
	}

	public void setPositionTileHover(int tileX, int tileY) {
		onTileHover.setPosition(tileX * (UI_VIEWPORT_WIDTH / mapWidth), tileY * (UI_VIEWPORT_HEIGHT / mapHeight));
	}

	public void update() {
		for (final HpBar bar : hpBars) {
			bar.getHealthBar().setPosition(bar.getEntity().getCurrentPosition().getTileX() * (UI_VIEWPORT_WIDTH / mapWidth), ((bar.getEntity().getCurrentPosition().getTileY() * (UI_VIEWPORT_HEIGHT / mapHeight)) + 3));
		}
	}

	public Stage getStage() {
		return stage;
	}

	public Image getTileHoverImage() {
		return onTileHover;
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	public void dispose() {
		stage.dispose();
	}

	@Override
	public void onNotify(ProfileManager profileManager, ProfileEvent event) {
		// no-op
	}

}
