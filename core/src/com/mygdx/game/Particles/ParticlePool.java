package com.mygdx.game.Particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

import Utility.Utility;

public class ParticlePool {
	
	private ParticleEffectPool particlePool;
	private int MAX_PARTICLES_IN_POOL = 50;
	
	
	
	public ParticlePool(ParticleType particletype) {
		Utility.loadParticleAsset(particletype.getParticleFileLocation());
		particlePool = new ParticleEffectPool(Utility.getParticleAsset(particletype.getParticleFileLocation()),1,MAX_PARTICLES_IN_POOL);
	}
	
	public PooledEffect getParticleEffect() {
		return particlePool.obtain();
	}

}
