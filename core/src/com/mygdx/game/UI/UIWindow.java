package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

public abstract class UIWindow extends Window{
    protected float tileWidthPixel;
    protected float tileHeightPixel;
    
    private static final float WIDTH = 2.5f;
    private static final float HEIGHT = 3.3f;
    
    private static final int TILE_TO_PIXEL_RATIO = 20;
    private static final float FONT_SCALE_FACTOR = 0.0015f;
    private static final int ALPHA = 80;
    
    public UIWindow(String name) {
    	super(name,Utility.getStatusUISkin());
    	
    	initVariables();
    	setFadeBackgroundEffect();
    }
    
    protected void initVariables() {
    	tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_PIXEL_RATIO;
    	tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_PIXEL_RATIO;
    }
    
	private void setFadeBackgroundEffect() {
		Color newColor = this.getColor();
        newColor.a = ALPHA;
	}
	
    public void update() {
    	if(this.isVisible()) {
        	tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_PIXEL_RATIO;
        	tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_PIXEL_RATIO;
        	this.setSize(WIDTH * tileWidthPixel, HEIGHT * tileHeightPixel);
        	updatePos();
        	updateSize();
    	}
    }
    
    private void updateSize() {
    	for(Actor actor : this.getChildren()) {
    		if(actor.getClass() == Label.class) {
    			updateSizeLabels(actor);
    		}
    		
    		if(actor.getClass() == ImageButton.class) {
    			updateSizeButtons(actor);
    		}
    	}
    }
    
    private void updateSizeLabels(Actor actor) {
    	Label label = (Label) actor;
    	label.setFontScale(Gdx.graphics.getWidth() * FONT_SCALE_FACTOR, Gdx.graphics.getHeight() * FONT_SCALE_FACTOR);
    }
    
    private void updateSizeButtons(Actor actor) {
    	ImageButton button = (ImageButton) actor;
    	if(button.getImage() != null) {
    		this.getCell(actor).size(Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH,Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT);
    	}
    }
    
    protected abstract void configureMainWindow();
    protected abstract void createWidgets();
    protected abstract void addWidgets();
    protected abstract void updatePos();
}
