package com.mygdx.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Audio.AudioManager;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Audio.AudioSubject;
import com.mygdx.game.UI.PlayerBattleHUD;


public class GameScreen implements Screen, AudioSubject{
	private static final String TAG = GameScreen.class.getSimpleName();
	
    private Array<AudioObserver> _observers;

    public GameScreen(){
        _observers = new Array<AudioObserver>();
        this.addObserver(AudioManager.getInstance());
    }

    @Override
    public void addObserver(AudioObserver audioObserver) {
        _observers.add(audioObserver);
    }

    @Override
    public void removeObserver(AudioObserver audioObserver) {
        _observers.removeValue(audioObserver, true);
    }

    @Override
    public void removeAllObservers() {
        _observers.removeAll(_observers, true);
    }

    @Override
    public void notify(AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event) {
        for(AudioObserver observer: _observers){
            observer.onNotify(command, event);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {
    	Gdx.app.debug(TAG, "resizing with : (" + width + " , " + height + ")");
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
