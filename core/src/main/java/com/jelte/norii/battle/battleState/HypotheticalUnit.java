package com.jelte.norii.battle.battleState;

import java.util.Collection;

import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Modifier;

public class HypotheticalUnit {

	private boolean playerUnit;
	private int hp;
	private int maxHp;
	private int attackRange;
	private int ap;
	private Collection<Modifier> modifiers;
	private Collection<Ability> abilities;
	private int entityId;
	private int x;
	private int y;

	public HypotheticalUnit(int entityId, boolean playerUnit, int hp, int maxHp, int attackRange, int ap, Collection<Modifier> modifiers, Collection<Ability> abilities, int x, int y) {
		super();
		this.playerUnit = playerUnit;
		this.hp = hp;
		this.attackRange = attackRange;
		this.ap = ap;
		this.modifiers = modifiers;
		this.abilities = abilities;
		this.x = x;
		this.y = y;
		this.entityId = entityId;
	}

	public HypotheticalUnit(int entityId, boolean playerUnit, int hp, int maxHp, int attackRange, int ap, Collection<Modifier> collection, Collection<Ability> abilities) {
		super();
		this.playerUnit = playerUnit;
		this.hp = hp;
		this.attackRange = attackRange;
		this.ap = ap;
		this.modifiers = collection;
		this.abilities = abilities;
		this.x = 0;
		this.y = 0;
		this.entityId = entityId;
	}

	public int getScore() {
		int score = getModifiersScore();
		if (playerUnit) {
			return score * (-1);
		} else {
			return score;
		}
	}

	private int getModifiersScore() {
		int score = hp;
		for (Modifier modifier : modifiers) {
			switch (modifier.getType()) {
			case DAMAGE_OVER_TIME:
				score -= 2;
				break;
			case REMOVE_AP:
				score -= 2;
				break;
			case REDUCE_ARMOR:
				score -= 2;
				break;
			case REDUCE_DAMAGE:
				score -= 2;
				break;
			case IMPROVE_DAMAGE:
				score += 2;
				break;
			case STUNNED:
				score -= 2;
				break;
			case ROOTED:
				score -= 2;
				break;
			case SILENCED:
				score -= 2;
				break;
			default:
				return score;
			}
		}
		return score;
	}

	public void addModifier(Modifier modifier) {
		modifiers.add(modifier);
	}

	public boolean isPlayerUnit() {
		return playerUnit;
	}

	public void setPlayerUnit(boolean playerUnit) {
		this.playerUnit = playerUnit;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public Collection<Modifier> getModifiers() {
		return modifiers;
	}

	public void setModifiers(Collection<Modifier> modifiers) {
		this.modifiers = modifiers;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Collection<Ability> getAbilities() {
		return abilities;
	}

	public void setAbilities(Collection<Ability> abilities) {
		this.abilities = abilities;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}

	public int getAp() {
		return ap;
	}

	public void setAp(int ap) {
		this.ap = ap;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

}
