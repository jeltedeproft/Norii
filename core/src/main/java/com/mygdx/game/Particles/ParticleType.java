package com.mygdx.game.Particles;

public enum ParticleType {
	SPAWN("particles/spawn.p"), //
	MOVE("particles/move.p"), //
	SPELL("particles/mysmalleffect.p"), //
	FIREBALL("particles/fireball.p"), //
	SWAP("particles/swap.p"), //
	ATTACK("particles/attack.p");//

	private String particleFileLocation;

	public String getParticleFileLocation() {
		return particleFileLocation;
	}

	ParticleType(final String particleLocation) {
		particleFileLocation = particleLocation;
	}
}
