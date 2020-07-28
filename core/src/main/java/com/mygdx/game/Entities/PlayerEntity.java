package com.mygdx.game.Entities;

import com.mygdx.game.Entities.EntityObserver.EntityCommand;
import com.mygdx.game.Magic.Ability;
import com.mygdx.game.Magic.ModifiersEnum;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;
import com.mygdx.game.UI.ActionsUI;

public class PlayerEntity extends Entity {

	private ActionsUI actionsui;

	public PlayerEntity(EntityTypes type) {
		super(type);
		isPlayerUnit = true;
		ParticleMaker.addParticle(ParticleType.BLUE_CIRCLE, currentPlayerPosition);
		ringParticle = ParticleMaker.getParticle(ParticleType.BLUE_CIRCLE, currentPlayerPosition);
	}

	public ActionsUI getActionsui() {
		return actionsui;
	}

	public void setActionsui(final ActionsUI actionsui) {
		this.actionsui = actionsui;
	}

	public void setActive(final boolean isActive) {
		this.isActive = isActive;
		actionsui.update();
	}

	public void setInMovementPhase(final boolean isInMovementPhase) {
		if (isInMovementPhase) {
			actionsui.setVisible(false);
			notifyEntityObserver(EntityCommand.IN_MOVEMENT);
		}
	}

	public void setInAttackPhase(final boolean isInAttackPhase) {
		if (canAttack()) {
			this.isInAttackPhase = isInAttackPhase;
			if (isInAttackPhase) {
				actionsui.setVisible(false);
				notifyEntityObserver(EntityCommand.IN_ATTACK_PHASE);
			}
		}
	}

	public void setInSpellPhase(final boolean isInSpellPhase, final Ability ability) {
		if (isInSpellPhase) {
			actionsui.setVisible(false);
			notifyEntityObserver(EntityCommand.IN_SPELL_PHASE, ability);
		}
	}

	public void setInActionPhase(final boolean isInActionPhase) {
		notifyEntityObserver(EntityCommand.UNIT_ACTIVE);

		if (isInActionPhase) {
			actionsui.update();
			actionsui.setVisible(!hasModifier(ModifiersEnum.STUNNED));
		}
	}

	@Override
	public String toString() {
		return "name : " + entityData.getName() + "   ID:" + entityID;
	}

}
