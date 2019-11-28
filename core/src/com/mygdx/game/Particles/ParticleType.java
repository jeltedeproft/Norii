package com.mygdx.game.Particles;

public enum ParticleType {
	SPAWN("particles/mysmalleffect.p"),
	MOVE("particles/mysmalleffect.p"),
	ATTACK("particles/attackParticle.p");
	
	private String particleFileLocation;

	public String getParticleFileLocation() {
		return this.particleFileLocation;
	}

	ParticleType(String particleLocation) {
		this.particleFileLocation = particleLocation;
	}
}
