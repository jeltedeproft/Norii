package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.AssetManagerUtility;

public class SpellActionUIButton extends ActionUIButton {
	private final Ability ability;
	private final String spellInfoText;

	public SpellActionUIButton(final String imageName, final PlayerEntity linkedUnit, final Ability ability, int mapWidth, int mapHeight) {
		super(imageName);
		active = true;
		this.ability = ability;
		spellInfoText = ability.getSpellInfo();
		actionName = ability.getName();
		labels.add(new Label(spellInfoText, AssetManagerUtility.getSkin()));
		initPopUp(mapWidth, mapHeight);

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
