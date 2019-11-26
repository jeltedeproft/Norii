package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

public class PortraitsUI extends VerticalGroup {
	private static final String TAG = PortraitsUI.class.getSimpleName();

	private ArrayList<PortraitUI> portraits;
	private Entity[] entities;

	private static final float PORTRAITS_BOTTOM_PADDING = 6;
	private static final float PORTRAIT_WIDTH = 2.0f;
	private static final float PORTRAIT_HEIGHT = 3.0f;
	private static final int VERTICAL_PADDING = 5;
	
	private int portraitsHeight;
	private int portraitsWidth;


	public PortraitsUI(Entity[] entities){
		initializeVariables(entities);
		updateSizeContainer();
		updateSizePortraits();
	}
	
	private void initializeVariables(Entity[] entities) {
		this.entities = entities;
		portraits = new ArrayList<PortraitUI>();
		this.setTransform(true);
		this.expand(true);
		this.fill();
		createPortraits(entities);
		this.setPosition(0, PORTRAITS_BOTTOM_PADDING * Map.TILE_HEIGHT_PIXEL);
	}
	
	private void createPortraits(Entity[] entities) {
		for(Entity entity : entities) {
			PortraitUI portrait = new PortraitUI(entity);
			portraits.add(portrait);
			Image portraitImage = portrait.getHeroPortrait();
			this.addActor(portraitImage);
		}
	}

	public void updateSizeContainer() {
		int scaledHeight = (int) (PORTRAIT_HEIGHT * Map.TILE_HEIGHT_PIXEL);
		int scaledWidth = (int) (PORTRAIT_WIDTH * Map.TILE_WIDTH_PIXEL);
		portraitsHeight = (entities.length * (scaledHeight + VERTICAL_PADDING));
		portraitsWidth = scaledWidth;
		this.setSize(portraitsWidth, portraitsHeight);
		
		updateSizePortraits();
	}
	
	private void updateSizePortraits() {
		for(PortraitUI portrait : portraits) {
			int newHeight = portraitsHeight / portraits.size();
			portrait.getHeroPortraitScalable().setMinHeight(newHeight);
			portrait.getHeroPortraitScalable().setMinWidth(portraitsWidth);
		}
		this.setPosition(0, PORTRAITS_BOTTOM_PADDING * Map.TILE_HEIGHT_PIXEL);
	}

	public void HighlightUnit(Entity unit) {
		//TO-DO
	}
}


