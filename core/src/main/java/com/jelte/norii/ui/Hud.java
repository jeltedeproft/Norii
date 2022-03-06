package com.jelte.norii.ui;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

	private BottomBar portraitAndStats;
	private ApBar apIndicator;
	private HudMessages hudMessages;
	private BattleScreen battleScreen;

	private float tilePixelWidth;
	private float tilePixelHeight;

	private boolean locked;
	private boolean isTutorial;
	private boolean isOnline;

	public static final float UI_VIEWPORT_WIDTH = 1000;
	public static final float UI_VIEWPORT_HEIGHT = 1000;

	public Hud(List<Entity> playerUnits, List<Entity> aiUnits, SpriteBatch spriteBatch, BattleScreen battleScreen, EnemyType enemyType) {
		isOnline = enemyType == EnemyType.ONLINE_PLAYER;
		List<Entity> allUnits = isOnline ? playerUnits : Stream.concat(playerUnits.stream(), aiUnits.stream()).collect(Collectors.toList());

		initVariables(spriteBatch, battleScreen);
		createTileHoverParticle();
		createCharacterHUD();
		for (final Entity entity : allUnits) {
			addUnit(entity);
		}
		createHudMessages(isTutorial);
	}

	private void initVariables(SpriteBatch spriteBatch, BattleScreen battleScreen) {
		this.battleScreen = battleScreen;
		tilePixelWidth = UI_VIEWPORT_WIDTH / BattleScreen.VISIBLE_WIDTH;
		tilePixelHeight = UI_VIEWPORT_HEIGHT / BattleScreen.VISIBLE_HEIGHT;
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
		portraitAndStats.setHero(entity);
	}

	private void createCharacterHUD() {
		Table bottom = new Table();
		portraitAndStats = new BottomBar(this);
		bottom.add(portraitAndStats);
		wrapTableAndAddToStage(bottom).bottom().left().expand();

		Table topLeft = new Table();
		apIndicator = new ApBar();
		topLeft.add(apIndicator);
		wrapTableAndAddToStage(topLeft).top().left().expand();
	}

	private Cell<Table> wrapTableAndAddToStage(Table table) {
		Table root = new Table();
		root.setFillParent(true);

		stage.addActor(root);

		return root.add(table);
	}

	private void createHudMessages(boolean isTutorial) {
		hudMessages = new HudMessages(stage, isTutorial);
	}

	public void setPositionTileHover(int viewportTileX, int viewportTileY, float xDifference, float yDifference) {
		onTileHover.setPosition(tilePixelWidth * (viewportTileX - xDifference), tilePixelWidth * (viewportTileY - yDifference));
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

	public void render() {
		stage.draw();
	}

	public void dispose() {
		stage.dispose();
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
