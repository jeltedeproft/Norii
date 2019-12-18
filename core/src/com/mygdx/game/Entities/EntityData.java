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
	
	public EntityData() {
		
	}

	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPortraitSpritePath() {
		return portraitSpritePath;
	}
	public void setPortraitSpritePath(String portraitSpritePath) {
		this.portraitSpritePath = portraitSpritePath;
	}
	public String getEntitySpriteFilePath() {
		return entitySpriteFilePath;
	}
	public void setEntitySpriteFilePath(String entitySpriteFilePath) {
		this.entitySpriteFilePath = entitySpriteFilePath;
	}
	public int getMaxAP() {
		return maxAP;
	}
	public void setMaxAP(int maxAP) {
		this.maxAP = maxAP;
	}
	public int getMaxHP() {
		return maxHP;
	}
	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}
	public int getMaxXP() {
		return maxXP;
	}
	public void setMaxXP(int maxXP) {
		this.maxXP = maxXP;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getXp() {
		return xp;
	}
	public void setXp(int xp) {
		this.xp = xp;
	}
	public int getIni() {
		return baseInitiative;
	}
	public void setIni(int ini) {
		this.baseInitiative = ini;
	}
	public int getAttackrange() {
		return attackRange;
	}
	public void setAttackrange(int attackrange) {
		this.attackRange = attackrange;
	}
	public int getAttackPower() {
		return attackPower;
	}
	public void setAttackPower(int attackPower) {
		this.attackPower = attackPower;
	}
	public int getBasicAttackCost() {
		return basicAttackCost;
	}
	public void setBasicAttackCost(int basicAttackCost) {
		this.basicAttackCost = basicAttackCost;
	}
	
}
