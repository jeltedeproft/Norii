package com.mygdx.game.Particles;

public enum ParticleType {
	SPAWN("particles/mysmalleffect.p"),
	MOVE("particles/mysmalleffect.p"),
	SPELL("particles/mysmalleffect.p"),
	ATTACK("particles/attackParticle.p");

	private String particleFileLocation;

	public String getParticleFileLocation() {
		return particleFileLocation;
	}

	ParticleType(final String particleLocation) {
		particleFileLocation = particleLocation;
	}
}
