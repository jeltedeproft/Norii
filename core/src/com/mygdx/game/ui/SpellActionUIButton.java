package com.mygdx.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.entities.PlayerEntity;
import com.mygdx.game.magic.Ability;

public class SpellActionUIButton extends ActionUIButton {
	private final Ability ability;

	public SpellActionUIButton(final String imageName, final PlayerEntity linkedUnit, final Ability ability) {
		super(imageName);
		active = true;
		this.ability = ability;
		infotext = ability.getSpellInfo();
		actionName = ability.getName();
		initPopUp();

		button.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				if (linkedUnit.getAp() >= ability.getSpellData().getApCost()) {
					linkedUnit.setInSpellPhase(true, ability);
				}
			}
		});
	}
}