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
	//animation stuff
	private static final int animationframes = 3;
	private static final int FRAME_WIDTH = 32;
	private static final int FRAME_HEIGHT = 32;
	private String _spritePath;

	private Animation<TextureRegion> _walkLeftAnimation;
	private Animation<TextureRegion> _walkRightAnimation;
	private Animation<TextureRegion> _walkUpAnimation;
	private Animation<TextureRegion> _walkDownAnimation;

	private Array<TextureRegion> _walkLeftFrames;
	private Array<TextureRegion> _walkRightFrames;
	private Array<TextureRegion> _walkUpFrames;
	private Array<TextureRegion> _walkDownFrames;
	
	protected float _frameTime = 0f;
	protected Sprite _frameSprite = null;
	protected TextureRegion _currentFrame = null;
	private Direction _currentDirection = Direction.DOWN;
	private Direction _previousDirection = Direction.UP;
	
	public enum State {
		IDLE, WALKING
	}
	
	public enum Direction {
		UP,RIGHT,DOWN,LEFT;
	}
	
	public EntityAnimation(String spritePath) {
		this._spritePath = spritePath;
		Utility.loadTextureAsset(_spritePath);
		loadSprite();
		loadAllAnimations();
	}
	
	public void update(float delta){
		_frameTime = (_frameTime + delta)%5; //Want to avoid overflow
		updateFrame();
	}
	
	private void loadSprite()
	{
		Texture texture = Utility.getTextureAsset(_spritePath);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		_frameSprite = new Sprite(textureFrames[0][0].getTexture(), 0,0,FRAME_WIDTH, FRAME_HEIGHT);
		_currentFrame = textureFrames[0][0];
	}
	
	private void loadAllAnimations(){
		//Walking animation
		Texture texture = Utility.getTextureAsset(_spritePath);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);

		_walkDownFrames = new Array<TextureRegion>(animationframes);
		_walkLeftFrames = new Array<TextureRegion>(animationframes);
		_walkRightFrames = new Array<TextureRegion>(animationframes);
		_walkUpFrames = new Array<TextureRegion>(animationframes);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < animationframes; j++) {
				//Gdx.app.debug(TAG, "Got frame " + i + "," + j + " from " + sourceImage);
				TextureRegion region = textureFrames[i][j];
				if( region == null ){
					Gdx.app.debug(TAG, "Got null animation frame " + i + "," + j);
				}
				switch(i)
				{
					case 0:
						_walkDownFrames.insert(j, region);
						break;
					case 1:
						_walkLeftFrames.insert(j, region);
						break;
					case 2:
						_walkRightFrames.insert(j, region);
						break;
					case 3:
						_walkUpFrames.insert(j, region);
						break;
				}
			}
		}


		_walkDownAnimation = new Animation(0.25f, _walkDownFrames, Animation.PlayMode.LOOP);
		_walkLeftAnimation = new Animation(0.25f, _walkLeftFrames, Animation.PlayMode.LOOP);
		_walkRightAnimation = new Animation(0.25f, _walkRightFrames, Animation.PlayMode.LOOP);
		_walkUpAnimation = new Animation(0.25f, _walkUpFrames, Animation.PlayMode.LOOP);
	}
	
	public Sprite getFrameSprite(){
		return _frameSprite;
	}

	public TextureRegion getFrame(){
		return _currentFrame;
	}

	public String get_spritePath() {
		return _spritePath;
	}
	
	public void setDirection(Direction direction,  float deltaTime){
		this._previousDirection = this._currentDirection;
		this._currentDirection = direction;
		updateFrame();
	}
	
	public void updateFrame() {
		switch (_currentDirection) {
		case DOWN :
			_currentFrame = _walkDownAnimation.getKeyFrame(_frameTime);
			break;
		case LEFT :
			_currentFrame = _walkLeftAnimation.getKeyFrame(_frameTime);
			break;
		case UP :
			_currentFrame = _walkUpAnimation.getKeyFrame(_frameTime);
			break;
		case RIGHT :
			_currentFrame = _walkRightAnimation.getKeyFrame(_frameTime);
			break;
		default:
			break;
		}
	}
	
	public void setFramePos(TiledMapPosition pos) {
		_frameSprite.setX(pos.getRealX());
		_frameSprite.setY(pos.getRealY());
	}
}
