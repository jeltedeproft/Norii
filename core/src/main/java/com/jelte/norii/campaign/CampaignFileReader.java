package com.jelte.norii.campaign;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class CampaignFileReader {
	private static boolean loaded = false;
	private static ArrayList<CampaignData> campaignLevels = new ArrayList<>();
	private static final String CAMPAIGN_FILE = "campaign/campaign.json";

	private CampaignFileReader() {
	}

	@SuppressWarnings("unchecked")
	public static void loadCampaignInMemory() {
		if (!loaded) {
			final Json json = new Json();
			try {
				campaignLevels = (ArrayList<CampaignData>) json.fromJson(
						ClassReflection.forName("java.util.ArrayList"),
						ClassReflection.forName("com.jelte.norii.campaign.CampaignData"),
						Gdx.files.internal(CAMPAIGN_FILE));
				loaded = true;
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<CampaignData> getCampaignLevels() {
		return campaignLevels;
	}

	public static CampaignData getLevel(int index) {
		return campaignLevels.get(index);
	}

	public static int getLevelCount() {
		return campaignLevels.size();
	}
}
