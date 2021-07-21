package com.jelte.norii.screen;

import com.badlogic.gdx.Screen;
import com.jelte.norii.ai.AITeams;

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
			final AITeams aiTeams = (AITeams) params[ScreenEnum.ScreenParams.AI_TEAM.ordinal()];
			return new BattleScreen(aiTeams);
		}
	},
	SETTINGS {
		@Override
		public Screen getScreen(Object... params) {
			return new SettingsScreen();
		}
	},
	TEAM {
		@Override
		public Screen getScreen(Object... params) {
			return new SetTeamScreen();
		}
	},
	MULTIPLAYER {
		@Override
		public Screen getScreen(Object... params) {
			return new MultiplayerScreen();
		}
	};

	public abstract Screen getScreen(Object... params);

	public enum ScreenParams {
		AI_TEAM;
	}
}