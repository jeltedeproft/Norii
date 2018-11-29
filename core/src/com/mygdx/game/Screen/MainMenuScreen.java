package com.mygdx.game.Screen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.game.Utility;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.Owner;
import com.mygdx.game.Entities.Player;

public class MainMenuScreen implements Screen {
	private static final String TAG = MainMenuScreen.class.getSimpleName();

	private Stage _stage;
	private ArrayList<Owner> fighters;
	private ArrayList<Entity> monsters;

	public MainMenuScreen(Object... params){
		
		//creation
		_stage = new Stage();
		Table table = new Table();
		table.setFillParent(true);

		Image title = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("bludbourne_title"));
		TextButton newGameButton = new TextButton("New Game", Utility.STATUSUI_SKIN);
		TextButton exitButton = new TextButton("Exit",Utility.STATUSUI_SKIN);


		//Layout
		table.add(title).spaceBottom(75).row();
		table.add(newGameButton).spaceBottom(10).row();
		table.add(exitButton).spaceBottom(10).row();

		_stage.addActor(table);

		//Listeners
		newGameButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				//add the fighters participating in a battle
				fighters = new ArrayList<Owner>();
				monsters = new ArrayList<Entity>();
				monsters.add(new Entity("Fallia"));
				Player.getInstance().setTeam(monsters);
				fighters.add(Player.getInstance());
				ScreenManager.getInstance().showScreen( ScreenEnum.BATTLE,fighters); 
				return true;
			}
								  }
		);

		exitButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.exit();
				return true;
			}
		});
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		_stage.act(delta);
		_stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		_stage.getViewport().setScreenSize(width, height);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(_stage);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		_stage.dispose();
	}
	
}




