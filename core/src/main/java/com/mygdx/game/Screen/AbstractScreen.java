package com.mygdx.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class AbstractScreen extends Stage implements Screen {
	
    protected AbstractScreen() {
        super( new FitViewport(640, 640, new OrthographicCamera()) );
    }
 
    // Subclasses must load actors in this method
    public abstract void buildStage();
 
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
 
        super.act(delta);
        super.draw();
    }
 
    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }
 
    @Override
    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }
 
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
