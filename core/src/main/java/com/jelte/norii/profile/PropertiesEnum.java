package com.jelte.norii.profile;

public enum PropertiesEnum {
	AVAILABLE_HEROES("availableHeroes"), TEAM_HEROES("teamHeroes"), MAX_HERO_COUNT("maxHeroCount");

	private String propertyName;

	public String getPropertyName() {
		return propertyName;
	}

	PropertiesEnum(final String propertyName) {
		this.propertyName = propertyName;
	}
}
