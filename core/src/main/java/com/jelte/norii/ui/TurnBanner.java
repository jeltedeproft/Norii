package com.jelte.norii.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.jelte.norii.utility.AssetManagerUtility;

public class TurnBanner extends Table implements Disposable {
	private static final Color PLAYER_TURN_COLOR = new Color(0.83f, 0.67f, 0.19f, 1f);
	private static final Color ENEMY_TURN_COLOR = new Color(0.86f, 0.32f, 0.24f, 1f);
	private static final float DISPLAY_DURATION = 1.5f;
	private static final float BANNER_HEIGHT = Hud.UI_VIEWPORT_HEIGHT * 0.11f;
	private static final float BANNER_Y = (Hud.UI_VIEWPORT_HEIGHT - BANNER_HEIGHT) * 0.5f;

	private final Label bannerLabel;
	private final Texture bgTexture;

	public TurnBanner() {
		Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		bgPixmap.setColor(0.04f, 0.02f, 0.08f, 0.88f);
		bgPixmap.fill();
		bgTexture = new Texture(bgPixmap);
		bgPixmap.dispose();
		setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));

		bannerLabel = new Label("", AssetManagerUtility.getSkin(), "bigFont");
		bannerLabel.setAlignment(Align.center);
		add(bannerLabel).expand().center();

		setSize(Hud.UI_VIEWPORT_WIDTH, BANNER_HEIGHT);
		setPosition(0, BANNER_Y);
		setVisible(false);
	}

	public void showPlayerTurn() {
		show("PLAYER TURN", PLAYER_TURN_COLOR);
	}

	public void showEnemyTurn() {
		show("ENEMY TURN", ENEMY_TURN_COLOR);
	}

	private void show(String text, Color color) {
		clearActions();
		bannerLabel.setText(text);
		bannerLabel.setColor(color);
		setVisible(true);
		getColor().a = 0f;
		addAction(Actions.sequence(
				Actions.fadeIn(0.3f),
				Actions.delay(DISPLAY_DURATION),
				Actions.fadeOut(0.4f),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						setVisible(false);
					}
				})));
	}

	@Override
	public void dispose() {
		bgTexture.dispose();
	}
}
