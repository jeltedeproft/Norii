package com.mygdx.game.particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

import utility.AssetManagerUtility;

public class ParticlePool {

	private ParticleEffectPool particleEffectPool;
	private static final int MAX_PARTICLES_IN_POOL = 50;
	private static final int INITIAL_CAPACITY = 1;

	public ParticlePool(ParticleType particletype) {
		AssetManagerUtility.loadParticleAsset(particletype.getParticleFileLocation());
		particleEffectPool = new ParticleEffectPool(AssetManagerUtility.getParticleAsset(particletype.getParticleFileLocation()), INITIAL_CAPACITY, MAX_PARTICLES_IN_POOL);
	}

	public PooledEffect getParticleEffect() {
		return particleEffectPool.obtain();
	}

}
