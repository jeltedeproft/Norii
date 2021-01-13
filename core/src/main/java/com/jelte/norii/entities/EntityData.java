package com.jelte.norii.entities;

public class EntityData {

	private int id;
	private String name;
	private String portraitSpritePath;
	private String entitySpriteName;
	private int maxAP;
	private int maxHP;
	private int level;
	private int attackRange;
	private int attackPower;
	private int basicAttackCost;
	private String[] abilities;

	private Entity linkedEntity;

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

	public String getEntitySpriteName() {
		return entitySpriteName;
	}

	public void setEntitySpriteName(final String entitySpriteName) {
		this.entitySpriteName = entitySpriteName;
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

	public int getLevel() {
		return level;
	}

	public void setLevel(final int level) {
		this.level = level;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public void setAttackRange(final int attackrange) {
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

	public void setEntity(Entity entity) {
		this.linkedEntity = entity;
	}

	@Override
	public String toString() {
		return "id : " + id + "\n" + "name : " + name + "\n" + "portraitSpritePath : " + portraitSpritePath + "\n" + "entitySpriteFilePath : " + entitySpriteName + "\n" + "maxAP : " + maxAP + "\n" + "maxHP : " + maxHP + "\n" + "level : "
				+ level + "\n" + "attackRange : " + attackRange + "\n" + "attackPower : " + attackPower + "\n" + "basicAttackCost : " + basicAttackCost + "\n";
	}

}
