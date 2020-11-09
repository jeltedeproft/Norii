package com.jelte.norii.ai;

public class AITeamData {

	private int id;
	private String name;
	private String[] units;

	public AITeamData() {

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

	public String[] getUnits() {
		return units;
	}

	public void setUnits(final String[] units) {
		this.units = units;
	}

}
