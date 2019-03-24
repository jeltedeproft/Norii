package com.mygdx.game.Entities;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Utility;
import com.mygdx.game.Map.Map;
import com.mygdx.game.UI.ActionsUI;
import com.mygdx.game.UI.StatusUI;

public class Entity extends Actor{
	
    public static enum EntityFilePath{
        COMMANDER("sprites/characters/Commander.png"),
        ICARUS("sprites/characters/Icarus.png"),
        DEMON("sprites/characters/Demon.png"),
        SHAMAN("sprites/characters/Shaman.png");

        private String _entityFullFilePath;

        EntityFilePath(String entityFullFilePath){
            this._entityFullFilePath = entityFullFilePath;
        }

        public String getValue(){
            return _entityFullFilePath;
        }
    }
	
	private static final String TAG = Entity.class.getSimpleName();
	private static final int animationframes = 3;
	private static String _defaultSpritePath;

	private Vector2 _velocity;
	private String _entityID;
	
	private StatusUI statusui;
	private ActionsUI actionsui;
	
	private String name;

	//stats
	private int mp;
	private int hp;
	private int level;
	private int xp;
	private int ini;
	private boolean inBattle;
	private boolean isActive;
	private boolean isInMovementPhase;
	private boolean isInActionPhase;

	private Direction _currentDirection = Direction.LEFT;
	private Direction _previousDirection = Direction.UP;

	private Animation<TextureRegion> _walkLeftAnimation;
	private Animation<TextureRegion> _walkRightAnimation;
	private Animation<TextureRegion> _walkUpAnimation;
	private Animation<TextureRegion> _walkDownAnimation;

	private Array<TextureRegion> _walkLeftFrames;
	private Array<TextureRegion> _walkRightFrames;
	private Array<TextureRegion> _walkUpFrames;
	private Array<TextureRegion> _walkDownFrames;

	protected Vector2 _nextPlayerPosition;
	protected Vector2 _currentPlayerPosition;
	protected State _state = State.IDLE;
	protected float _frameTime = 0f;
	protected Sprite _frameSprite = null;
	protected TextureRegion _currentFrame = null;
	protected EntityActor entityactor;

	public final int FRAME_WIDTH = 32;
	public final int FRAME_HEIGHT = 32;
	public static Rectangle boundingBox;

	public enum State {
		IDLE, WALKING
	}
	
	public enum Direction {
		UP,RIGHT,DOWN,LEFT;
	}
	
	public Entity(String name,EntityFilePath entityfilepath){
		this._defaultSpritePath = entityfilepath.getValue();
		initEntity();
		this.name = name;
	}
	
	public void initEntity(){
		//Gdx.app.debug(TAG, "Construction" );
		this._entityID = UUID.randomUUID().toString();
		this._nextPlayerPosition = new Vector2();
		this._currentPlayerPosition = new Vector2();
		this.boundingBox = new Rectangle();
		this._velocity = new Vector2(2f,2f);
		this.hp = 10;
		this.mp = 3;
		this.ini = Utility.getRandomIntFrom1to(100);
		this.inBattle = false;
		this.isInMovementPhase = false;

		Utility.loadTextureAsset(_defaultSpritePath);
		loadDefaultSprite();
		loadAllAnimations();
	}

	public boolean isInBattle() {
		return inBattle;
	}

	public void setInBattle(boolean inBattle) {
		this.inBattle = inBattle;
	}
	
	public boolean isInActionPhase() {
		return isInActionPhase;
	}

	public void setInActionPhase(boolean isInActionPhase) {
		this.isInActionPhase = isInActionPhase;
	}

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
		updateStatusUI();
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
		updateStatusUI();
	}

	public int getIni() {
		return ini;
	}

	public void setIni(int ini) {
		this.ini = ini;
		updateStatusUI();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
		updateStatusUI();
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
		updateStatusUI();
	}
	
	public void update(float delta){
		_frameTime = (_frameTime + delta)%5; //Want to avoid overflow

		//Gdx.app.debug(TAG, "frametime: " + _frameTime );

		//We want the hitbox to be at the feet for a better feel
		setBoundingBoxSize(0f, 0.5f);
	}

	public void init(){
		this._currentPlayerPosition.x = 0;
		this._currentPlayerPosition.y = 0;
		
		this._nextPlayerPosition.x = 0;
		this._nextPlayerPosition.y = 0;

		//Gdx.app.debug(TAG, "Calling INIT" );
	}

	public StatusUI getStatusui() {
		return statusui;
	}

	public void setStatusui(StatusUI statusui) {
		this.statusui = statusui;
	}
	
	public ActionsUI getActionsui() {
		return actionsui;
	}

	public void setActionsui(ActionsUI actionsui) {
		this.actionsui = actionsui;
	}

	public void setBoundingBoxSize(float percentageWidthReduced, float percentageHeightReduced){
		//Update the current bounding box
		float width;
		float height;

		float widthReductionAmount = 1.0f - percentageWidthReduced; //.8f for 20% (1 - .20)
		float heightReductionAmount = 1.0f - percentageHeightReduced; //.8f for 20% (1 - .20)

		if( widthReductionAmount > 0 && widthReductionAmount < 1){
			width = FRAME_WIDTH * widthReductionAmount;
		}else{
			width = FRAME_WIDTH;
		}

		if( heightReductionAmount > 0 && heightReductionAmount < 1){
			height = FRAME_HEIGHT * heightReductionAmount;
		}else{
			height = FRAME_HEIGHT;
		}


		if( width == 0 || height == 0){
			Gdx.app.debug(TAG, "Width and Height are 0!! " + width + ":" + height);
		}

		//Need to account for the unitscale, since the map coordinates will be in pixels
		float minX;
		float minY;
		if( Map.UNIT_SCALE > 0 ) {
			minX = _nextPlayerPosition.x / Map.UNIT_SCALE;
			minY = _nextPlayerPosition.y / Map.UNIT_SCALE;
		}else{
			minX = _nextPlayerPosition.x;
			minY = _nextPlayerPosition.y;
		}

		boundingBox.set(minX, minY, width, height);
		//Gdx.app.debug(TAG, "SETTING Bounding Box: (" + minX + "," + minY + ")  width: " + width + " height: " + height);
	}

	public Rectangle getBoundingBox() {
		return boundingBox;
	}

	private void loadDefaultSprite()
	{
		Texture texture = Utility.getTextureAsset(_defaultSpritePath);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		_frameSprite = new Sprite(textureFrames[0][0].getTexture(), 0,0,FRAME_WIDTH, FRAME_HEIGHT);
		_currentFrame = textureFrames[0][0];
	}
	
	private void loadAllAnimations(){
		//Walking animation
		Texture texture = Utility.getTextureAsset(_defaultSpritePath);
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

	public void dispose(){
		Utility.unloadAsset(_defaultSpritePath);
	}
	
	public void setState(State state){
		this._state = state;
	}
	
	public Sprite getFrameSprite(){
		return _frameSprite;
	}

	public TextureRegion getFrame(){
		return _currentFrame;
	}
	
	public Vector2 getCurrentPosition(){
		return _currentPlayerPosition;
	}
	
	public void setCurrentPosition(float currentPositionX, float currentPositionY){
		_frameSprite.setX(currentPositionX);
		_frameSprite.setY(currentPositionY);
		this._currentPlayerPosition.x = currentPositionX;
		this._currentPlayerPosition.y = currentPositionY;

		//also move the actor linked to this entity
		this.entityactor.setPos(new Vector2(currentPositionX,currentPositionY));
		
		//update the status UI's position
		updateStatusUI();
	}
	
	public void setDirection(Direction direction,  float deltaTime){
		this._previousDirection = this._currentDirection;
		this._currentDirection = direction;
		
		//Look into the appropriate variable when changing position

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
	
	public void setNextPositionToCurrent(){
		setCurrentPosition(_nextPlayerPosition.x, _nextPlayerPosition.y);
		//Gdx.app.debug(TAG, "Setting nextPosition as Current: (" + _nextPlayerPosition.x + "," + _nextPlayerPosition.y + ")");
	}
	
	public void calculateNextPosition(Direction currentDirection, float deltaTime){
		float testX = _currentPlayerPosition.x;
		float testY = _currentPlayerPosition.y;
		
		_velocity.scl(deltaTime);
		
		switch (currentDirection) {
		case LEFT : 
		testX -=  _velocity.x;
		break;
		case RIGHT :
		testX += _velocity.x;
		break;
		case UP : 
		testY += _velocity.y;
		break;
		case DOWN : 
		testY -= _velocity.y;
		break;
		default:
			break;
		}
		
		_nextPlayerPosition.x = testX;
		_nextPlayerPosition.y = testY;
		
		//velocity
		_velocity.scl(1 / deltaTime);
	}
	
	public void updateStatusUI() {
		statusui.update();
	}

	public String getName() {
		return name;
	}

	public EntityActor getEntityactor() {
		return entityactor;
	}

	public void setEntityactor(EntityActor entityactor) {
		this.entityactor = entityactor;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isInMovementPhase() {
		return isInMovementPhase;
	}

	public void setInMovementPhase(boolean isInMovementPhase) {
		this.isInMovementPhase = isInMovementPhase;
	}
	
	

}
