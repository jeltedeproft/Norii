package com.mygdx.game.Magic;

public class Effect {
	private final Mechanic effect;
	private final int value;
	private final int duration;

	public Effect(final Mechanic effect, final int value, final int duration) {
		this.effect = effect;
		this.value = value;
		this.duration = duration;
	}
}
