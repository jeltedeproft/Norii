package com.mygdx.game.Magic;

public class SpellData {

	private int id;
	private String name;
	private String iconSpritePath;
	private int apCost;
	private int mpCost;
	private String lineOfSight;
	private String target;
	private int damage;
	private int range;

	public SpellData() {

	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getIconSpritePath() {
		return iconSpritePath;
	}

	public void setIconSpritePath(final String iconSpritePath) {
		this.iconSpritePath = iconSpritePath;
	}

	public int getApCost() {
		return apCost;
	}

	public void setApCost(final int apCost) {
		this.apCost = apCost;
	}

	public int getMpCost() {
		return mpCost;
	}

	public void setMpCost(final int mpCost) {
		this.mpCost = mpCost;
	}

	public String getLineOfSight() {
		return lineOfSight;
	}

	public void setLineOfSight(final String lineOfSight) {
		this.lineOfSight = lineOfSight;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(final String target) {
		this.target = target;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(final int damage) {
		this.damage = damage;
	}

	public int getRange() {
		return range;
	}

	public void setRange(final int range) {
		this.range = range;
	}

}
