package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;

public class SpellImageButton extends ImageButton {

	private Entity entity;
	private Ability ability;

	public SpellImageButton(ImageButtonStyle style) {
		super(style);
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
	}

}
