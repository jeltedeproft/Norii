package com.mygdx.game.Particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import Utility.TiledMapPosition;

public class Particle extends Actor {

	private TiledMapPosition pos;
	private Boolean active;
	private final ParticleType type;
	private final PooledEffect particleEffect;

	Particle(final TiledMapPosition pos, final PooledEffect pe, final ParticleType type) {
		super();
		this.pos = pos;
		active = true;
		this.type = type;
		particleEffect = pe;
	}

	public boolean isActive() {
		return active;
	}

	public void deactivate() {
		active = false;
	}

	public void draw(final SpriteBatch spriteBatch, final float delta) {
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

	public void setPosition(TiledMapPosition pos) {
		this.pos = pos;
		particleEffect.setPosition(pos.getTileX(), pos.getTileY());
	}

	public void update(final float delta) {
		particleEffect.update(delta);
	}

	public boolean isComplete() {
		return particleEffect.isComplete();
	}

	public PooledEffect getParticleEffect() {
		return particleEffect;
	}

	public void start() {
		particleEffect.start();
	}

	@Override
	public String toString() {
		return "Particle : " + type + " at pos : (" + pos.getTileX() + "," + pos.getTileY() + ")";
	}
}
