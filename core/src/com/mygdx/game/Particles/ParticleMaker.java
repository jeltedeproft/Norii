package com.mygdx.game.Particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Audio.AudioManager;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityObserver;
import com.mygdx.game.Map.MyPathFinder;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

import Utility.TiledMapPosition;
import Utility.Utility;

public class ParticleMaker {
	private static final String TAG = ParticleMaker.class.getSimpleName();
	
	private static HashMap<ParticleType,ParticlePool> particlePools;
	private static HashMap<ParticleType,ArrayList<Particle>> allParticles;
	private static boolean ParticlesHaveChanged;
	
	private static ParticleMaker _instance = null;
	
	static{
		ParticlesHaveChanged = false;
		particlePools = new HashMap<ParticleType,ParticlePool>();
		allParticles = new HashMap<ParticleType,ArrayList<Particle>>();
	}
	
    public static ParticleMaker getInstance() {
        if (_instance == null) {
            _instance = new ParticleMaker();
        }

        return _instance;
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
					//particles.remove(particle);
				}
			}
		}
	}
	
	public static void deactivateAllParticlesOfType(ParticleType particletype){
		for(Particle particle : allParticles.get(particletype)) {
			particle.deactivate();
		}
		ParticlesHaveChanged = true;
		Utility.unloadAsset(particletype.getParticleFileLocation());
	}
	
	
	public static void addParticle(ParticleType particletype, TiledMapPosition pos) {
		ParticlePool particlePool;
		Gdx.app.debug(TAG, "adding particle at pos : " + pos.getRealX() + " , " + pos.getRealY());
		//if pool exists, reuse, else create one
		if(particlePools.containsKey(particletype)) {
			particlePool = particlePools.get(particletype);
		}else {
			particlePool = new ParticlePool(particletype);
		}
		
		PooledEffect particle = particlePool.getParticleEffect();
		//particleparticle.setPosition(pos.getRealX(), pos.getRealY());
		particle.setPosition(pos.getTileX(), pos.getTileY());
		Particle newParticle = new Particle(pos, particle, particletype);
		
		//check if particletype is in the list, if no add it
		if(allParticles.get(particletype) == null) {
			allParticles.put(particletype, new ArrayList<Particle>());
		}
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
