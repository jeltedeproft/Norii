package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;

import Utility.Utility;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

public class PortraitsUI extends VerticalGroup {
	private static final String TAG = PortraitsUI.class.getSimpleName();
	
    private ArrayList<PortraitUI> portraits;
    
    private int width;
    private int height;
    private final int portraitWidth = 64;
    private final int portraitHeight = 64;
    private final int verticalPadding = 20;
    private final int horizontalPadding = 10;
    

    public PortraitsUI(Entity[] entities){
        //super("", Utility.STATUSUI_SKIN);
        
        //test
        Label heroNameLabel = new Label("heroes", Utility.STATUSUI_SKIN, "inventory-item-count");
        //this.add(heroNameLabel);
        this.addActor(heroNameLabel);
        
        portraits = new ArrayList<PortraitUI>();

        VerticalGroup vg = new VerticalGroup();
        vg.setFillParent(true);
        
        //create PortraitUI for every unit
        for(Entity entity : entities) {
        	portraits.add(new PortraitUI(entity));
        }
        
        //display PortraitUI for every unit
        for(PortraitUI portrait : portraits) {
        	vg.addActor(portrait.get_heroPortrait());
        	//this.add(portrait.get_heroPortrait());
        	this.addActor(portrait.get_heroPortrait());
        }       

        //this.add(vg);
        //defaults().expand().fill();
        
        //set the size relative to the amount of units
        height = entities.length * (portraitHeight + verticalPadding);
        width = portraitWidth + horizontalPadding;
    	this.setSize(width, height);

    }
    
    
    public void HighlightUnit(Entity unit) {
    	//TO-DO
    }
}


