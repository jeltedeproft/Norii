package com.jelte.norii.magic;

public class SpellData {

	private int id;
	private String name;
	private String iconSpriteName;
	private int apCost;
	private int mpCost;
	private String lineOfSight;
	private boolean goesTroughUnits;
	private boolean goesTroughObstacles;
	private String target;
	private String areaOfEffect;
	private int damage;
	private String damageType;
	private int durationInTurns;
	private int range;
	private int areaOfEffectRange;
	private String affectsTeam;
	private String infoText;

	public SpellData() {
		// no-op
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

	public String getIconSpriteName() {
		return iconSpriteName;
	}

	public void setIconSpriteName(final String iconSpriteName) {
		this.iconSpriteName = iconSpriteName;
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

	public boolean isGoesTroughUnits() {
		return goesTroughUnits;
	}

	public void setGoesTroughUnits(boolean goesTroughUnits) {
		this.goesTroughUnits = goesTroughUnits;
	}

	public boolean isGoesTroughObstacles() {
		return goesTroughObstacles;
	}

	public void setGoesTroughObstacles(boolean goesTroughObstacles) {
		this.goesTroughObstacles = goesTroughObstacles;
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

	public String getDamageType() {
		return damageType;
	}

	public void setDamageType(String damageType) {
		this.damageType = damageType;
	}

	public int getRange() {
		return range;
	}

	public void setRange(final int range) {
		this.range = range;
	}

	public int getAreaOfEffectRange() {
		return areaOfEffectRange;
	}

	public void setAreaOfEffectRange(int areaOfEffectRange) {
		this.areaOfEffectRange = areaOfEffectRange;
	}

	public String getAffectsTeam() {
		return affectsTeam;
	}

	public void setAffectsTeam(String affectsTeam) {
		this.affectsTeam = affectsTeam;
	}

	public String getAreaOfEffect() {
		return areaOfEffect;
	}

	public void setAreaOfEffect(String areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}

	public String getInfoText() {
		return infoText;
	}

	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}

	public int getDurationInTurns() {
		return durationInTurns;
	}

	public void setDurationInTurns(int durationInTurns) {
		this.durationInTurns = durationInTurns;
	}

}
