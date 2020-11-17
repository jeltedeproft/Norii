package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.jelte.norii.utility.AssetManagerUtility;

public abstract class UIWindow extends Window {
	protected float tileWidthPixel;
	protected float tileHeightPixel;

	protected static final int ALPHA = 80;

	protected UIWindow(String name, float width, float height) {
		super(name, AssetManagerUtility.getSkin());

		initVariables(width, height);
	}

	protected void initVariables(float width, float height) {
		this.setSize(width, height);
		for (final Cell cell : this.getCells()) {
			cell.setActorBounds(5, 5, 1, 1);
		}
	}

	public void update() {
		if (this.isVisible()) {
			updatePos();
			updateSize();
		}
	}

	protected void updateSize() {
		for (final Actor actor : this.getChildren()) {
			if (actor.getClass() == Label.class) {
				updateSizeLabels(actor);
			}

			if (actor.getClass() == ImageButton.class) {
				updateSizeImageButtons(actor);
			}
		}
	}

	private void updateSizeLabels(Actor actor) {
		final Label label = (Label) actor;
		label.setFontScale(1, 1);
	}

	private void updateSizeImageButtons(Actor actor) {
		final ImageButton button = (ImageButton) actor;
		if (button.getImage() != null) {
			final Cell<Actor> cell = this.getCell(actor);
			cell.size(1, 1);
			button.setBounds(10, 10, 1, 1);
		}
	}

	protected abstract void configureMainWindow();

	protected abstract void createWidgets();

	protected abstract void addWidgets();

	protected abstract void updatePos();
}
