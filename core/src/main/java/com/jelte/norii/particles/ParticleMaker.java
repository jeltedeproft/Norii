package com.jelte.norii.particles;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jelte.norii.map.Map;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.TiledMapPosition;

public class ParticleMaker {
	private static EnumMap<ParticleType, ParticlePool> particlePools;
	private static EnumMap<ParticleType, ArrayList<Particle>> allParticles;
	private static boolean particlesChanged;

	static {
		particlesChanged = false;
		particlePools = new EnumMap<>(ParticleType.class);
		allParticles = new EnumMap<>(ParticleType.class);
	}

	private ParticleMaker() {

	}

	public static void drawAllActiveParticles(final SpriteBatch spriteBatch, final float delta) {
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE); // performance-optim.: manually set blend function to additive!
		for (final ArrayList<Particle> particleTypeList : allParticles.values()) {
			for (final Particle particle : particleTypeList) {
				if (particle.isActive()) {
					particle.update(delta);
					particle.draw(spriteBatch, delta);
				}

				if (particle.isComplete()) {
					particlesChanged = true;
					particle.deactivate();
				}
			}
		}
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); // performance-optim.: manually reset blend function!
	}

	public static void deactivateAllParticlesOfType(final ParticleType particletype) {
		if (allParticles.get(particletype) != null) {
			for (final Particle particle : allParticles.get(particletype)) {
				particle.deactivate();
			}
			particlesChanged = true;
			AssetManagerUtility.unloadAsset(particletype.getParticleFileLocation());
		}
	}

	public static void deactivateParticle(final ParticleType particletype, final TiledMapPosition pos, final int id) {
		if (allParticles.get(particletype) != null) {
			for (final Particle particle : allParticles.get(particletype)) {
				if ((particle.getPosition().isTileEqualTo(pos)) && (particle.getId() == id)) {
					particle.deactivate();
				}
			}
			particlesChanged = true;
		}
	}

	public static void addParticle(final ParticleType particletype, final TiledMapPosition pos, final int id) {
		final ParticlePool particlePool = initiatePool(particletype);
		final Particle newParticle = createPooledParticle(particletype, pos, particlePool, id);
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

	private static Particle createPooledParticle(final ParticleType particletype, final TiledMapPosition pos, final ParticlePool particlePool, final int id) {
		final PooledEffect particle = particlePool.getParticleEffect();
		particle.setPosition(pos.getTileX(), pos.getTileY());
		particle.scaleEffect(Map.UNIT_SCALE);
		return new Particle(pos, particle, particletype, id);
	}

	private static void addParticleToTypedParticles(final ParticleType particletype, final Particle newParticle) {
		allParticles.computeIfAbsent(particletype, k -> new ArrayList<>());
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

	public static Particle getParticle(final ParticleType particletype, final TiledMapPosition pos, final int id) {
		for (final Particle particle : allParticles.get(particletype)) {
			if ((particle.getPosition().isTileEqualTo(pos)) && (particle.getId() == id)) {
				return particle;
			}
		}
		return null;
	}

	public static Particle getParticle(final ParticleType particletype) {
		for (final Particle particle : allParticles.get(particletype)) {
			return particle;
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
