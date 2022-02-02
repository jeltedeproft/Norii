package com.jelte.norii.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ShadedGroup extends Table {

	private FrameBuffer fbo;
	private String vertexShader;
	private String fragmentShader;
	private ShaderProgram shaderProgram;
	private float time;

	private float shockWavePositionX;
	private float shockWavePositionY;

	public ShadedGroup(String vertexShaderFile, String fragmentShaderFile) {
		time = 0;
		vertexShader = Gdx.files.internal(vertexShaderFile).readString();
		fragmentShader = Gdx.files.internal(fragmentShaderFile).readString();
		shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
		shaderProgram.pedantic = false;
		if (!shaderProgram.isCompiled()) {
			System.out.println(shaderProgram.getLog());
		}
		fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}

	public ShadedGroup(SpriteBatch batch, String fragmentShaderFile) {
		time = 0;
		shaderProgram = new ShaderProgram(batch.getShader().getVertexShaderSource(), Gdx.files.internal(fragmentShaderFile).readString());
		shaderProgram.pedantic = false;
		if (!shaderProgram.isCompiled()) {
			System.out.println(shaderProgram.getLog());
		}
		fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		time += delta;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		batch.flush();
		fbo.begin();
		batch.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.draw(batch, parentAlpha);
		batch.end();
		batch.flush();
		fbo.end();
		batch.begin();
		batch.setShader(shaderProgram);
		Vector2 v = new Vector2(shockWavePositionX, shockWavePositionY);
		v.x = v.x / Gdx.graphics.getWidth();
		v.y = v.y / Gdx.graphics.getHeight();
		shaderProgram.setUniformf("time", time);
		shaderProgram.setUniformf("center", v);
		Texture texture = fbo.getColorBufferTexture();
		TextureRegion textureRegion = new TextureRegion(texture);
		// and.... FLIP! V (vertical) only
		textureRegion.flip(false, true);
		batch.draw(textureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.setShader(null);
	}
}