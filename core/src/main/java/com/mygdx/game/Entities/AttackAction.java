package com.mygdx.game.Entities;

import com.badlogic.gdx.scenes.scene2d.Action;

public class AttackAction extends Action {
	private final Entity entityToAttack;

	public AttackAction(Entity entityToAttack) {
		super();
		this.entityToAttack = entityToAttack;
	}

	@Override
	public boolean act(float delta) {
		if (target != null) {
			final EntityActor targetEntity = (EntityActor) target;
			targetEntity.getEntity().attack(entityToAttack);
		}

		return true;
	}

}
