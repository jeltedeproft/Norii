package com.jelte.norii.magic;

public class Ability {
	private final SpellData spellData;
	private final AbilitiesEnum abilityEnum;

	public enum LineOfSight {
		LINE, CIRCLE, CROSS, SQUARE, DIAGONAL_RIGHT, DIAGONAL_LEFT, SQUARE_BORDER, CIRCLE_BORDER
	}

	public enum AreaOfEffect {
		CELL, HORIZONTAL_LINE, VERTICAL_LINE, HORIZONTAL_LINE_RIGHT, VERTICAL_LINE_UP, VERTICAL_LINE_DOWN, CIRCLE, CROSS, SQUARE, HORIZONTAL_LINE_LEFT, DIAGONAL_RIGHT, DIAGONAL_LEFT, DIAGONAL, SQUARE_BORDER, CIRCLE_BORDER
	}

	public enum AffectedTeams {
		FRIENDLY, ENEMY, BOTH
	}

	public enum Target {
		UNIT, CELL, CELL_BUT_NO_UNIT, SELF, NO_TARGET;

		public boolean needsUnit(Target target) {
			return target.equals(CELL) || target.equals(UNIT);
		}
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

	public String getSpellInfo() {
		return spellData.getInfoText();
	}

	public int getId() {
		return spellData.getId();
	}

	public LineOfSight getLineOfSight() {
		return LineOfSight.valueOf(spellData.getLineOfSight());
	}

	public AreaOfEffect getAreaOfEffect() {
		return AreaOfEffect.valueOf(spellData.getAreaOfEffect());
	}

	public AffectedTeams getAffectedTeams() {
		return AffectedTeams.valueOf(spellData.getAffectsTeam());
	}

	public Target getTarget() {
		return Target.valueOf(spellData.getTarget());
	}

	public AbilitiesEnum getAbilityEnum() {
		return abilityEnum;
	}

	@Override
	public String toString() {
		return "ability name : " + spellData.getName();
	}

}