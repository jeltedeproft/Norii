package com.mygdx.game.Entities;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import Utility.Utility;

public class EntityAnimation {
	private static final String TAG = EntityAnimation.class.getSimpleName();

	private static final int FRAME_WIDTH = 32;
	private static final int FRAME_HEIGHT = 32;

	private static final int SPELLCAST_ANIMATIONS = 0;
	private static final int THRUST_ANIMATIONS = 0;
	private static final int WALK_ANIMATIONS = 3;
	private static final int SLASH_ANIMATIONS = 0;
	private static final int SHOOT_ANIMATIONS = 0;
	private static final int HURT_ANIMATIONS = 0;
	private static final int IDLE_ANIMATIONS = 0;

	private static final float ANIMATION_DURATION = 0.30f;

	private String spritePath;
	protected float frameTime = 0f;
	protected TextureRegion currentFrame = null;
	private Direction currentDirection = Direction.DOWN;

	private EnumMap<EntityAnimationType, Integer> framesPerAnimationType;
	private EnumMap<EntityAnimationType, Animation.PlayMode> playmodePerAnimationType;
	private EntityAnimationType currentAnimationType = EntityAnimationType.WALK;

	private EnumMap<EntityAnimationType, Animation<TextureRegion>> leftAnimations;
	private EnumMap<EntityAnimationType, Animation<TextureRegion>> rightAnimations;
	private EnumMap<EntityAnimationType, Animation<TextureRegion>> upAnimations;
	private EnumMap<EntityAnimationType, Animation<TextureRegion>> downAnimations;

	public EntityAnimation(final String spritePath) {
		initVariables(spritePath);
		loadTextureSheet();
	}

	private void initVariables(final String spritePath) {
		this.spritePath = spritePath;
		framesPerAnimationType = new EnumMap<>(EntityAnimationType.class);
		playmodePerAnimationType = new EnumMap<>(EntityAnimationType.class);
		downAnimations = new EnumMap<>(EntityAnimationType.class);
		leftAnimations = new EnumMap<>(EntityAnimationType.class);
		rightAnimations = new EnumMap<>(EntityAnimationType.class);
		upAnimations = new EnumMap<>(EntityAnimationType.class);
		initFramesPerAnimationType();
		initPlaymodePerAnimationType();
	}

	private void initFramesPerAnimationType() {
		framesPerAnimationType.put(EntityAnimationType.SPELLCAST, SPELLCAST_ANIMATIONS);
		framesPerAnimationType.put(EntityAnimationType.THRUST, THRUST_ANIMATIONS);
		framesPerAnimationType.put(EntityAnimationType.WALK, WALK_ANIMATIONS);
		framesPerAnimationType.put(EntityAnimationType.SLASH, SLASH_ANIMATIONS);
		framesPerAnimationType.put(EntityAnimationType.SHOOT, SHOOT_ANIMATIONS);
		framesPerAnimationType.put(EntityAnimationType.HURT, HURT_ANIMATIONS);
		framesPerAnimationType.put(EntityAnimationType.IDLE, IDLE_ANIMATIONS);
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

	private void loadTextureSheet() {
		Utility.loadTextureAsset(spritePath);
		final Texture texture = Utility.getTextureAsset(spritePath);
		final TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		currentFrame = textureFrames[0][0];
		divideAnimationFrames(textureFrames);
	}

	private void divideAnimationFrames(final TextureRegion[][] textureFrames) {
		for (Direction direction : Direction.values()) {
			for (EntityAnimationType animationType : EntityAnimationType.values()) {
				Array<TextureRegion> holderTextureRegion = new Array<>();
				for (int i = 0; i < framesPerAnimationType.get(animationType); i++) {
					int y1 = direction.ordinal();
					int y2 = animationType.getYPosition();
					holderTextureRegion.add(textureFrames[y1 + y2][i]);
				}
				sortFrames(direction, animationType, holderTextureRegion);
			}
		}
	}

	private void sortFrames(Direction direction, EntityAnimationType animationType, Array<TextureRegion> holderTextureRegion) {
		switch (direction) {
		case DOWN:
			downAnimations.put(animationType, new Animation<>(ANIMATION_DURATION, holderTextureRegion, playmodePerAnimationType.get(animationType)));
			break;
		case UP:
			upAnimations.put(animationType, new Animation<>(ANIMATION_DURATION, holderTextureRegion, playmodePerAnimationType.get(animationType)));
			break;
		case LEFT:
			leftAnimations.put(animationType, new Animation<>(ANIMATION_DURATION, holderTextureRegion, playmodePerAnimationType.get(animationType)));
			break;
		case RIGHT:
			rightAnimations.put(animationType, new Animation<>(ANIMATION_DURATION, holderTextureRegion, playmodePerAnimationType.get(animationType)));
			break;
		default:
			Gdx.app.log(TAG, "wrong direction for animation");
		}
	}

	public void update(final float delta) {
		frameTime = (frameTime + delta) % 5; // Want to avoid overflow
		updateFrame();
	}

	public void updateFrame() {
		switch (currentDirection) {
		case DOWN:
			currentAnimationType = getNextAnimationType(downAnimations.get(currentAnimationType));
			currentFrame = downAnimations.get(currentAnimationType).getKeyFrame(frameTime);
			break;
		case LEFT:
			currentAnimationType = getNextAnimationType(leftAnimations.get(currentAnimationType));
			currentFrame = leftAnimations.get(currentAnimationType).getKeyFrame(frameTime);
			break;
		case UP:
			currentAnimationType = getNextAnimationType(upAnimations.get(currentAnimationType));
			currentFrame = upAnimations.get(currentAnimationType).getKeyFrame(frameTime);
			break;
		case RIGHT:
			currentAnimationType = getNextAnimationType(rightAnimations.get(currentAnimationType));
			currentFrame = rightAnimations.get(currentAnimationType).getKeyFrame(frameTime);
			break;
		default:
			break;
		}
	}

	private EntityAnimationType getNextAnimationType(Animation<TextureRegion> animation) {
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

	public String getSpritePath() {
		return spritePath;
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
	}

	public enum Direction {
		UP,
		LEFT,
		DOWN,
		RIGHT;
	}
}
