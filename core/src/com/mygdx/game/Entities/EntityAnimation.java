package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Entities.EntityAnimation.Direction;

import Utility.TiledMapPosition;
import Utility.Utility;

public class EntityAnimation {
	private static final String TAG = EntityAnimation.class.getSimpleName();

	private static final int ANIMATIONFRAMES = 3;
	private static final int DIRECTIONS = 4;
	private static final int FRAME_WIDTH = 32;
	private static final int FRAME_HEIGHT = 32;
	private String spritePath;

	private Animation<TextureRegion> walkLeftAnimation;
	private Animation<TextureRegion> walkRightAnimation;
	private Animation<TextureRegion> walkUpAnimation;
	private Animation<TextureRegion> walkDownAnimation;
	
	Array<TextureRegion> walkDownFrames;
	Array<TextureRegion> walkLeftFrames;
	Array<TextureRegion> walkRightFrames;
	Array<TextureRegion> walkUpFrames;
	
	protected float frameTime = 0f;
	protected Sprite frameSprite = null;
	protected TextureRegion currentFrame = null;
	private Direction currentDirection = Direction.DOWN;
	private Direction previousDirection = Direction.UP;
	
	public enum State {
		IDLE, WALKING
	}
	
	public enum Direction {
		UP,RIGHT,DOWN,LEFT;
	}
	
	public EntityAnimation(String spritePath) {
		this.spritePath = spritePath;
		Utility.loadTextureAsset(spritePath);
		loadSprite();
		loadAllAnimations();
	}
	
	public void update(float delta){
		frameTime = (frameTime + delta)%5; //Want to avoid overflow
		updateFrame();
	}
	
	private void loadSprite(){
		Texture texture = Utility.getTextureAsset(spritePath);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		frameSprite = new Sprite(textureFrames[0][0].getTexture(), 0,0,FRAME_WIDTH, FRAME_HEIGHT);
		currentFrame = textureFrames[0][0];
	}
	
	private void loadAllAnimations(){
		TextureRegion[][] textureFrames = initVariables();
		divideAnimationFrames(textureFrames);
		createAnimations();
	}
	
	private TextureRegion[][] initVariables() {
		Texture texture = Utility.getTextureAsset(spritePath);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		
		walkDownFrames = new Array<TextureRegion>(ANIMATIONFRAMES);
		walkLeftFrames = new Array<TextureRegion>(ANIMATIONFRAMES);
		walkRightFrames = new Array<TextureRegion>(ANIMATIONFRAMES);
		walkUpFrames = new Array<TextureRegion>(ANIMATIONFRAMES);
		return textureFrames;
	}

	private void divideAnimationFrames(TextureRegion[][] textureFrames) {
		for (int i = 0; i < DIRECTIONS; i++) {
			for (int j = 0; j < ANIMATIONFRAMES; j++) {
				TextureRegion region = textureFrames[i][j];
				if( region == null ){
					Gdx.app.debug(TAG, "Got null animation frame " + i + "," + j);
				}
				
				switch(i){
					case 0:
						walkDownFrames.insert(j, region);
						break;
					case 1:
						walkLeftFrames.insert(j, region);
						break;
					case 2:
						walkRightFrames.insert(j, region);
						break;
					case 3:
						walkUpFrames.insert(j, region);
						break;
					default:
						break;
				}
			}
		}
	}

	private void createAnimations() {
		walkDownAnimation = new Animation<TextureRegion>(0.25f, walkDownFrames, Animation.PlayMode.LOOP);
		walkLeftAnimation = new Animation<TextureRegion>(0.25f, walkLeftFrames, Animation.PlayMode.LOOP);
		walkRightAnimation = new Animation<TextureRegion>(0.25f, walkRightFrames, Animation.PlayMode.LOOP);
		walkUpAnimation = new Animation<TextureRegion>(0.25f, walkUpFrames, Animation.PlayMode.LOOP);
	}
	
	public Sprite getFrameSprite(){
		return frameSprite;
	}

	public TextureRegion getFrame(){
		return currentFrame;
	}

	public String getSpritePath() {
		return spritePath;
	}
	
	public void setDirection(Direction direction){
		this.previousDirection = this.currentDirection;
		this.currentDirection = direction;
		updateFrame();
	}
	
	public void updateFrame() {
		switch (currentDirection) {
		case DOWN :
			currentFrame = walkDownAnimation.getKeyFrame(frameTime);
			break;
		case LEFT :
			currentFrame = walkLeftAnimation.getKeyFrame(frameTime);
			break;
		case UP :
			currentFrame = walkUpAnimation.getKeyFrame(frameTime);
			break;
		case RIGHT :
			currentFrame = walkRightAnimation.getKeyFrame(frameTime);
			break;
		default:
			break;
		}
	}
	
	public void setFramePos(TiledMapPosition pos) {
		frameSprite.setX(pos.getRealScreenX());
		frameSprite.setY(pos.getRealScreenY());
	}
}
