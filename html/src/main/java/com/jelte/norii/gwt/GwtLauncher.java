package com.jelte.norii.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.inject.OnCompletion;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.jelte.norii.Norii;

public class GwtLauncher extends GwtApplication {
	// PADDING is to avoid scrolling in iframes, set to 20 if you have problems
	private static final int PADDING = 0;

	@Override
	public GwtApplicationConfiguration getConfig() {
		final int w = Window.getClientWidth() - PADDING;
		final int h = Window.getClientHeight() - PADDING;
		final GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(w, h);
		Window.enableScrolling(false);
		Window.setMargin("0");
		Window.addResizeHandler(new ResizeListener());
		return cfg;
	}

	class ResizeListener implements ResizeHandler {
		@Override
		public void onResize(ResizeEvent event) {
			if (Gdx.graphics.isFullscreen()) {
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			} else {
				final int width = event.getWidth() - PADDING;
				final int height = event.getHeight() - PADDING;
				getRootPanel().setWidth("" + width + "px");
				getRootPanel().setHeight("" + height + "px");
				getApplicationListener().resize(width, height);
				Gdx.graphics.setWindowedMode(width, height);
			}
		}
	}

	@Override
	public ApplicationListener createApplicationListener() {
		return new Norii();
	}

	@Override
	public void onModuleLoad() {
		FreetypeInjector.inject(new OnCompletion() {
			@Override
			public void run() {
				// Replace HtmlLauncher with the class name
				// If your class is called FooBar.java than the line should be
				// FooBar.super.onModuleLoad();
				GwtLauncher.super.onModuleLoad();
			}
		});
	}

}
