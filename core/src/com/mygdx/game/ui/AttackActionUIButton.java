package com.mygdx.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.entities.PlayerEntity;

public class AttackActionUIButton extends ActionUIButton {
	public AttackActionUIButton(final String imageFileName, final PlayerEntity linkedUnit) {
		super(imageFileName);
		active = true;
		infotext = "use this button to attack";
		actionName = "Attack";
		initPopUp();

		button.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				if (linkedUnit.getAp() >= linkedUnit.getEntityData().getBasicAttackCost()) {
					linkedUnit.setInAttackPhase(true);
				}
			}
		});
	}

}
