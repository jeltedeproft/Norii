package com.mygdx.game.entities;

import java.util.EnumMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import utility.AssetManagerUtility;

public class EntityAnimation {
	private static final String TAG = EntityAnimation.class.getSimpleName();

	private static final float ANIMATION_DURATION = 0.3f;

	private String spriteName;
	protected float frameTime = 0f;
	protected TextureRegion currentFrame = null;
	private Direction currentDirection = Direction.DOWN;
	private Animation<TextureRegion> currentAnimation;
	private EnumMap<EntityAnimationType, Animation.PlayMode> playmodePerAnimationType;
	private EntityAnimationType currentAnimationType = EntityAnimationType.IDLE;

	public EntityAnimation(final String spriteName) {
		initVariables(spriteName);
	}

	private void initVariables(final String spriteName) {
		this.spriteName = spriteName;
		playmodePerAnimationType = new EnumMap<>(EntityAnimationType.class);
		initPlaymodePerAnimationType();
		String animationName = spriteName + currentAnimationType.getTypeAsString() + currentDirection.getDirectionAsString();
		currentAnimation = AssetManagerUtility.getAnimation(animationName, ANIMATION_DURATION, playmodePerAnimationType.get(currentAnimationType));

		if (currentAnimation == null) {
			animationName = spriteName + "Walk" + currentDirection.getDirectionAsString();
			currentAnimation = AssetManagerUtility.getAnimation(animationName, ANIMATION_DURATION, playmodePerAnimationType.get(currentAnimationType));
		}
	}

	private void initPlaymodePerAnimationType() {
		playmodePerAnimationType.put(EntityAnimationType.SPELLCAST, Animation.PlayMode.NORMAL);
		playmodePerAnimationType.put(EntityAnimationType.THRUST, Animation.PlayMode.NORMAL);
		playmodePerAnimationType.put(EntityAnimationType.WALK, Animation.PlayMode.LOOP);
		playmodePerAnimationType.put(EntityAnimationType.SLASH, Animation.PlayMode.NORMAL);
		playmodePerAnimationType.put(EntityAnimationType.SHOOT, Animation.PlayMode.NORMAL);
		playmodePerAnimationType.put(EntityAnimationType.HURT, Animation.PlayMode.NORMAL);
		playmodePerAnimationType.put(EntityAnimationType.IDLE, Animation.PlayMode.LOOP);
	}

	public void update(final float delta) {
		frameTime = (frameTime + delta) % 5; // Want to avoid overflow
		updateFrame();
	}

	public void updateFrame() {
		currentAnimationType = returnIdleIfFinished(currentAnimation);
		currentFrame = currentAnimation.getKeyFrame(frameTime);
	}

	private EntityAnimationType returnIdleIfFinished(Animation<TextureRegion> animation) {
		if (animation.getPlayMode() == PlayMode.NORMAL && isFinished(animation)) {
			return EntityAnimationType.IDLE;
		}

		return currentAnimationType;
	}

	private boolean isFinished(Animation<TextureRegion> animation) {
		return frameTime > animation.getAnimationDuration();
	}

	public TextureRegion getFrame() {
		return currentFrame;
	}

	public String getSpriteName() {
		return spriteName;
	}

	public void setDirection(final Direction direction) {
		currentDirection = direction;
		updateFrame();
	}

	public Direction getCurrentDirection() {
		return currentDirection;
	}

	public EntityAnimationType getCurrentAnimationType() {
		return currentAnimationType;
	}

	public void setCurrentAnimationType(EntityAnimationType currentAnimationType) {
		this.currentAnimationType = currentAnimationType;
		frameTime = 0;
		String animationName = spriteName + currentAnimationType.getTypeAsString() + currentDirection.getDirectionAsString();
		currentAnimation = AssetManagerUtility.getAnimation(animationName, ANIMATION_DURATION, playmodePerAnimationType.get(currentAnimationType));
	}

	public enum Direction {
		UP("Up"),
		LEFT("Left"),
		DOWN("Down"),
		RIGHT("Right");

		private String directionString;

		public String getDirectionAsString() {
			return directionString;
		}

		Direction(final String directionString) {
			this.directionString = directionString;
		}
	}
}
