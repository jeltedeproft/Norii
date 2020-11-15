package com.jelte.norii.testUI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.profile.ProfileObserver;

public class MainUI extends Table implements ProfileObserver {

	private Stage stage;

	public MainUI() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onNotify(ProfileManager profileManager, ProfileEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public Stage getStage() {
		return stage;
	}

}
