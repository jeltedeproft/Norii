
package com.mygdx.game.UI;

import java.util.ArrayList;

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
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.Player;
import com.mygdx.game.Profile.ProfileManager;
import com.mygdx.game.Profile.ProfileObserver;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

public class PlayerBattleHUD extends Table implements ProfileObserver {
	private Stage stage;
	private PortraitsUI portraits;
	private StatusUI[] statusUIs;
	private ArrayList<ActionInfoUIWindow> actionInfoUIWindows;
	private HPBar[] hpBars;
	private CharacterHud bottomMenu;
	private ActionsUI[] actionUIs;

	private Image onTileHover;

	public PlayerBattleHUD(Camera camera, Entity[] sortedUnits) {
		initVariables(camera, sortedUnits);
		createTileHoverParticle();
		createBottomMenu(sortedUnits);
		createHPBars(sortedUnits);
		createActionUIs(sortedUnits);
		initializeActionPopUps();
		createStatusUIs(sortedUnits);
	}

	private void initVariables(Camera camera, Entity[] sortedUnits) {
		statusUIs = new StatusUI[sortedUnits.length];
		actionUIs = new ActionsUI[Player.getInstance().getUnitsSortedByIni().length];
		hpBars = new HPBar[sortedUnits.length];
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

	private void createActionUIs(Entity[] sortedUnits) {
		for (int i = 0; i < sortedUnits.length; i++) {
			if (sortedUnits[i].isPlayerUnit()) {
				final Entity entity = sortedUnits[i];
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
		actionInfoUIWindows = new ArrayList<ActionInfoUIWindow>();
		for (final ActionsUI actionUI : actionUIs) {
			for (final ActionInfoUIWindow popUp : actionUI.getPopUps()) {
				actionInfoUIWindows.add(popUp);
				stage.addActor(popUp);
			}
		}
	}

	private void createStatusUIs(Entity[] sortedUnits) {
		for (int i = 0; i < sortedUnits.length; i++) {
			final Entity entity = sortedUnits[i];
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

	private void createHPBars(Entity[] sortedUnits) {
		for (int i = 0; i < sortedUnits.length; i++) {
			final Entity entity = sortedUnits[i];
			hpBars[i] = new HPBar(entity);
			final HPBar hpBar = hpBars[i];
			stage.addActor(hpBar.getHpBarWidget());
		}
	}

	private void createBottomMenu(Entity[] sortedUnits) {
		bottomMenu = new CharacterHud(sortedUnits);
		bottomMenu.setHero(sortedUnits[0]);

		stage.addActor(bottomMenu);
	}

	private void createPortraits(Entity[] sortedUnits) {
		portraits = new PortraitsUI(sortedUnits);

		portraits.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;

			}
		});

		stage.addActor(portraits);
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

	public PortraitsUI getPortraits() {
		return portraits;
	}

	public StatusUI[] getStatusuis() {
		return statusUIs;
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		// portraits.updateSizeContainer();
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

	}
}
