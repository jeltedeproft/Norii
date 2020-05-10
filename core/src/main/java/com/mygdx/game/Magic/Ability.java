package com.mygdx.game.Magic;

public class Ability {
	private final SpellData spellData;
	private final AbilitiesEnum abilityEnum;

	public enum LineOfSight {
		LINE, CIRCLE, CROSS
	}

	public enum Targets {
		CELL, LINE, CIRCLE, CROSS, SQUARE
	}

	public enum AffectedTeams {
		FRIENDLY, ENEMY, BOTH
	}

	public Ability(final AbilitiesEnum abilityEnum) {
		this.abilityEnum = abilityEnum;
		spellData = SpellFileReader.getSpellData().get(abilityEnum.ordinal());
	}

	public SpellData getSpellData() {
		return spellData;
	}

	public String getName() {
		return spellData.getName();
	}

	public int getId() {
		return spellData.getId();
	}

	public LineOfSight getLineOfSight() {
		return LineOfSight.valueOf(spellData.getLineOfSight());
	}

	public Targets getTargets() {
		return Targets.valueOf(spellData.getTarget());
	}

	public AffectedTeams getAffectedTeams() {
		return AffectedTeams.valueOf(spellData.getAffectsTeam());
	}

	public AbilitiesEnum getAbilityEnum() {
		return abilityEnum;
	}

}