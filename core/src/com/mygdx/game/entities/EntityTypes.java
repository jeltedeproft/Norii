package com.mygdx.game.entities;

//keep this in sync with JSON files.
public enum EntityTypes {
	COMMANDER("Commander"),
	ICARUS("Icarus"),
	DEMON("Demon"),
	SHAMAN("Shaman"),
	BEAST_OGRE("Beast Ogre"),
	BLACK_CAT("Black Cat"),
	BLACK_DOG("Black Dog"),
	BLACK_GHOST_BALL("Black Ghost Ball"),
	BLACK_SKELETON("Black Skeleton"),
	BLACK_SLIME_DARK_BLUE_EYES("Black Slime Dark Blue Eyes"),
	BLACK_SLIME_GREEN_EYES("Black Slime Green Eyes"),
	BLACK_SLIME_PINK_EYES("Black Slime Pink Eyes"),
	BLACK_SLIME_PURPLE_EYES("Black Slime Purple Eyes"),
	BLACK_SLIME_RED_EYES("Black Slime Red Eyes"),
	BLACK_SLIME("Black Slime"),
	BLACK_SLIME_BLUE_EYES("Black Slime Blue Eyes"),
	BLACK_SLIME_WHITE_EYES("Black Slime White Eyes"),
	BLACK_SLIME_YELLOW_EYES("Black Slime Yellow Eyes"),
	BLUE_GHOSTY("Blue Ghosty"),
	BLUE_SLIME("Blue Slime"),
	DARK_BLUE_SLIME("Dark Blue Slime"),
	FEMALE_EIGHT("Female Eight"),
	FEMALE_ELEVEN("Female Eleven"),
	FEMALE_FIFTEEN("Female Fifteen"),
	FEMALE_FIFTY_FIVE("Female Fifty Five"),
	FEMALE_FIFTY_FOUR("Female Fifty Four"),
	FEMALE_FIFTY("Female Fifty"),
	FEMALE_FIVE("Female Five"),
	FEMALE_FOUR("Female Four"),
	FEMALE_NINE("Female Nine"),
	FEMALE_NINETEEN("Female Nineteen"),
	FEMALE_ONE("Female One");

	private String entityName;

	public String getEntityName() {
		return entityName;
	}

	EntityTypes(final String entityName) {
		this.entityName = entityName;
	}
}
