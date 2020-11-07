package com.mygdx.game.screen;

import com.badlogic.gdx.Screen;
import com.mygdx.game.ai.AITeams;

public enum ScreenEnum {

	MAIN_MENU {
		@Override
		public Screen getScreen(Object... params) {
			final Screen main = ScreenManager.getMainMenu();
			if (main == null) {
				return new MainMenuScreen();
			} else {
				return ScreenManager.getMainMenu();
			}
		}
	},
	BATTLE {
		@Override
		public Screen getScreen(Object... params) {
			final AITeams aiTeams = (AITeams) params[ScreenEnum.ScreenParams.AI_TEAM.ordinal()];
			return new BattleScreen(aiTeams);
		}
	},
	SETTINGS {
		@Override
		public Screen getScreen(Object... params) {
			return new SettingsScreen();
		}
	};

	public abstract Screen getScreen(Object... params);

	public enum ScreenParams {
		AI_TEAM;
	}
}