package com.jelte.norii.particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.jelte.norii.utility.TiledMapPosition;

public class Particle extends Actor {

	private TiledMapPosition pos;
	private Boolean active;
	private Boolean shown;
	private int id;
	private final ParticleType type;
	private final PooledEffect particleEffect;

	Particle(final TiledMapPosition pos, final PooledEffect pe, final ParticleType type, final int id) {
		super();
		this.pos = pos;
		shown = true;
		active = true;
		this.type = type;
		particleEffect = pe;
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void deactivate() {
		active = false;
	}

	public void draw(final SpriteBatch spriteBatch, final float delta) {
		if (Boolean.TRUE.equals(shown)) {
			particleEffect.draw(spriteBatch, delta);
		}
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
		if (Boolean.TRUE.equals(shown)) {
			particleEffect.update(delta);
		}
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean isShown() {
		return shown;
	}

	public void setShown(Boolean shown) {
		this.shown = shown;
	}

	@Override
	public String toString() {
		return "Particle : " + type + " at pos : (" + pos.getTileX() + "," + pos.getTileY() + ")";
	}
}
