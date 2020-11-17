package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.testUI.ActionUIButton;

public class MoveActionUIButton extends ActionUIButton {
	public MoveActionUIButton(final String imageFileName, final PlayerEntity linkedUnit, int mapWidth, int mapHeight) {
		super(imageFileName);
		active = true;
		infotext = "use this button to move";
		actionName = "Move";
		initPopUp(mapWidth, mapHeight);

		button.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				if (linkedUnit.canMove()) {
					linkedUnit.setInMovementPhase(true);
				}
			}
		});
	}
}
