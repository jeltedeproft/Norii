package com.jelte.norii.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;

public class GameScreen implements Screen {

	public GameScreen() {
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
