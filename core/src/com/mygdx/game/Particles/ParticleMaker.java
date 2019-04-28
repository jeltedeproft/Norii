package com.mygdx.game.Particles;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.mygdx.game.Screen.BattleScreen;

import Utility.TiledMapPosition;
import Utility.Utility;

public class ParticleMaker {
	private static final String TAG = BattleScreen.class.getSimpleName();
	private final static int MAX_PARTICLES_IN_POOL = 50;
	
	private static HashMap<ParticleType,ParticlePool> particlePools;
	private static HashMap<ParticleType,ArrayList<Particle>> allParticles;
	private static boolean ParticlesHaveChanged;
	


	static{
		ParticlesHaveChanged = false;
		particlePools = new HashMap<ParticleType,ParticlePool>();
	}
	
	public static void drawAllActiveParticles(SpriteBatch spriteBatch, float delta) {		
		for (ArrayList<Particle> particleTypeList : allParticles.values()) {
			spriteBatch.begin();
			for(Particle particle : particleTypeList) {
				particle.update(delta);
				particle.draw(spriteBatch,delta);
				
				if (particle.isComplete()) {
					particle.delete();
					//particles.remove(particle);
				}
			}
			spriteBatch.end();
		}
	}
	
	public static void deactivateAllParticlesOfType(ParticleType particletype){
		for(Particle particle : allParticles.get(particletype)) {
			particle.deactivate();
		}
	}
	
	
	public static void addParticle(ParticleType particletype, TiledMapPosition pos) {
		ParticlePool particlePool;
		
		//if pool exists, reuse, else create one
		if(particlePools.containsKey(particletype)) {
			particlePool = particlePools.get(particletype);
		}else {
			particlePool = new ParticlePool(particletype);
		}
		
		PooledEffect particle = particlePool.getParticleEffect();
		Particle newParticle = new Particle(pos, particle, particletype);
		allParticles.get(particletype).add(newParticle);
	}
	
	
	public static Particle getParticle(ParticleType particletype, TiledMapPosition pos) {
		for(Particle particle : allParticles.get(particletype)) {
			if(particle.getPosition().isEqualTo(pos)) {
				return particle;
			}
		}
		return null;
	}
	
	public static boolean isParticleTypeEmpty(ParticleType particletype){
		return particlePools.get(particletype) != null;
	}
	
	public static void deactivateParticle(Particle particle) {
		ParticlesHaveChanged = true;
		particle.deactivate();
	}
	
	public static void cleanUpUnactiveParticles() {
		if(ParticlesHaveChanged) {
			for(ArrayList<Particle> particleTypeList : allParticles.values()) {
				for(Particle particle : particleTypeList) {
					if(!particle.isActive()) {
						particle.delete();
						allParticles.get(particle.getParticleType()).remove(particle);
						particle = null;
					}
				}
			}
			ParticlesHaveChanged = false;
		}
	}
}
