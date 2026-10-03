package com.jelte.norii.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Square pixel grid. Scales by a whole number so the skin stays crisp,
 * and falls back to a fit scale only when the window is smaller than the grid.
 */
public class PixelViewport extends Viewport {
	public static final int WIDTH = 480;
	public static final int HEIGHT = 480;

	public PixelViewport() {
		setWorldSize(WIDTH, HEIGHT);
		setCamera(new OrthographicCamera());
	}

	public static void beginFullScreen() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void update(int screenWidth, int screenHeight, boolean centerCamera) {
		float worldWidth = getWorldWidth();
		float worldHeight = getWorldHeight();
		int scale = (int) Math.min(screenWidth / worldWidth, screenHeight / worldHeight);
		int viewportWidth;
		int viewportHeight;
		if (scale < 1) {
			float fitted = Math.min(screenWidth / worldWidth, screenHeight / worldHeight);
			viewportWidth = Math.max(1, Math.round(worldWidth * fitted));
			viewportHeight = Math.max(1, Math.round(worldHeight * fitted));
		} else {
			viewportWidth = (int) worldWidth * scale;
			viewportHeight = (int) worldHeight * scale;
		}
		setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);
		apply(centerCamera);
	}
}
