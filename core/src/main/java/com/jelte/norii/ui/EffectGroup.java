package com.jelte.norii.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EffectGroup extends Table {

	private FrameBuffer fbo;
	private String vertexShader;
	private String fragmentShader;
	private ShaderProgram shaderProgram;
	private float time;

	private boolean disabled;

	private float shockWavePositionX;
	private float shockWavePositionY;

	public EffectGroup(String vertexShaderFile, String fragmentShaderFile) {
		disabled = true;
		time = 0;
		vertexShader = Gdx.files.internal(vertexShaderFile).readString();
		fragmentShader = Gdx.files.internal(fragmentShaderFile).readString();
		shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
		shaderProgram.pedantic = false;

		fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}

	public EffectGroup(SpriteBatch batch, String fragmentShaderFile) {
		time = 0;
		shaderProgram = new ShaderProgram(batch.getShader().getVertexShaderSource(), Gdx.files.internal(fragmentShaderFile).readString());
		shaderProgram.pedantic = false;
		if (!shaderProgram.isCompiled()) {
			System.out.println(shaderProgram.getLog());
		}
		fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}

	public void start(float posX, float posY) {
		this.shockWavePositionX = posX;
		this.shockWavePositionY = posY;
		RunnableAction enable = new RunnableAction();
		enable.setRunnable(new Runnable() {
			@Override
			public void run() {
				disabled = true;
			}
		});
		this.addAction(Actions.delay(1, enable));
		disabled = false;
		time = 0;

	}

	@Override
	public void act(float delta) {
		super.act(delta);
		time += delta;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (disabled) {
			super.draw(batch, parentAlpha);
		} else {
			batch.end();
			batch.flush();
			fbo.begin();
			batch.begin();
			super.draw(batch, parentAlpha);
			batch.end();
			batch.flush();
			fbo.end();
			batch.setShader(shaderProgram);
			shaderProgram.bind();
			shaderProgram.setUniformf("u_amount", 10);
			shaderProgram.setUniformf("u_speed", .5f);
			shaderProgram.setUniformf("u_time", time);
			shaderProgram.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.begin();
			Texture texture = fbo.getColorBufferTexture();
			TextureRegion textureRegion = new TextureRegion(texture);
			// and.... FLIP! V (vertical) only
			textureRegion.flip(false, true);
			batch.draw(textureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.setShader(null);
		}
	}

	public FrameBuffer getFbo() {
		return fbo;
	}
}