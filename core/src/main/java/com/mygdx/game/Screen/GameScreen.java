package com.mygdx.game.Screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Audio.AudioManager;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Audio.AudioSubject;

public class GameScreen implements Screen, AudioSubject {
	private final Array<AudioObserver> observers;

	public GameScreen() {
		observers = new Array<AudioObserver>();
		this.addObserver(AudioManager.getInstance());
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
		for (final AudioObserver observer : observers) {
			observer.onNotify(command, event);
		}
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		MessageManager.getInstance().update();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
