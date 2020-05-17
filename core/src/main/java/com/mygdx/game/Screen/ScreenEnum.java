package com.mygdx.game.Screen;

import java.util.ArrayList;

import com.badlogic.gdx.Screen;
import com.mygdx.game.AI.AITeams;
import com.mygdx.game.Entities.TeamLeader;

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
			final ArrayList<TeamLeader> players = (ArrayList<TeamLeader>) params[ScreenEnum.ScreenParams.ARRAYLIST_OF_OWNERS.ordinal()];
			final AITeams aiTeams = (AITeams) params[ScreenEnum.ScreenParams.AI_TEAM.ordinal()];
			return new BattleScreen(players, aiTeams);
		}
	};

	public abstract Screen getScreen(Object... params);

	public enum ScreenParams {
		ARRAYLIST_OF_OWNERS, AI_TEAM;
	}
}