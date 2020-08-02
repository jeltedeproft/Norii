
package com.mygdx.game.UI;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.Entities.AiEntity;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.Player;
import com.mygdx.game.Entities.PlayerEntity;
import com.mygdx.game.Profile.ProfileManager;
import com.mygdx.game.Profile.ProfileObserver;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

public class PlayerBattleHUD extends Table implements ProfileObserver {
	private Stage stage;
	private StatusUI[] statusUIs;
	private List<ActionInfoUIWindow> actionInfoUIWindows;
	private HPBar[] hpBars;
	private CharacterHud bottomMenu;
	private ActionsUI[] actionUIs;
	private Image onTileHover;

	public PlayerBattleHUD(Camera camera, List<PlayerEntity> playerUnits, List<AiEntity> aiUnits) {
		List<Entity> allUnits = ListUtils.union(playerUnits, aiUnits);
		initVariables(camera, allUnits);
		createTileHoverParticle();
		createCharacterHUDs(allUnits);
		createHPBars(allUnits);
		createActionUIs(playerUnits);
		createStatusUIs(allUnits);
		initializeActionPopUps();
	}

	private void initVariables(Camera camera, List<Entity> allUnits) {
		statusUIs = new StatusUI[allUnits.size()];
		actionUIs = new ActionsUI[Player.getInstance().getPlayerUnits().size()];
		hpBars = new HPBar[allUnits.size()];
		stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
	}

	private void createTileHoverParticle() {
		Utility.loadTextureAsset(Utility.ON_TILE_HOVER_FILE_PATH);
		final TextureRegion tr = new TextureRegion(Utility.getTextureAsset(Utility.ON_TILE_HOVER_FILE_PATH));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		onTileHover = new Image(trd);
		onTileHover.setPosition(-1, -1);

		stage.addActor(onTileHover);
	}

	private void createActionUIs(List<PlayerEntity> playerUnits) {
		for (int i = 0; i < playerUnits.size(); i++) {
			if (playerUnits.get(i).isPlayerUnit()) {
				final PlayerEntity entity = playerUnits.get(i);
				actionUIs[i] = new ActionsUI(entity);
				final ActionsUI actionui = actionUIs[i];

				actionui.addListener(new InputListener() {
					@Override
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
						return true;
					}
				});

				stage.addActor(actionui);
			}
		}
	}

	private void initializeActionPopUps() {
		actionInfoUIWindows = new ArrayList<>();
		for (final ActionsUI actionUI : actionUIs) {
			for (final ActionInfoUIWindow popUp : actionUI.getPopUps()) {
				actionInfoUIWindows.add(popUp);
				stage.addActor(popUp);
			}
		}
	}

	private void createStatusUIs(List<Entity> allUnits) {
		for (int i = 0; i < allUnits.size(); i++) {
			final Entity entity = allUnits.get(i);
			statusUIs[i] = new StatusUI(entity);
			final StatusUI statusui = statusUIs[i];

			statusui.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}
			});

			stage.addActor(statusui);
		}
	}

	private void createHPBars(List<Entity> allUnits) {
		for (int i = 0; i < allUnits.size(); i++) {
			final Entity entity = allUnits.get(i);
			hpBars[i] = new HPBar(entity);
			final HPBar hpBar = hpBars[i];
			stage.addActor(hpBar.getHpBarWidget());
		}
	}

	private void createCharacterHUDs(List<Entity> allUnits) {
		bottomMenu = new CharacterHud(allUnits);
		bottomMenu.setHero(allUnits.get(0));

		stage.addActor(bottomMenu);
	}

	public void update() {
		updateStatusUIs();
		updateActionUIs();
		updateHPBars();
		updateHoverParticle();
		updatePopUps();
	}

	@Override
	public Stage getStage() {
		return stage;
	}

	public Image getTileHoverImage() {
		return onTileHover;
	}

	public StatusUI[] getStatusuis() {
		return statusUIs;
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		bottomMenu.update();
		updateStatusUIs();
		updateActionUIs();
		updateHPBars();
		updateHoverParticle();
	}

	private void updateStatusUIs() {
		for (final StatusUI ui : statusUIs) {
			ui.update();
		}
	}

	private void updateActionUIs() {
		for (final ActionsUI ui : actionUIs) {
			ui.update();
		}
	}

	private void updateHPBars() {
		for (final HPBar hpBar : hpBars) {
			hpBar.update();
		}
	}

	private void updatePopUps() {
		for (final ActionInfoUIWindow popUp : actionInfoUIWindows) {
			popUp.update();
		}
	}

	private void updateHoverParticle() {
		final float tilePixelWidth = Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH;
		final float tilePixelHeight = Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT;
		onTileHover.setSize(tilePixelWidth, tilePixelHeight);
		onTileHover.getDrawable().setMinHeight(tilePixelHeight);
		onTileHover.getDrawable().setMinWidth(tilePixelWidth);
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
