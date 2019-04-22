package com.mygdx.game.Particles;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.mygdx.game.Map.Map;

import Utility.Utility;

public class ParticleMaker {
	private final static String SPAWNEFFECT = "particles/mysmalleffect.p";
	private final static String MOVEEFFECT = "particles/mysmalleffect.p";
	private final static int MAX_PARTICLES_IN_POOL = 50;
	
	private ArrayMap<Object,Particle> particlesLinkedToEntity;
	private ArrayList<Particle> allParticles;
	
	private ArrayList<PooledEffect> spawnParticles;
	private ArrayList<PooledEffect> moveparticles;
	private ParticleEffectPool spawnEffectPool;
	private ParticleEffectPool moveEffectPool;
	
	public enum ParticleType{
		SPAWN,
		MOVE
	}
	
	//getparticle(type)
	//getpaticles(type)
	
	public void drawAllActiveParticles(SpriteBatch spriteBatch, float delta) {
		for(Particle particle : allParticles) {
			if(particle.isActive()) {
				particle.draw(spriteBatch, delta);
			}
		}
	}
	
	public void deactivateParticle() {
		
	}
	
	public void deactivateAllParticlesOfType(ParticleType particletype){
		
	}
	
	public void linkParticleWithEntity(Object object,Particle particle) {
		particle.setHasLinkedEntity(true);
		particlesLinkedToEntity.put(object, particle);
	}
	
	public void initializeParticles() {
		particlesLinkedToEntity = new ArrayMap<Object, Particle>();
		//max 50
		spawnParticles = new ArrayList<PooledEffect>();
		moveparticles = new ArrayList<PooledEffect>();
		Utility.loadParticleAsset(SPAWNEFFECT);
		Utility.loadParticleAsset(MOVEEFFECT);
		spawnEffectPool =  new ParticleEffectPool(Utility.getParticleAsset(SPAWNEFFECT),1,MAX_PARTICLES_IN_POOL);  
		moveEffectPool =  new ParticleEffectPool(Utility.getParticleAsset(MOVEEFFECT),1,MAX_PARTICLES_IN_POOL); 
	}
	
	public void addParticle(ParticleType particletype, Vector2 pos) {
		PooledEffect particle = spawnEffectPool.obtain();
		particle.setPosition(pos.x  / Map.UNIT_SCALE, pos.y / Map.UNIT_SCALE);
		spawnParticles.add(particle);
	}
	
	public Particle getParticle() {
		Particle newParticle = new Particle(null, null, null, null, null);
		return moveEffectPool.obtain();
	}
	
	public ArrayList<PooledEffect> getParticles(ParticleType particletype){
		switch(particletype) {
		  case SPAWN:
		    // code block
		    break;
		  case MOVE:
		    // code block
		    break;
		  default:
		    // code block
		}
	}
	
	public boolean isParticleTypeEmpty(ParticleType particletype){
		switch(particletype) {
		  case SPAWN:
		    return spawnParticles.isEmpty();
		case MOVE:
		    return moveparticles.isEmpty();
		default:
		    return false; //give error
		}
	}
	
	public void cleanUpUnactiveParticles() {
		for(Particle particle : allParticles) {
			if(!particle.isActive()) {
				if(particle.getHasLinkedEntity()) {
					particlesLinkedToEntity.removeValue(particle, true);
				}
				particle.free();
				allParticles.remove(particle);
				particle = null;
			}
		}
	}
}
