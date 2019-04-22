package com.mygdx.game.Particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.Particles.ParticleMaker.ParticleType;

import Utility.TiledMapPosition;

public class Particle {
	
	private TiledMapPosition pos;
	private Boolean Active;
	private Boolean _hasLinkedEntity;
	private ParticleType type;
	private PooledEffect particleEffect;
	public Body attachedBody;
	
	Particle(TiledMapPosition pos, Boolean hasLinkedEntity, PooledEffect pe, Boolean active, ParticleType type){
		this.pos = pos;
		this.Active = active;
		this._hasLinkedEntity = hasLinkedEntity;
		this.type = type;
		this.particleEffect = pe;
	}
	
	public boolean isActive() {
		return Active;
	}
	
	public void draw(SpriteBatch spriteBatch, float delta) {
		particleEffect.draw(spriteBatch, delta);
	}
	
	public void free() {
		particleEffect.free();
	}
	
	public Boolean getHasLinkedEntity() {
		return _hasLinkedEntity;
	}

	public void setHasLinkedEntity(Boolean hasLinkedEntity) {
		this._hasLinkedEntity = hasLinkedEntity;
	}

}
