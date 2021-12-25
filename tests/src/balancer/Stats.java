package balancer;

import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.magic.Ability;

public class Stats {
	private EntityTypes entityType;

	// general unit stats
	private int magicalDefense;
	private int physicalDefense;
	private int attackRange;
	private int maxHP;
	private int basicAttackCost;
	private int attackPower;

	// ability
	private Ability ability;
	private AbilityStats abilityStats;

	public Stats(Entity unit) {
		magicalDefense = unit.getEntityData().getMagicalDefense();
		physicalDefense = unit.getEntityData().getPhysicalDefense();
		attackRange = unit.getEntityData().getAttackRange();
		maxHP = unit.getEntityData().getMaxHP();
		basicAttackCost = unit.getEntityData().getBasicAttackCost();
		attackPower = unit.getEntityData().getAttackPower();
		ability = unit.getAbility();
		abilityStats = new AbilityStats(unit.getAbility());
		entityType = unit.getEntityType();
	}

	public Stats() {
		// do nothing
	}

	public EntityTypes getEntity() {
		return entityType;
	}

	public int getMagicalDefense() {
		return magicalDefense;
	}

	public int getPhysicalDefense() {
		return physicalDefense;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public int getBasicAttackCost() {
		return basicAttackCost;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public AbilityStats getAbilityStats() {
		return abilityStats;
	}

	public void setEntityType(EntityTypes entityType) {
		this.entityType = entityType;
	}

	public void setMagicalDefense(int magicalDefense) {
		this.magicalDefense = magicalDefense;
	}

	public void setPhysicalDefense(int physicalDefense) {
		this.physicalDefense = physicalDefense;
	}

	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}

	public void setBasicAttackCost(int basicAttackCost) {
		this.basicAttackCost = basicAttackCost;
	}

	public void setAttackPower(int attackPower) {
		this.attackPower = attackPower;
	}

	public void setAbilityStats(AbilityStats abilityStats) {
		this.abilityStats = abilityStats;
	}

	public Ability getAbility() {
		return ability;
	}

	private void setAbility(Ability ability) {
		this.ability = ability;
	}

	public Stats makeCopy() {
		Stats stats = new Stats();
		stats.setAbilityStats(abilityStats);
		stats.setAbility(ability);
		stats.setAttackPower(attackPower);
		stats.setAttackRange(attackRange);
		stats.setBasicAttackCost(basicAttackCost);
		stats.setEntityType(entityType);
		stats.setMagicalDefense(magicalDefense);
		stats.setMaxHP(maxHP);
		stats.setPhysicalDefense(physicalDefense);
		return stats;
	}

}
