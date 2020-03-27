package com.mygdx.game.Entities;

public class EntityData {

	private int id;
	private String name;
	private String portraitSpritePath;
	private String entitySpriteFilePath;
	private int maxAP;
	private int maxHP;
	private int level;
	private int xp;
	private int maxXP;
	private int baseInitiative;
	private int attackRange;
	private int attackPower;
	private int basicAttackCost;
	private String[] abilities;

	public EntityData() {

	}

	public int getID() {
		return id;
	}

	public void setID(final int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getPortraitSpritePath() {
		return portraitSpritePath;
	}

	public void setPortraitSpritePath(final String portraitSpritePath) {
		this.portraitSpritePath = portraitSpritePath;
	}

	public String getEntitySpriteFilePath() {
		return entitySpriteFilePath;
	}

	public void setEntitySpriteFilePath(final String entitySpriteFilePath) {
		this.entitySpriteFilePath = entitySpriteFilePath;
	}

	public int getMaxAP() {
		return maxAP;
	}

	public void setMaxAP(final int maxAP) {
		this.maxAP = maxAP;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(final int maxHP) {
		this.maxHP = maxHP;
	}

	public int getMaxXP() {
		return maxXP;
	}

	public void setMaxXP(final int maxXP) {
		this.maxXP = maxXP;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(final int level) {
		this.level = level;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(final int xp) {
		this.xp = xp;
	}

	public int getIni() {
		return baseInitiative;
	}

	public void setIni(final int ini) {
		baseInitiative = ini;
	}

	public int getAttackrange() {
		return attackRange;
	}

	public void setAttackrange(final int attackrange) {
		attackRange = attackrange;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public void setAttackPower(final int attackPower) {
		this.attackPower = attackPower;
	}

	public int getBasicAttackCost() {
		return basicAttackCost;
	}

	public void setBasicAttackCost(final int basicAttackCost) {
		this.basicAttackCost = basicAttackCost;
	}

	public String[] getAbilties() {
		return abilities;
	}

	public void setAbilties(final String[] abilties) {
		this.abilities = abilties;
	}

}
