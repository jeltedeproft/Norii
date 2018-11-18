package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Unit{
	public static final String SPRITE_KANE = "sprites/Kane.png";
	
	private Texture unittexture;
	private Sprite unitsprite;

	public Unit(Texture texture) {
		unittexture = texture;
		unitsprite = new Sprite(texture);
	}

	public Texture getUnittexture() {
		return unittexture;
	}

	public void setUnittexture(Texture unittexture) {
		this.unittexture = unittexture;
	}

	public Sprite getUnitsprite() {
		return unitsprite;
	}

	public void setUnitsprite(Sprite unitsprite) {
		this.unitsprite = unitsprite;
	}	
}

