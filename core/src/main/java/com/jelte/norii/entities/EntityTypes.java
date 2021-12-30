package com.jelte.norii.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.jelte.norii.utility.Utility;

//keep this in sync with JSON files. ORDER IS IMPORTANT
public enum EntityTypes {
	COMMANDER("Commander"), // 0
	ICARUS("Icarus"), // 1
	DEMON("Demon"), // 2
	SHAMAN("Shaman"), // 3
	BEAST_OGRE("Beast Ogre"), // 4
	BLACK_CAT("Black Cat"), // 5
	BLACK_DOG("Black Dog"), // 6
	BLACK_GHOST_BALL("Black Ghost Ball"), // 7
	BLACK_SKELETON("Black Skeleton"), // 8
	BLACK_SLIME_DARK_BLUE_EYES("Black Slime Dark Blue Eyes"), // 9
	BLACK_SLIME_GREEN_EYES("Black Slime Green Eyes"), // 10
	BLACK_SLIME_PINK_EYES("Black Slime Pink Eyes"), // 11
	BLACK_SLIME_PURPLE_EYES("Black Slime Purple Eyes"), // 12
	BLACK_SLIME_RED_EYES("Black Slime Red Eyes"), // 13
	BLACK_SLIME("Black Slime"), // 14
	BLACK_SLIME_BLUE_EYES("Black Slime Blue Eyes"), // 15
	BLACK_SLIME_WHITE_EYES("Black Slime White Eyes"), // 16
	BLACK_SLIME_YELLOW_EYES("Black Slime Yellow Eyes"), // 17
	BLUE_GHOSTY("Blue Ghosty"), // 18
	BLUE_SLIME("Blue Slime"), // 19
	DARK_BLUE_SLIME("Dark Blue Slime"), // 20
	FEMALE_EIGHT("Female Eight"), // 21
	FEMALE_ELEVEN("Female Eleven"), // 22
	FEMALE_FIFTEEN("Female Fifteen"), // 23
	FEMALE_FIFTY_FIVE("Female Fifty Five"), // 24
	FEMALE_FIFTY_FOUR("Female Fifty Four"), // 25
	FEMALE_FIFTY("Female Fifty"), // 26
	FEMALE_FIVE("Female Five"), // 27
	FEMALE_FOUR("Female Four"), // 28
	FEMALE_NINE("Female Nine"), // 29
	FEMALE_NINETEEN("Female Nineteen"), // 30
	FEMALE_ONE("Female One"), // 31
	BOOMERANG("Boomerang"), // 32
	PORTAL("Portal"), // 33
	GHOST("Ghost"), // 34
	ROCK("Rock"), // 35
	DARK_MAGE("Dark Mage"), // 36
	RYU("Ryu"), // 37
	ASTROLOGER("Astrologer"), // 38
	ARTIST("Artist"), // 39
	CITIZEN("Citizen"), // 40
	WARRIOR_GIRL("Warrior Girl"); // 41

	private static final List<EntityTypes> TYPES = Collections.unmodifiableList(Arrays.asList(EntityTypes.values()));
	private static final int SIZE = TYPES.size();

	private String entityName;

	public String getEntityName() {
		return entityName;
	}

	EntityTypes(final String entityName) {
		this.entityName = entityName;
	}

	public static EntityTypes randomEntityType() {
		return TYPES.get(Utility.random.nextInt(SIZE));
	}
}
