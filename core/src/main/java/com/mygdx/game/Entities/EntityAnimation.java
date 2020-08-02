package com.mygdx.game.Entities;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import Utility.Utility;

public class EntityAnimation {
	private static final String TAG = EntityAnimation.class.getSimpleName();

	private static final int FRAME_WIDTH = 64;
	private static final int FRAME_HEIGHT = 64;

	private String spritePath;
	protected float frameTime = 0f;
	protected TextureRegion currentFrame = null;
	private Direction currentDirection = Direction.DOWN;

	private EnumMap<EntityAnimationType, Integer> framesPerAnimationType;
	private EnumMap<EntityAnimationType, Animation.PlayMode> playmodePerAnimationType;
	private EntityAnimationType currentAnimationType = EntityAnimationType.IDLE;

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
		framesPerAnimationType.put(EntityAnimationType.SPELLCAST, 7);
		framesPerAnimationType.put(EntityAnimationType.THRUST, 8);
		framesPerAnimationType.put(EntityAnimationType.WALK, 9);
		framesPerAnimationType.put(EntityAnimationType.SLASH, 6);
		framesPerAnimationType.put(EntityAnimationType.SHOOT, 13);
		framesPerAnimationType.put(EntityAnimationType.HURT, 6);
		framesPerAnimationType.put(EntityAnimationType.IDLE, 2);
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
				if (notIdleOrDeath(animationType)) {
					for (int i = 0; i < framesPerAnimationType.get(animationType); i++) {
						int y1 = direction.ordinal();
						int y2 = animationType.getYPosition();
						holderTextureRegion.add(textureFrames[y1 + y2][i]);
					}
				} else {
					addIdleOrDeath(direction, animationType, holderTextureRegion, textureFrames);
				}
				sortFrames(direction, animationType, holderTextureRegion);
			}
		}
	}

	private boolean notIdleOrDeath(EntityAnimationType animationType) {
		return !(animationType == EntityAnimationType.IDLE || animationType == EntityAnimationType.HURT);
	}

	private void addIdleOrDeath(Direction direction, EntityAnimationType animationType, Array<TextureRegion> holderTextureRegion, TextureRegion[][] textureFrames) {
		if (animationType == EntityAnimationType.IDLE) {
			int y1 = direction.ordinal();
			holderTextureRegion.add(textureFrames[y1][0]);
			holderTextureRegion.add(textureFrames[y1][1]);
		}

		if (animationType == EntityAnimationType.HURT) {
			int y2 = animationType.getYPosition();
			for (int i = 0; i < framesPerAnimationType.get(animationType); i++) {
				holderTextureRegion.add(textureFrames[y2][i]);
			}
		}
	}

	private void sortFrames(Direction direction, EntityAnimationType animationType, Array<TextureRegion> holderTextureRegion) {
		switch (direction) {
		case DOWN:
			downAnimations.put(animationType, new Animation<>(0.25f, holderTextureRegion, playmodePerAnimationType.get(animationType)));
			break;
		case UP:
			upAnimations.put(animationType, new Animation<>(0.25f, holderTextureRegion, playmodePerAnimationType.get(animationType)));
			break;
		case LEFT:
			leftAnimations.put(animationType, new Animation<>(0.25f, holderTextureRegion, playmodePerAnimationType.get(animationType)));
			break;
		case RIGHT:
			rightAnimations.put(animationType, new Animation<>(0.25f, holderTextureRegion, playmodePerAnimationType.get(animationType)));
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
			currentFrame = downAnimations.get(currentAnimationType).getKeyFrame(frameTime);
			break;
		case LEFT:
			currentFrame = leftAnimations.get(currentAnimationType).getKeyFrame(frameTime);
			break;
		case UP:
			currentFrame = upAnimations.get(currentAnimationType).getKeyFrame(frameTime);
			break;
		case RIGHT:
			currentFrame = rightAnimations.get(currentAnimationType).getKeyFrame(frameTime);
			break;
		default:
			break;
		}
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
	}

	public enum Direction {
		UP,
		LEFT,
		DOWN,
		RIGHT;
	}
}
