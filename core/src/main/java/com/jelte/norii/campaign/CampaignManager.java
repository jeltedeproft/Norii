package com.jelte.norii.campaign;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.AITeamLeader.LevelEnum;
import com.jelte.norii.profile.ProfileManager;

public class CampaignManager {
	public static final int TOTAL_LEVELS = 5;

	// Maps campaign level index (0-4) to the LevelEnum used by AITeamLeader
	private static final LevelEnum[] LEVEL_ENUMS = {
			LevelEnum.CAMPAIGN_1,
			LevelEnum.CAMPAIGN_2,
			LevelEnum.CAMPAIGN_3,
			LevelEnum.CAMPAIGN_4,
			LevelEnum.CAMPAIGN_5
	};

	private static CampaignManager instance;

	private boolean campaignBattleActive = false;
	private int activeLevelIndex = 0;

	private CampaignManager() {
	}

	public static CampaignManager getInstance() {
		if (instance == null) {
			instance = new CampaignManager();
		}
		return instance;
	}

	public void startCampaignBattle(int levelIndex) {
		this.activeLevelIndex = levelIndex;
		this.campaignBattleActive = true;
	}

	public void endCampaignBattle() {
		campaignBattleActive = false;
	}

	/** Call after player wins a campaign battle. Saves progress and grants reward unit. */
	public void completeCampaignLevel() {
		int progress = ProfileManager.getInstance().getCampaignProgress();
		if (activeLevelIndex > progress) {
			ProfileManager.getInstance().setCampaignProgress(activeLevelIndex);
		}
		grantRewardUnit(activeLevelIndex);
	}

	private void grantRewardUnit(int levelIndex) {
		CampaignData level = CampaignFileReader.getLevel(levelIndex);
		String reward = level.getRewardUnit();
		if (reward == null || reward.isEmpty()) {
			return;
		}
		Array<String> available = ProfileManager.getInstance().getAvailableHeroes();
		if (available == null) {
			available = new Array<>();
		}
		if (!available.contains(reward, false)) {
			available.add(reward);
			ProfileManager.getInstance().setAvailableHeroes(available);
		}
	}

	/** Returns true if there are more levels after the current one. */
	public boolean hasNextLevel() {
		return activeLevelIndex < TOTAL_LEVELS - 1;
	}

	public int getNextLevelIndex() {
		return activeLevelIndex + 1;
	}

	public boolean isCampaignBattleActive() {
		return campaignBattleActive;
	}

	public int getActiveLevelIndex() {
		return activeLevelIndex;
	}

	public CampaignData getActiveLevel() {
		return CampaignFileReader.getLevel(activeLevelIndex);
	}

	public LevelEnum getLevelEnum(int index) {
		return LEVEL_ENUMS[index];
	}

	/** Returns highest campaign level index completed, or -1 if none. */
	public int getHighestCompletedLevel() {
		return ProfileManager.getInstance().getCampaignProgress();
	}

	public boolean isLevelUnlocked(int index) {
		return index == 0 || index <= getHighestCompletedLevel() + 1;
	}

	public boolean isLevelCompleted(int index) {
		return index <= getHighestCompletedLevel();
	}
}
