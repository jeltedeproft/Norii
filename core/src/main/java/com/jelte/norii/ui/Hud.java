package com.jelte.norii.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jelte.norii.ai.EnemyType;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.screen.BattleScreen;
import com.jelte.norii.utility.AssetManagerUtility;

public class Hud {
	private Stage stage;
	private Image onTileHover;

	private HashMap<Integer, HpBar> entityIdWithHpBar;
	private BottomBar portraitAndStats;
	private ApBar apIndicator;
	private HudMessages hudMessages;
	private BattleScreen battleScreen;

	private int mapWidth;
	private int mapHeight;

	private float tilePixelWidth;
	private float tilePixelHeight;

	private boolean locked;
	private boolean isTutorial;
	private boolean isOnline;

	public static final float UI_VIEWPORT_WIDTH = 1000;
	public static final float UI_VIEWPORT_HEIGHT = 1000;

	public Hud(List<Entity> playerUnits, List<Entity> aiUnits, SpriteBatch spriteBatch, int mapWidth, int mapHeight, BattleScreen battleScreen, EnemyType enemyType) {
		isOnline = enemyType == EnemyType.ONLINE_PLAYER;
		List<Entity> allUnits = isOnline	? playerUnits
											: Stream.concat(playerUnits.stream(), aiUnits.stream()).collect(Collectors.toList());

		initVariables(spriteBatch, mapWidth, mapHeight, battleScreen);
		createTileHoverParticle();
		createCharacterHUD();
		for (final Entity entity : allUnits) {
			addUnit(entity);
		}
		createHudMessages(isTutorial);

	}

	private void initVariables(SpriteBatch spriteBatch, int mapWidth, int mapHeight, BattleScreen battleScreen) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.battleScreen = battleScreen;
		tilePixelWidth = UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = UI_VIEWPORT_HEIGHT / mapHeight;
		entityIdWithHpBar = new HashMap<>();
		stage = new Stage(new ExtendViewport(UI_VIEWPORT_WIDTH, UI_VIEWPORT_HEIGHT), spriteBatch);
	}

	private void createTileHoverParticle() {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(AssetManagerUtility.TILE_HOVER_IMAGE));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		onTileHover = new Image(trd);
		onTileHover.setPosition(-1, -1);
		onTileHover.setSize(tilePixelWidth, tilePixelHeight);
		onTileHover.getDrawable().setMinHeight(tilePixelHeight);
		onTileHover.getDrawable().setMinWidth(tilePixelWidth);

		stage.addActor(onTileHover);
	}

	public void addUnit(Entity entity) {
		createHpBar(entity);
		portraitAndStats.setHero(entity);
	}

	public void removeUnit(Entity entity) {
		final Integer id = entity.getEntityID();
		if (entityIdWithHpBar.containsKey(id)) {
			entityIdWithHpBar.get(id).getHealthBar().setVisible(false);
			entityIdWithHpBar.get(id).getHealthBar().remove();
			entityIdWithHpBar.remove(id);
		}
	}

	private void createHpBar(Entity entity) {
		final HpBar hpBar = new HpBar(entity, mapWidth, mapHeight);
		stage.addActor(hpBar.getHealthBar());
		entityIdWithHpBar.put(entity.getEntityID(), hpBar);
	}

	private void createCharacterHUD() {
		portraitAndStats = new BottomBar(mapWidth, mapHeight, this);
		stage.addActor(portraitAndStats.getTable());

		apIndicator = new ApBar(mapWidth, mapHeight);
		stage.addActor(apIndicator.getTable());
	}

	private void createHudMessages(boolean isTutorial) {
		hudMessages = new HudMessages(stage, mapWidth, mapHeight, tilePixelWidth, tilePixelHeight, isTutorial);
	}

	public void setPositionTileHover(int tileX, int tileY) {
		onTileHover.setPosition(tileX * tilePixelWidth, tileY * tilePixelHeight);
	}

	public void update(List<Entity> units) {
		for (final Entity entity : units) {
			final int id = entity.getEntityID();
			final HpBar bar = entityIdWithHpBar.get(id);

			if (bar != null) {
				bar.update(entity);
			}
		}
	}

	public void updateApBar(int currentAp) {
		apIndicator.update(currentAp);
	}

	public void updateBottomBar(Entity entity) {
		portraitAndStats.update(entity);
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

	public void render() {
		stage.draw();
	}

	public void dispose() {
		stage.dispose();
	}

	public Map<Integer, HpBar> getEntityIdWithHpBar() {
		return entityIdWithHpBar;
	}

	public void sendMessage(MessageToBattleScreen message, int entityID, Ability ability) {
		battleScreen.messageFromUi(message, entityID, ability);
	}

	public BottomBar getPortraitAndStats() {
		return portraitAndStats;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public HudMessages getHudMessages() {
		return hudMessages;
	}

	public boolean isTutorial() {
		return isTutorial;
	}

	public void setTutorial(boolean isTutorial) {
		this.isTutorial = isTutorial;
	}
}
