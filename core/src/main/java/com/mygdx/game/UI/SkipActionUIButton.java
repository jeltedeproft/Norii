package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;

public class SkipActionUIButton extends ActionUIButton{
	public SkipActionUIButton(final ActionsUI ui,String imageFileName, final Entity linkedUnit) {
		super(imageFileName);
		this.active = true;

		button.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y) {
		    	ui.setVisible(false);
		    	linkedUnit.notifyEntityObserver(EntityCommand.SKIP);
		    }
		});
	}



}
