package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Entities.Entity;

public class MoveActionUIButton extends ActionUIButton {
	public MoveActionUIButton(final String imageFileName, final Entity linkedUnit) {
		super(imageFileName);
		active = true;

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
