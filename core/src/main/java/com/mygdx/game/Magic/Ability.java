package com.mygdx.game.Magic;

public class Ability {
	private final SpellData spellData;

	public Ability(final AbilitiesEnum abilityEnum) {
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

}