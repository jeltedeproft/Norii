package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;

public class SkipActionUIButton extends ActionUIButton {
	public SkipActionUIButton(final ActionsUI ui, final String imageFileName, final Entity linkedUnit) {
		super(imageFileName);
		active = true;
		infotext = "use this button to skip your turn";
		actionName = "Skip";
		initPopUp();

		button.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				ui.setVisible(false);
				linkedUnit.notifyEntityObserver(EntityCommand.SKIP);
			}
		});
	}
}
