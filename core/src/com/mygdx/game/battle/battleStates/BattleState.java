package com.mygdx.game.battle.battleStates;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.audio.AudioManager;
import com.mygdx.game.audio.AudioObserver;
import com.mygdx.game.audio.AudioSubject;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.map.TiledMapActor;

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
		System.out.println("clicked on tilemapactor");
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
