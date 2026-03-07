package com.jelte.norii.campaign;

public class CampaignData {
	private int id;
	private String chapter;
	private String name;
	private String subtitle;
	private String introText;
	private String outroText;
	private int enemyLevelId;
	private String rewardUnit;

	public CampaignData() {
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getChapter() { return chapter; }
	public void setChapter(String chapter) { this.chapter = chapter; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getSubtitle() { return subtitle; }
	public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

	public String getIntroText() { return introText; }
	public void setIntroText(String introText) { this.introText = introText; }

	public String getOutroText() { return outroText; }
	public void setOutroText(String outroText) { this.outroText = outroText; }

	public int getEnemyLevelId() { return enemyLevelId; }
	public void setEnemyLevelId(int enemyLevelId) { this.enemyLevelId = enemyLevelId; }

	public String getRewardUnit() { return rewardUnit; }
	public void setRewardUnit(String rewardUnit) { this.rewardUnit = rewardUnit; }
}
