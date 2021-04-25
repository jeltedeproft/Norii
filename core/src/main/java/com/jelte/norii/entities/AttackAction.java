package com.jelte.norii.entities;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.jelte.norii.magic.Ability.DamageType;

public class AttackAction extends Action {
	private final Entity entityToAttack;
	private final Entity attacker;

	public AttackAction(Entity attacker, Entity entityToAttack) {
		super();
		this.entityToAttack = entityToAttack;
		this.attacker = attacker;
	}

	@Override
	public boolean act(float delta) {
		if (target != null) {
			attacker.getVisualComponent().setAnimationType(EntityAnimationType.WALK);
			final EntityActor targetEntity = (EntityActor) target;
			targetEntity.getEntity().attack(entityToAttack, DamageType.PHYSICAL);
		}

		return true;
	}

}
