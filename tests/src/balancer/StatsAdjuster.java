package balancer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.Utility;

public class StatsAdjuster {
	private static final int MAGICAL_DEFENSE_ADJUST_AMOUNT = 2;
	private static final int PHYSICAL_DEFENSE_ADJUST_AMOUNT = 2;
	private static final int ATTACK_RANGE_ADJUST_AMOUNT = 2;
	private static final int MAX_HP_ADJUST_AMOUNT = 2;
	private static final int BASIC_ATTACK_COST_ADJUST_AMOUNT = 2;
	private static final int ATTACK_POWER_ADJUST_AMOUNT = 2;
	private static final int AP_COST_ADJUST_AMOUNT = 2;
	private static final int DAMAGE_ADJUST_AMOUNT = 2;
	private static final int RANGE_ADJUST_AMOUNT = 2;
	private static final int AREA_OF_EFFECT_RANGE_ADJUST_AMOUNT = 2;

	public Stats returnAdjustedStatsCopy(Stats stats, int gamesPlayed, int gamesWon) {
		float winrate = gamesWon / gamesPlayed;
		Stats copyStats = stats.makeCopy();

		if (Utility.isBetween(winrate, 0.8f, 1.0f)) {
			adjustStats(copyStats, 3);
		}

		if (Utility.isBetween(winrate, 0.6f, 0.8f)) {
			adjustStats(copyStats, 2);
		}

		if (Utility.isBetween(winrate, 0.5f, 0.6f)) {
			adjustStats(copyStats, 1);
		}

		if (Utility.isBetween(winrate, 0.4f, 0.5f)) {
			adjustStats(copyStats, -1);
		}

		if (Utility.isBetween(winrate, 0.2f, 0.4f)) {
			adjustStats(copyStats, -2);
		}

		if (Utility.isBetween(winrate, 0f, 0.2f)) {
			adjustStats(copyStats, -3);
		}
		return copyStats;
	}

	private void adjustStats(Stats copyStats, int i) {
		if (i > 0) {
			changeStats(copyStats, i, true);
		}

		if (i < 0) {
			changeStats(copyStats, Math.abs(i), false);
		}
	}

	private void changeStats(Stats copyStats, int numberOfStats, boolean strengthen) {
		int counter = numberOfStats;
		while (counter > 0) {
			StatsEnum stat = StatsEnum.getRandomStat();
			changeStat(copyStats, stat, strengthen);
			counter--;
		}
	}

	private void changeStat(Stats copyStats, StatsEnum stat, boolean strengthen) {
		Ability ability = copyStats.getAbility();
		switch (stat) {
		case MAGICAL_DEFENSE:
			int magDef = copyStats.getMagicalDefense();
			copyStats.setMagicalDefense(calculateNewValue(magDef, MAGICAL_DEFENSE_ADJUST_AMOUNT, strengthen));
			break;
		case PHYSICAL_DEFENSE:
			int phyDef = copyStats.getPhysicalDefense();
			copyStats.setPhysicalDefense(calculateNewValue(phyDef, PHYSICAL_DEFENSE_ADJUST_AMOUNT, strengthen));
			break;
		case ATTACK_RANGE:
			int attackRange = copyStats.getAttackRange();
			copyStats.setAttackRange(calculateNewValue(attackRange, ATTACK_RANGE_ADJUST_AMOUNT, strengthen));
			break;
		case MAX_HP:
			int maxHP = copyStats.getMaxHP();
			copyStats.setMaxHP(calculateNewValue(maxHP, MAX_HP_ADJUST_AMOUNT, strengthen));
			break;
		case BASIC_ATTACK_COST:
			int basicAttackCost = copyStats.getBasicAttackCost();
			copyStats.setBasicAttackCost(calculateNewValue(basicAttackCost, BASIC_ATTACK_COST_ADJUST_AMOUNT, strengthen));
			break;
		case ATTACK_POWER:
			int attackPower = copyStats.getAttackPower();
			copyStats.setAttackPower(calculateNewValue(attackPower, ATTACK_POWER_ADJUST_AMOUNT, strengthen));
			break;
		case AP_COST:
			int apCost = ability.getSpellData().getApCost();
			ability.getSpellData().setApCost(calculateNewValue(apCost, AP_COST_ADJUST_AMOUNT, strengthen));
			break;
		case DAMAGE:
			int damage = ability.getSpellData().getDamage();
			ability.getSpellData().setDamage(calculateNewValue(damage, DAMAGE_ADJUST_AMOUNT, strengthen));
			break;
		case RANGE:
			int range = ability.getSpellData().getRange();
			ability.getSpellData().setRange(calculateNewValue(range, RANGE_ADJUST_AMOUNT, strengthen));
			break;
		case AREA_OF_EFFECT_RANGE:
			int aoeRange = ability.getSpellData().getAreaOfEffectRange();
			ability.getSpellData().setAreaOfEffectRange(calculateNewValue(aoeRange, AREA_OF_EFFECT_RANGE_ADJUST_AMOUNT, strengthen));
			break;
		}

	}

	private int calculateNewValue(int stat, int change, boolean strengthen) {
		if (strengthen) {
			return stat + change;
		} else {
			return stat - change;
		}
	}

	private enum StatsEnum {
		MAGICAL_DEFENSE, PHYSICAL_DEFENSE, ATTACK_RANGE, MAX_HP, BASIC_ATTACK_COST, ATTACK_POWER, AP_COST, DAMAGE, RANGE, AREA_OF_EFFECT_RANGE;

		private static final List<StatsEnum> STATS = Collections.unmodifiableList(Arrays.asList(StatsEnum.values()));
		private static final int SIZE = STATS.size();
		private static final java.util.Random RANDOM = new java.util.Random();

		public static StatsEnum getRandomStat() {
			return STATS.get(RANDOM.nextInt(SIZE));
		}
	}

}
