package com.mygdx.game.Particles;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Map.Map;

import Utility.TiledMapPosition;
import Utility.Utility;

public class ParticleMaker {
	private static EnumMap<ParticleType, ParticlePool> particlePools;
	private static EnumMap<ParticleType, ArrayList<Particle>> allParticles;
	private static boolean particlesChanged;

	static {
		particlesChanged = false;
		particlePools = new EnumMap<ParticleType, ParticlePool>(ParticleType.class);
		allParticles = new EnumMap<ParticleType, ArrayList<Particle>>(ParticleType.class);
	}

	private ParticleMaker() {

	}

	public static void drawAllActiveParticles(final SpriteBatch spriteBatch, final float delta) {
		for (final ArrayList<Particle> particleTypeList : allParticles.values()) {
			for (final Particle particle : particleTypeList) {
				if (particle.isActive()) {
					particle.update(delta);
					particle.draw(spriteBatch, delta);
				}

				if (particle.isComplete()) {
					particle.delete();
					particle.deactivate();
				}
			}
		}
	}

	public static void deactivateAllParticlesOfType(final ParticleType particletype) {
		if (allParticles.get(particletype) != null) {
			for (final Particle particle : allParticles.get(particletype)) {
				particle.deactivate();
			}
			particlesChanged = true;
			Utility.unloadAsset(particletype.getParticleFileLocation());
		}
	}

	public static void addParticle(final ParticleType particletype, final TiledMapPosition pos) {
		final ParticlePool particlePool = initiatePool(particletype);
		final Particle newParticle = createPooledParticle(particletype, pos, particlePool);
		addParticleToTypedParticles(particletype, newParticle);
	}

	private static ParticlePool initiatePool(final ParticleType particletype) {
		ParticlePool particlePool;

		if (particlePools.containsKey(particletype)) {
			particlePool = particlePools.get(particletype);
		} else {
			particlePool = new ParticlePool(particletype);
		}
		return particlePool;
	}

	private static Particle createPooledParticle(final ParticleType particletype, final TiledMapPosition pos, final ParticlePool particlePool) {
		final PooledEffect particle = particlePool.getParticleEffect();
		particle.setPosition(pos.getTileX(), pos.getTileY());
		particle.scaleEffect(Map.UNIT_SCALE);
		return new Particle(pos, particle, particletype);
	}

	private static void addParticleToTypedParticles(final ParticleType particletype, final Particle newParticle) {
		if (allParticles.get(particletype) == null) {
			allParticles.put(particletype, new ArrayList<Particle>());
		}
		allParticles.get(particletype).add(newParticle);
		newParticle.start();
	}

	public static Particle getParticle(final ParticleType particletype, final TiledMapPosition pos) {
		for (final Particle particle : allParticles.get(particletype)) {
			if (particle.getPosition().isTileEqualTo(pos)) {
				return particle;
			}
		}
		return null;
	}

	public static boolean isParticleTypeEmpty(final ParticleType particletype) {
		return particlePools.get(particletype) != null;
	}

	public static void deactivateParticle(final Particle particle) {
		particlesChanged = true;
		particle.deactivate();
	}

	public static void cleanUpUnactiveParticles() {
		if (particlesChanged) {
			for (final ArrayList<Particle> particleTypeList : allParticles.values()) {
				for (final Particle particle : particleTypeList) {
					if (!particle.isActive()) {
						particle.delete();
						allParticles.get(particle.getParticleType()).remove(particle);
					}
				}
			}
			particlesChanged = false;
		}
	}
}
