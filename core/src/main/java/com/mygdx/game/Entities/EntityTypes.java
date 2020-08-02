package com.mygdx.game.Entities;

//keep this in sync with JSON files.
public enum EntityTypes {
	COMMANDER("Commander"),
	ICARUS("Icarus"),
	DEMON("Demon"),
	SHAMAN("Shaman"),
	MONK("Monk"),
	SKELETON("Skeleton"),
	ORC("Orc"),
	KNIGHT("Knight");

	private String entityName;

	public String getEntityName() {
		return entityName;
	}

	EntityTypes(final String entityName) {
		this.entityName = entityName;
	}
}
