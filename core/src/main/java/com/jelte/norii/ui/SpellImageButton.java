package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.AssetManagerUtility;

public class SpellImageButton extends TextButton {

	private Entity entity;
	private Ability ability;

	public SpellImageButton() {
		super("", AssetManagerUtility.getSkin(), "default");
	}

	public Entity getEntity() {
		return entity;
	}

	public Ability getAbility() {
		return ability;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public void setAbility(Ability ability) {
		this.ability = ability;
		this.setText(ability.getName());
	}

}
