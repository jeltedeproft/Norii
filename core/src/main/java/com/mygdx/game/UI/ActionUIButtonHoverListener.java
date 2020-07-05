package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ActionUIButtonHoverListener extends ClickListener {
	ActionUIButton actionUIButton;

	public ActionUIButtonHoverListener(ActionUIButton actionUIButton) {
		this.actionUIButton = actionUIButton;
	}

	@Override
	public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		actionUIButton.showPopUp();
	}

	@Override
	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		actionUIButton.hidePopUp();
	}

}
