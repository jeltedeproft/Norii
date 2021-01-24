package com.jelte.norii.magic;

import com.jelte.norii.utility.MyPoint;

public class Ability {
	private final SpellData spellData;
	private final AbilitiesEnum abilityEnum;
	private final MyPoint targetLocation; // can be null

	public enum LineOfSight {
		LINE, CIRCLE, CROSS, SQUARE, DIAGONAL_RIGHT, DIAGONAL_LEFT, SQUARE_BORDER, CIRCLE_BORDER
	}

	public enum AreaOfEffect {
		CELL, STRAIGHT_LINE, HORIZONTAL_LINE, VERTICAL_LINE, HORIZONTAL_LINE_RIGHT, VERTICAL_LINE_UP, VERTICAL_LINE_DOWN, CIRCLE, CROSS, SQUARE, HORIZONTAL_LINE_LEFT, DIAGONAL_RIGHT, DIAGONAL_LEFT, DIAGONAL, SQUARE_BORDER, CIRCLE_BORDER
	}

	public enum AffectedTeams {
		FRIENDLY, ENEMY, BOTH, NONE
	}

	public enum Target {
		UNIT, CELL, CELL_BUT_NO_UNIT, SELF, NO_TARGET;

		public boolean needsUnit(Target target) {
			return target.equals(CELL) || target.equals(UNIT);
		}
	}

	public Ability(final AbilitiesEnum abilityEnum) {
		this.abilityEnum = abilityEnum;
		this.targetLocation = null;
		spellData = SpellFileReader.getSpellData().get(abilityEnum.ordinal());
	}

	public Ability(final AbilitiesEnum abilityEnum, MyPoint targetLocation) {
		this.abilityEnum = abilityEnum;
		this.targetLocation = targetLocation;
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

	public boolean getGoesTroughUnits() {
		return spellData.isGoesTroughUnits();
	}

	public MyPoint getTargetLocation() {
		return targetLocation;
	}

	@Override
	public String toString() {
		return "ability name : " + spellData.getName();
	}

}