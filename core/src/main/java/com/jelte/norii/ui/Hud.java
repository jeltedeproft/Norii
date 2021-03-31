package com.jelte.norii.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.profile.ProfileObserver;
import com.jelte.norii.screen.BattleScreen;
import com.jelte.norii.utility.AssetManagerUtility;

public class Hud implements ProfileObserver {
	private Stage stage;
	private Image onTileHover;

	private HashMap<Integer, HpBar> entityIdWithHpBar;
	private HashMap<Integer, StatusUi> entityIdWithStatusUi;
	private HashMap<Integer, ActionsUi> entityIdWithActionUi;
	private HashMap<Integer, List<ActionInfoUiWindow>> entityIdWithActionInfoUiWindows;
	private PortraitAndStats portraitAndStats;
	private HudMessages hudMessages;
	private BattleScreen battleScreen;

	private int mapWidth;
	private int mapHeight;

	private float tilePixelWidth;
	private float tilePixelHeight;

	private boolean locked;

	public static final float UI_VIEWPORT_WIDTH = 400f;
	public static final float UI_VIEWPORT_HEIGHT = 400f;

	public Hud(List<Entity> playerUnits, List<Entity> aiUnits, SpriteBatch spriteBatch, int mapWidth, int mapHeight, BattleScreen battleScreen, boolean isTutorial) {
		final List<Entity> allUnits = Stream.concat(playerUnits.stream(), aiUnits.stream()).collect(Collectors.toList());
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
		entityIdWithStatusUi = new HashMap<>();
		entityIdWithActionUi = new HashMap<>();
		entityIdWithActionInfoUiWindows = new HashMap<>();
		stage = new Stage(new FitViewport(UI_VIEWPORT_WIDTH, UI_VIEWPORT_HEIGHT), spriteBatch);
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
		createStatusUI(entity);
		if (entity.isPlayerUnit()) {
			createActionUI(entity);
		}
	}

	public void removeUnit(Entity entity) {
		final Integer id = entity.getEntityID();
		if (entityIdWithStatusUi.containsKey(id)) {
			entityIdWithStatusUi.get(id).setVisible(false);
			entityIdWithStatusUi.get(id).remove();
			entityIdWithStatusUi.remove(id);
		}

		if (entityIdWithHpBar.containsKey(id)) {
			entityIdWithHpBar.get(id).getHealthBar().setVisible(false);
			entityIdWithHpBar.get(id).getHealthBar().remove();
			entityIdWithHpBar.remove(id);
		}

		if (entityIdWithActionUi.containsKey(id)) {
			for (final ActionUIButton button : entityIdWithActionUi.get(id).getButtons()) {
				button.hide();
			}

			for (final ActionInfoUiWindow popup : entityIdWithActionUi.get(id).getPopUps()) {
				popup.setVisible(false);
			}
			entityIdWithActionUi.get(id).setVisible(false);
			entityIdWithActionUi.get(id).remove();
			entityIdWithActionUi.remove(id);
		}
	}

	private void createHpBar(Entity entity) {
		final HpBar hpBar = new HpBar(entity, mapWidth, mapHeight);
		stage.addActor(hpBar.getHealthBar());
		entityIdWithHpBar.put(entity.getEntityID(), hpBar);
	}

	private void createCharacterHUD() {
		portraitAndStats = new PortraitAndStats(mapWidth, mapHeight);
		stage.addActor(portraitAndStats.getTable());
	}

	private void createHudMessages(boolean isTutorial) {
		hudMessages = new HudMessages(stage, mapWidth, mapHeight, tilePixelWidth, tilePixelHeight, isTutorial);
	}

	private void createStatusUI(Entity entity) {
		final StatusUi statusui = new StatusUi(entity, mapWidth, mapHeight);
		entityIdWithStatusUi.put(entity.getEntityID(), statusui);

		statusui.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		stage.addActor(statusui);
	}

	private void createActionUI(Entity playerUnit) {
		final ActionsUi actionui = new ActionsUi(playerUnit, mapWidth, mapHeight, this);
		entityIdWithActionUi.put(playerUnit.getEntityID(), actionui);

		actionui.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		stage.addActor(actionui);
		entityIdWithActionInfoUiWindows.put(playerUnit.getEntityID(), actionui.getPopUps());
		for (final ActionInfoUiWindow popUp : actionui.getPopUps()) {
			stage.addActor(popUp);
		}
	}

	public void setPositionTileHover(int tileX, int tileY) {
		onTileHover.setPosition(tileX * tilePixelWidth, tileY * tilePixelHeight);
	}

	public void update(List<Entity> units) {
		for (final Entity entity : units) {
			final int id = entity.getEntityID();
			final HpBar bar = entityIdWithHpBar.get(id);
			final StatusUi status = entityIdWithStatusUi.get(id);
			final ActionsUi action = entityIdWithActionUi.get(id);
			final List<ActionInfoUiWindow> infos = entityIdWithActionInfoUiWindows.get(id);

			if (bar != null) {
				bar.update(entity);
			}
			if (status != null) {
				status.update(entity);
			}
			if (action != null) {
				action.update(entity);
			}
			if (infos != null) {
				for (final ActionInfoUiWindow info : infos) {
					info.update();
				}
			}

			if (entity.isStatsChanged()) {
				portraitAndStats.update(entity);
			}
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

	public Collection<ActionsUi> getActionUIs() {
		return entityIdWithActionUi.values();
	}

	public Collection<StatusUi> getStatusUIs() {
		return entityIdWithStatusUi.values();
	}

	public Map<Integer, HpBar> getEntityIdWithHpBar() {
		return entityIdWithHpBar;
	}

	public Map<Integer, StatusUi> getEntityIdWithStatusUi() {
		return entityIdWithStatusUi;
	}

	public Map<Integer, ActionsUi> getEntityIdWithActionUi() {
		return entityIdWithActionUi;
	}

	public Map<Integer, List<ActionInfoUiWindow>> getEntityIdWithActionInfoUiWindows() {
		return entityIdWithActionInfoUiWindows;
	}

	public void sendMessage(MessageToBattleScreen message, int entityID, Ability ability) {
		battleScreen.messageFromUi(message, entityID, ability);
	}

	public PortraitAndStats getPortraitAndStats() {
		return portraitAndStats;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void showActions(boolean show, Entity entity) {
		final int id = entity.getEntityID();
		final ActionsUi action = entityIdWithActionUi.get(id);
		action.setVisible(show);
	}

	public HudMessages getHudMessages() {
		return hudMessages;
	}
}
