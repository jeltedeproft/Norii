package com.jelte.norii.entities;

import com.jelte.norii.entities.EntityObserver.EntityCommand;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.ui.ActionsUi;

public class PlayerEntity extends Entity {

	private ActionsUi actionsui;

	public PlayerEntity(EntityTypes type) {
		super(type);
		isPlayerUnit = true;
	}

	public ActionsUi getActionsui() {
		return actionsui;
	}

	public void setActionsui(final ActionsUi actionsUi2) {
		this.actionsui = actionsUi2;
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
		return "name : " + entityData.getName() + "   ID:" + entityID + "   pos : (" + currentPlayerPosition.getTileX() + "," + currentPlayerPosition.getTileY() + ")";
	}

}
