package testBalancing;

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

	// ability stats
	private Ability ability;
	private int apCost;
	private int mpCost;
	private int damage;
	private int range;
	private int areaOfEffectRange;

	public Stats(Entity unit) {
		// unit
		entityType = unit.getEntityType();
		magicalDefense = unit.getEntityData().getMagicalDefense();
		physicalDefense = unit.getEntityData().getPhysicalDefense();
		attackRange = unit.getEntityData().getAttackRange();
		maxHP = unit.getEntityData().getMaxHP();
		basicAttackCost = unit.getEntityData().getBasicAttackCost();
		attackPower = unit.getEntityData().getAttackPower();

		// ability
		ability = unit.getAbility();
		apCost = ability.getSpellData().getApCost();
		mpCost = ability.getSpellData().getMpCost();
		damage = ability.getSpellData().getDamage();
		range = ability.getSpellData().getRange();
		areaOfEffectRange = ability.getSpellData().getAreaOfEffectRange();

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

	public Ability getAbility() {
		return ability;
	}

	private void setAbility(Ability ability) {
		this.ability = ability;
	}

	public int getApCost() {
		return apCost;
	}

	public int getMpCost() {
		return mpCost;
	}

	public int getDamage() {
		return damage;
	}

	public int getRange() {
		return range;
	}

	public int getAreaOfEffectRange() {
		return areaOfEffectRange;
	}

	public void setApCost(int apCost) {
		this.apCost = apCost;
	}

	public void setMpCost(int mpCost) {
		this.mpCost = mpCost;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public void setAreaOfEffectRange(int areaOfEffectRange) {
		this.areaOfEffectRange = areaOfEffectRange;
	}

	public Stats makeCopy() {
		Stats stats = new Stats();
		stats.setAbility(ability);
		stats.setAttackPower(attackPower);
		stats.setAttackRange(attackRange);
		stats.setBasicAttackCost(basicAttackCost);
		stats.setEntityType(entityType);
		stats.setMagicalDefense(magicalDefense);
		stats.setMaxHP(maxHP);
		stats.setPhysicalDefense(physicalDefense);
		stats.setApCost(apCost);
		stats.setMpCost(mpCost);
		stats.setAreaOfEffectRange(areaOfEffectRange);
		stats.setDamage(damage);
		stats.setRange(range);
		return stats;
	}

	@Override
	public String toString() {
		return "type = " + entityType + "\n" + "magical defense = " + magicalDefense + "\n" + "physicalDefense = " + physicalDefense + "\n" + "attack range = " + attackRange + "\n" + "basic attack cost = " + basicAttackCost + "\n"
				+ "max HP = " + maxHP + "\n" + "attack power = " + attackPower + "\n" + "ability = " + ability.getName() + "\n" + "ap cost = " + getApCost() + "\n" + "mp cost = " + getMpCost() + "\n" + "damage = " + getDamage() + "\n"
				+ "range = " + getRange() + "\n" + "area of effect range = " + getAreaOfEffectRange() + "\n";
	}

}
