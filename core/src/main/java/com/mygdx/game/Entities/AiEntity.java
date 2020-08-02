package com.mygdx.game.Entities;

import com.mygdx.game.Entities.EntityObserver.EntityCommand;
import com.mygdx.game.Magic.Ability;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

public class AiEntity extends Entity {
	public AiEntity(EntityTypes type) {
		super(type);
		isPlayerUnit = false;
		ParticleMaker.addParticle(ParticleType.RED_CIRCLE, currentPlayerPosition, entityID);
		ringParticle = ParticleMaker.getParticle(ParticleType.RED_CIRCLE, currentPlayerPosition, entityID);
	}

	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}

	public void setInMovementPhase(final boolean isInMovementPhase) {
		if (isInMovementPhase) {
			notifyEntityObserver(EntityCommand.IN_MOVEMENT);
		}
	}

	public void setInAttackPhase(final boolean isInAttackPhase) {
		if (canAttack()) {
			this.isInAttackPhase = isInAttackPhase;
			if (isInAttackPhase) {
				notifyEntityObserver(EntityCommand.IN_ATTACK_PHASE);
			}
		}
	}

	public void setInSpellPhase(final boolean isInSpellPhase, final Ability ability) {
		if (isInSpellPhase) {
			notifyEntityObserver(EntityCommand.IN_SPELL_PHASE, ability);
		}
	}

	public void setInActionPhase(final boolean isInActionPhase) {
		notifyEntityObserver(EntityCommand.UNIT_ACTIVE);
	}

	@Override
	public void notifyEntityObserver(final EntityObserver.EntityCommand command) {
		for (int i = 0; i < entityObservers.size; i++) {
			entityObservers.get(i).onEntityNotify(command, this);
		}
	}

	@Override
	public String toString() {
		return "name : " + entityData.getName() + "   ID:" + entityID;
	}

}
