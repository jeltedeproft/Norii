package com.mygdx.game.Battle.BattleStates;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Audio.AudioManager;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Audio.AudioSubject;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.TiledMapActor;

public abstract class BattleState implements AudioSubject {
	private Array<AudioObserver> observers;

	public abstract void exit();

	public BattleState() {
		observers = new Array<>();
		this.addAudioObserver(AudioManager.getInstance());
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
	public void addAudioObserver(AudioObserver audioObserver) {
		observers.add(audioObserver);
	}

	@Override
	public void removeAudioObserver(AudioObserver audioObserver) {
		observers.removeValue(audioObserver, true);
	}

	@Override
	public void removeAllAudioObservers() {
		observers.removeAll(observers, true);
	}

	@Override
	public void notifyAudio(AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event) {
		for (AudioObserver observer : observers) {
			observer.onNotify(command, event);
		}
	}
}
