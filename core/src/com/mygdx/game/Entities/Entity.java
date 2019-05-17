package com.mygdx.game.Entities;

import java.util.UUID;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.Entities.EntityAnimation.Direction;
import com.mygdx.game.Entities.EntityAnimation.State;
import com.mygdx.game.UI.ActionsUI;
import com.mygdx.game.UI.StatusUI;

import Utility.TiledMapPosition;
import Utility.Utility;

public class Entity extends Actor{
	private static final String TAG = Entity.class.getSimpleName();
	
	//stats
	private String _entityID;
	private String name;
	private int mp;
	private int hp;
	private int level;
	private int xp;
	private int ini;
	private boolean inBattle;
	private boolean isActive;
	private boolean isInMovementPhase;
	private boolean isInActionPhase;
	
	private TiledMapPosition _velocity;
	protected TiledMapPosition _nextPlayerPosition;
	protected TiledMapPosition _currentPlayerPosition;
	protected State _state = State.IDLE;
	
	private StatusUI statusui;
	private ActionsUI actionsui;
	
	private EntityAnimation entityAnimation;
	protected EntityActor entityactor;
	
	public Entity(String name,EntityFilePath entityfilepath){
		this.entityAnimation = new EntityAnimation(entityfilepath.getValue());
		initEntity();
		this.name = name;
	}
	
	public void initEntity(){
		//Gdx.app.debug(TAG, "Construction" );
		this._entityID = UUID.randomUUID().toString();
		this._nextPlayerPosition = new TiledMapPosition(0,0);
		this._currentPlayerPosition = new TiledMapPosition(0,0);
		this._velocity = new TiledMapPosition(2f,2f);
		this.hp = 10;
		this.mp = 3;
		this.ini = Utility.getRandomIntFrom1to(100);
		this.inBattle = false;
		this.isInMovementPhase = false;
	}
	
	public void update(float delta){
		this.entityAnimation.update(delta);
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

	public void dispose(){
		Utility.unloadAsset(this.entityAnimation.get_spritePath());
	}
	
	public void setState(State state){
		this._state = state;
	}
	

	public TiledMapPosition getCurrentPosition(){
		return _currentPlayerPosition;
	}
	
	public void setCurrentPosition(TiledMapPosition pos){
		entityAnimation.setFramePos(pos);
		this._currentPlayerPosition.setPosition(pos.getRealX(), pos.getRealY());

		//also move the actor linked to this entity
		this.entityactor.setPos(_currentPlayerPosition);
		
		//update the status UI's position
		updateStatusUI();
	}
	

	
	public void setNextPositionToCurrent(){
		setCurrentPosition(_nextPlayerPosition);
		//Gdx.app.debug(TAG, "Setting nextPosition as Current: (" + _nextPlayerPosition.x + "," + _nextPlayerPosition.y + ")");
	}
	
	public void calculateNextPosition(Direction currentDirection, float deltaTime){
		float testX = _currentPlayerPosition.getRealX();
		float testY = _currentPlayerPosition.getRealY();
		
		_velocity.scl(deltaTime);
		
		switch (currentDirection) {
		case LEFT : 
		testX -=  _velocity.getRealX();
		break;
		case RIGHT :
		testX += _velocity.getRealX();
		break;
		case UP : 
		testY += _velocity.getRealY();
		break;
		case DOWN : 
		testY -= _velocity.getRealY();
		break;
		default:
			break;
		}
		
		_nextPlayerPosition.setPosition(testX, testY);
		
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
	
	public boolean canMove() {
		return (this.mp > 0);
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
	
	public TextureRegion getFrame() {
		return entityAnimation.getFrame();
	}
	
	public void setDirection(Direction direction, float delta) {
		entityAnimation.setDirection(direction, delta);
	}

}
