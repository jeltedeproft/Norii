package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Entities.Entity;

public class MoveActionUIButton extends ActionUIButton{
	private Entity linkedUnit;

	public MoveActionUIButton(String imageFileName, Action action, final Entity linkedUnit) {
		super(imageFileName, action);
		this.active = true;
		this.linkedUnit = linkedUnit;


		button.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y) {
		    	if(linkedUnit.canMove()) { 
		    		linkedUnit.setInMovementPhase(!linkedUnit.isInMovementPhase());
		    	}	
		    }
		});
	}



}
