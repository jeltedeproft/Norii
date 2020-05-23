package com.mygdx.game.Screen;

import com.badlogic.gdx.Screen;
import com.mygdx.game.AI.AITeams;

public enum ScreenEnum {

	MAIN_MENU {
		@Override
		public Screen getScreen(Object... params) {
			return new MainMenuScreen();
		}
	},
	BATTLE {
		@Override
		public Screen getScreen(Object... params) {
			@SuppressWarnings("unchecked")
			final AITeams aiTeams = (AITeams) params[ScreenEnum.ScreenParams.AI_TEAM.ordinal()];
			return new BattleScreen(aiTeams);
		}
	};

	public abstract Screen getScreen(Object... params);

	public enum ScreenParams {
		AI_TEAM;
	}
}