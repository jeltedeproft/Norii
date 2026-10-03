package com.jelte.norii.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.jelte.norii.utility.AssetManagerUtility;

public class VictoryOverlay extends Table implements Disposable {
	private static final Color VICTORY_COLOR = new Color(0.95f, 0.78f, 0.28f, 1f);
	private static final Color DEFEAT_COLOR = new Color(0.86f, 0.32f, 0.24f, 1f);
	private static final Color SUBTITLE_COLOR = new Color(0.94f, 0.91f, 0.82f, 1f);

	private final Label titleLabel;
	private final Label subtitleLabel;
	private final TextButton continueButton;
	private final Texture bgTexture;
	private Runnable onContinue;

	public VictoryOverlay() {
		setFillParent(true);

		Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		bgPixmap.setColor(0f, 0f, 0f, 0.8f);
		bgPixmap.fill();
		bgTexture = new Texture(bgPixmap);
		bgPixmap.dispose();
		setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));

		titleLabel = new Label("", AssetManagerUtility.getSkin(), "bigFont");
		titleLabel.setAlignment(Align.center);

		subtitleLabel = new Label("", AssetManagerUtility.getSkin());
		subtitleLabel.setColor(SUBTITLE_COLOR);
		subtitleLabel.setAlignment(Align.center);
		subtitleLabel.setWrap(true);

		continueButton = new TextButton("Continue", AssetManagerUtility.getSkin(), "lumos30");
		continueButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (onContinue != null) {
					onContinue.run();
				}
				return true;
			}
		});

		add(titleLabel).padBottom(Hud.UI_VIEWPORT_HEIGHT * 0.025f).row();
		add(subtitleLabel).width(Hud.UI_VIEWPORT_WIDTH * 0.55f).padBottom(Hud.UI_VIEWPORT_HEIGHT * 0.055f).row();
		add(continueButton).width(Hud.UI_VIEWPORT_WIDTH * 0.22f).height(Hud.UI_VIEWPORT_HEIGHT * 0.07f);

		setVisible(false);
	}

	public void show(boolean playerWon, String subtitle, Runnable onContinue) {
		this.onContinue = onContinue;
		if (playerWon) {
			titleLabel.setText("VICTORY!");
			titleLabel.setColor(VICTORY_COLOR);
		} else {
			titleLabel.setText("DEFEAT");
			titleLabel.setColor(DEFEAT_COLOR);
		}
		subtitleLabel.setText(subtitle);
		setVisible(true);
		getColor().a = 0f;
		addAction(Actions.fadeIn(0.7f));
	}

	public void hide() {
		clearActions();
		setVisible(false);
	}

	@Override
	public void dispose() {
		bgTexture.dispose();
	}
}
