package com.mygdx.game.Particles;

import java.util.ArrayList;
import java.util.EnumMap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Map.Map;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

import Utility.TiledMapPosition;
import Utility.Utility;

public class ParticleMaker {
	private static EnumMap<ParticleType,ParticlePool> particlePools;
	private static EnumMap<ParticleType,ArrayList<Particle>> allParticles;
	private static boolean particlesChanged;
	
	static{
		particlesChanged = false;
		particlePools = new EnumMap<ParticleType,ParticlePool>(ParticleType.class);
		allParticles = new EnumMap<ParticleType,ArrayList<Particle>>(ParticleType.class);
	}
	
	private ParticleMaker() {
		
	}
	
	public static void drawAllActiveParticles(SpriteBatch spriteBatch, float delta) {
		for (ArrayList<Particle> particleTypeList : allParticles.values()) {
			for(Particle particle : particleTypeList) {
				if(particle.isActive()) {
					particle.update(delta);
					particle.draw(spriteBatch,delta);
				}
				
				if (particle.isComplete()) {
					particle.delete();
				}
			}
		}
	}
	
	public static void deactivateAllParticlesOfType(ParticleType particletype){
		if(allParticles.get(particletype) != null) {
			for(Particle particle : allParticles.get(particletype)) {
				particle.deactivate();
			}
			particlesChanged = true;
			Utility.unloadAsset(particletype.getParticleFileLocation());
		}
	}
	
	
	public static void addParticle(ParticleType particletype, TiledMapPosition pos) {
		ParticlePool particlePool = initiatePool(particletype);	
		Particle newParticle = createPooledParticle(particletype, pos, particlePool);	
		checkIfParticleTypeAlreadyExists(particletype, newParticle);
	}

	private static ParticlePool initiatePool(ParticleType particletype) {
		ParticlePool particlePool;

		if(particlePools.containsKey(particletype)) {
			particlePool = particlePools.get(particletype);
		}else {
			particlePool = new ParticlePool(particletype);
		}
		return particlePool;
	}

	private static Particle createPooledParticle(ParticleType particletype, TiledMapPosition pos, ParticlePool particlePool) {
		PooledEffect particle = particlePool.getParticleEffect();
		particle.setPosition(pos.getTileX(), pos.getTileY());
		particle.scaleEffect(Map.UNIT_SCALE);
		return new Particle(pos, particle, particletype);
	}

	private static void checkIfParticleTypeAlreadyExists(ParticleType particletype, Particle newParticle) {
		if(allParticles.get(particletype) == null) {
			allParticles.put(particletype, new ArrayList<Particle>());
		}
		allParticles.get(particletype).add(newParticle);
	}
	
	
	public static Particle getParticle(ParticleType particletype, TiledMapPosition pos) {
		for(Particle particle : allParticles.get(particletype)) {
			if(particle.getPosition().isTileEqualTo(pos)) {
				return particle;
			}
		}
		return null;
	}
	
	public static boolean isParticleTypeEmpty(ParticleType particletype){
		return particlePools.get(particletype) != null;
	}
	
	public static void deactivateParticle(Particle particle) {
		particlesChanged = true;
		particle.deactivate();
	}
	
	public static void cleanUpUnactiveParticles() {
		if(particlesChanged) {
			for(ArrayList<Particle> particleTypeList : allParticles.values()) {
				for(Particle particle : particleTypeList) {
					if(!particle.isActive()) {
						particle.delete();
						allParticles.get(particle.getParticleType()).remove(particle);
					}
				}
			}
			particlesChanged = false;
		}
	}	
}
