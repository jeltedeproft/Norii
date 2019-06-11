package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;

import Utility.Utility;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class PortraitsUI extends VerticalGroup {
	private static final String TAG = PortraitsUI.class.getSimpleName();

	private ArrayList<PortraitUI> portraits;
	private Entity[] entities;

	private final float portraitWidth = 0.1f;
	private final float portraitHeight = 0.1f;
	private final int verticalPadding = 5;
	private final int horizontalPadding = 2;
	
	private int height;
	private int width;


	public PortraitsUI(Entity[] entities){
		//super("", Utility.STATUSUI_SKIN);
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
	}
	
	private void createPortraits(Entity[] entities) {
		for(Entity entity : entities) {
			PortraitUI portrait = new PortraitUI(entity);
			portraits.add(portrait);
			Image portraitImage = portrait.get_heroPortrait();
			this.addActor(portraitImage);
		}
	}

	public void updateSizeContainer() {
		int scaledHeight = (int) (portraitHeight * Gdx.graphics.getHeight());
		int scaledWidth = (int) (portraitWidth * Gdx.graphics.getWidth());
		height = entities.length * (scaledHeight + verticalPadding);
		width = scaledWidth + horizontalPadding;
		this.setSize(width, height);
		
		updateSizePortraits();
	}
	
	private void updateSizePortraits() {
		for(PortraitUI portrait : portraits) {
			int newHeight = height / portraits.size();
			portrait.get_heroPortraitScalable().setMinHeight(newHeight);
			portrait.get_heroPortraitScalable().setMinWidth(width);
		}
	}

	public void HighlightUnit(Entity unit) {
		//TO-DO
	}
}


