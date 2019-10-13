package com.mygdx.game.Entities;

import java.util.UUID;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Entities.EntityAnimation.Direction;
import com.mygdx.game.Entities.EntityAnimation.State;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.UI.ActionsUI;
import com.mygdx.game.UI.StatusUI;

import Utility.TiledMapPosition;
import Utility.Utility;

public class Entity extends Actor implements EntitySubject{
	private static final String TAG = Entity.class.getSimpleName();
	
	//stats
	private String _entityID;
	private String name;
	private int mp;
	private int maxMP;
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
	
	private Array<EntityObserver> _observers;
	
	public Entity(String name,EntityFilePath entityfilepath){
		this.entityAnimation = new EntityAnimation(entityfilepath.getValue());
		initEntity();
		this.name = name;
	}
	
	public void initEntity(){
		this._observers = new Array<EntityObserver>();
		this._entityID = UUID.randomUUID().toString();
		this._nextPlayerPosition = new TiledMapPosition();
		this._currentPlayerPosition = new TiledMapPosition();
		this.hp = 10;
		this.mp = 3;
		this.maxMP = 3;
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
		this._currentPlayerPosition.setPositionFromTiles(pos.getTileX(), pos.getTileY());

		//also move the actor linked to this entity
		this.entityactor.setPos(_currentPlayerPosition);
		
		//update the status UI's position
		updateStatusUI();
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
		actionsui.update();
	}

	public boolean isInMovementPhase() {
		return isInMovementPhase;
	}

	public void setInMovementPhase(boolean isInMovementPhase) {
		this.isInMovementPhase = isInMovementPhase;
		if(isInMovementPhase) {
			this.notify(EntityCommand.IN_MOVEMENT);
		}
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
	
    @Override
    public void addObserver(EntityObserver entityObserver) {
        _observers.add(entityObserver);
    }

    @Override
    public void removeObserver(EntityObserver entityObserver) {
        _observers.removeValue(entityObserver, true);
    }

    @Override
    public void removeAllObservers() {
        _observers.removeAll(_observers, true);
    }

    @Override
    public void notify(EntityObserver.EntityCommand command) {
        for(EntityObserver observer: _observers){
            observer.onNotify(command,this);
        }
    }

}
