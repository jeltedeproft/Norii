package com.mygdx.game.Battle;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Audio.AudioManager;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Audio.AudioSubject;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.TiledMapActor;

public abstract class BattleState implements AudioSubject{
	private Array<AudioObserver> observers;
	
	public abstract void exit();
	
	public BattleState() {
        observers = new Array<AudioObserver>();
        this.addObserver(AudioManager.getInstance());
	}
	
	public void entry() {
		
	}
	
	public void update() {
		
	}
	
	public void clickedOnTile(TiledMapActor actor) {
		
	}
	
	public void clickedOnUnit(Entity entity) {
		
	}
	
	public void keyPressed(int key) {
		
	}
	
	public void buttonPressed(int button) {
		
	}
	
    @Override
    public void addObserver(AudioObserver audioObserver) {
        observers.add(audioObserver);
    }

    @Override
    public void removeObserver(AudioObserver audioObserver) {
        observers.removeValue(audioObserver, true);
    }

    @Override
    public void removeAllObservers() {
        observers.removeAll(observers, true);
    }

    @Override
    public void notify(AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event) {
        for(AudioObserver observer: observers){
            observer.onNotify(command, event);
        }
    }
}
