package com.mygdx.game.Particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

public class Particle extends Actor{
	
	private TiledMapPosition pos;
	private Boolean Active;
	private ParticleType type;
	private PooledEffect particleEffect;
	public Body attachedBody;
	
	Particle(TiledMapPosition pos, PooledEffect pe, ParticleType type){
		super();
		this.pos = pos;
		this.Active = true;
		this.type = type;
		this.particleEffect = pe;
	}
	
	public boolean isActive() {
		return Active;
	}
	
	public void deactivate() {
		this.Active = false;
	}
    
	public void draw(SpriteBatch spriteBatch, float delta) {
		Gdx.app.debug("Particle : ", "spritebatch position = " + spriteBatch.getProjectionMatrix().getScaleX() + " , " + spriteBatch.getProjectionMatrix().getScaleY() + ")"); 
		particleEffect.draw(spriteBatch, delta);
	}
	
	public void delete() {
		particleEffect.free();
	}
	
	public ParticleType getParticleType() {
		return type;
	}
	
	public TiledMapPosition getPosition() {
		return pos;
	}
	
	public void update(float delta) {
		this.particleEffect.update(delta);
	}
	
	public boolean isComplete() {
		return this.particleEffect.isComplete();
	}
	
	public PooledEffect getParticleEffect() {
		return particleEffect;
	}
}
