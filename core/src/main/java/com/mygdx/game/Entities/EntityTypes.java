package com.mygdx.game.Entities;

//keep this in sync with JSON files.
public enum EntityTypes {
	COMMANDER("Commander"),
	ICARUS("Icarus"),
	DEMON("Demon"),
	SHAMAN("Shaman"),
	MONK("Monk"), // 64
	SKELETON("Skeleton"), // 64
	ORC("Orc"), // 64
	KNIGHT("Knight"), // 64
	WHITE_CAT("White Cat"),
	BLACK_CAT("Black Cat"),
	BROWN_CAT("Brown Cat"),
	BROWN_DOG("Brown Dog"),
	GRAY_DOG("Gray Dog"),
	WHITE_DOG("White Dog"),
	SLITHER("Slither"),
	PURPLE_MAGE("Purple Mage"),
	PUMPKIN_HEAD("Pumpkin Head"),
	SKELETAL_KNIGHT("Skeletal Knight"),
	BLACK_SKELETAL("Black Skeletal"),
	MINI_SKELETAL("Mini Skeletal"),
	CREEPY_NURSE_GREEN("Creepy Nurse green"),
	CREEPY_NURSE_WHITE("Creepy Nurse White"),
	GHOSTY("ghosty"),
	GRAY_GHOSTY("Gray Ghosty"),
	FROST_GHOSTY("Frost Ghosty"),
	ALIEN("Alien"),
	BLACK_GHOST("Black Ghost"),
	BLACK_SLIME("Black Slime"),
	POSH_GIRL("Posh Girl"),
	VALIANT_KNIGHT("Valiant Knight"),
	SHADOW("Shadow"),
	SOLDIER("Soldier");

	private String entityName;

	public String getEntityName() {
		return entityName;
	}

	EntityTypes(final String entityName) {
		this.entityName = entityName;
	}
}
