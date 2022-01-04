package com.jelte.norii.magic;

import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability.DamageType;

public class Modifier {
	private ModifiersEnum type;
	private int turns;
	private int amount;

	public Modifier(final ModifiersEnum type, final int turns, final int amount) {
		super();
		this.type = type;
		this.turns = turns;
		this.amount = amount;
	}

	public void applyModifier(final Entity unit) {
		switch (type) {
		case REMOVE_AP:
			unit.setAp(unit.getAp() - amount);
			break;
		case REDUCE_DAMAGE:
			unit.getEntityData().setAttackPower(unit.getEntityData().getAttackPower() - amount);
			break;
		case IMPROVE_DAMAGE:
			unit.getEntityData().setAttackPower(unit.getEntityData().getAttackPower() + amount);
			break;
		case DAMAGE_OVER_TIME_PHYSICAL:
			unit.damage(amount, DamageType.PHYSICAL);
			break;
		case DAMAGE_OVER_TIME_MAGICAL:
			unit.damage(amount, DamageType.MAGICAL);
			break;
		case PURE_DAMAGE:
			unit.damage(amount, DamageType.PURE_DAMAGE);
			break;
		case REDUCE_MAGICAL_DEFENSE:
			unit.getEntityData().setMagicalDefense(unit.getEntityData().getMagicalDefense() - amount);
			break;
		case REDUCE_PHYSICAL_DEFENSE:
			unit.getEntityData().setPhysicalDefense(unit.getEntityData().getPhysicalDefense() - amount);
			break;
		case STUNNED:
			break;
		case INVISIBLE:
			break;
		default:
			break;

		}
		reduceTurn();
	}

	public void removeModifier(final Entity unit) {
		switch (type) {
		case REDUCE_DAMAGE:
			unit.getEntityData().setAttackPower(unit.getEntityData().getAttackPower() + amount);
			break;
		case IMPROVE_DAMAGE:
			unit.getEntityData().setAttackPower(unit.getEntityData().getAttackPower() - amount);
			break;
		case REDUCE_MAGICAL_DEFENSE:
			unit.getEntityData().setMagicalDefense(unit.getEntityData().getMagicalDefense() + amount);
			break;
		case REDUCE_PHYSICAL_DEFENSE:
			unit.getEntityData().setPhysicalDefense(unit.getEntityData().getPhysicalDefense() + amount);
			break;
		case IMAGE_CHANGED:
			unit.getVisualComponent().restoreAnimation();
			break;
		case INVISIBLE:
			unit.setInvisible(false);
			break;
		default:
			break;
		}
	}

	private void reduceTurn() {
		turns -= 1;
	}

	public ModifiersEnum getType() {
		return type;
	}

	public void setType(final ModifiersEnum type) {
		this.type = type;
	}

	public int getTurns() {
		return turns;
	}

	public void setTurns(final int turns) {
		this.turns = turns;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "modifier name : " + type;
	}
}
