package com.jelte.norii.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.inject.OnCompletion;
import com.jelte.norii.Norii;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
	//// USE THIS CODE FOR A FIXED SIZE APPLICATION
	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(640, 480);
	}
	//// END CODE FOR FIXED SIZE APPLICATION

	//// UNCOMMENT THIS CODE FOR A RESIZABLE APPLICATION
	// PADDING is to avoid scrolling in iframes, set to 20 if you have problems
	// private static final int PADDING = 0;
	//
	// @Override
	// public GwtApplicationConfiguration getConfig() {
	// int w = Window.getClientWidth() - PADDING;
	// int h = Window.getClientHeight() - PADDING;
	// GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(w, h);
	// Window.enableScrolling(false);
	// Window.setMargin("0");
	// Window.addResizeHandler(new ResizeListener());
	// cfg.preferFlash = false;
	// return cfg;
	// }
	//
	// class ResizeListener implements ResizeHandler {
	// @Override
	// public void onResize(ResizeEvent event) {
	// if (Gdx.graphics.isFullscreen()) return;
	// int width = event.getWidth() - PADDING;
	// int height = event.getHeight() - PADDING;
	// getRootPanel().setWidth("" + width + "px");
	// getRootPanel().setHeight("" + height + "px");
	// getApplicationListener().resize(width, height);
	// Gdx.graphics.setWindowedMode(width, height);
	// }
	// }
	//// END OF CODE FOR RESIZABLE APPLICATION

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
