package testBalancing.helpClasses;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.Utility;

public class StatsAdjuster {
	// UNIT
	private static final int MAGICAL_DEFENSE_ADJUST_AMOUNT = 3;
	private static final int PHYSICAL_DEFENSE_ADJUST_AMOUNT = 3;
	private static final int ATTACK_RANGE_ADJUST_AMOUNT = 1;
	private static final int MAX_HP_ADJUST_AMOUNT = 5;
	private static final int BASIC_ATTACK_COST_ADJUST_AMOUNT = 1;
	private static final int ATTACK_POWER_ADJUST_AMOUNT = 1;

	// ABILITY
	private static final int AP_COST_ADJUST_AMOUNT = 1;
	private static final int DAMAGE_ADJUST_AMOUNT = 5;
	private static final int RANGE_ADJUST_AMOUNT = 1;
	private static final int AREA_OF_EFFECT_RANGE_ADJUST_AMOUNT = 1;
	private static final int DURATION_IN_TURNS_ADJUST_AMOUNT = 1;
	private static final int AMOUNT_ADJUST_AMOUNT = 1;

	public Stats returnAdjustedStatsCopy(Stats stats, int gamesPlayed, int gamesWon) {
		float winrate = (float) gamesWon / (float) gamesPlayed;
		Stats copyStats = stats.makeCopy();

		if (Utility.isBetween(winrate, 0.8f, 1.0f)) {
			adjustStats(copyStats, -3);
		}

		if (Utility.isBetween(winrate, 0.6f, 0.8f)) {
			adjustStats(copyStats, -2);
		}

		if (Utility.isBetween(winrate, 0.5f, 0.6f)) {
			adjustStats(copyStats, -1);
		}

		if (Utility.isBetween(winrate, 0.4f, 0.5f)) {
			adjustStats(copyStats, 1);
		}

		if (Utility.isBetween(winrate, 0.2f, 0.4f)) {
			adjustStats(copyStats, 2);
		}

		if (Utility.isBetween(winrate, 0f, 0.2f)) {
			adjustStats(copyStats, 3);
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
			int newmagDefValue = calculateNewValue(magDef, MAGICAL_DEFENSE_ADJUST_AMOUNT, strengthen);
			if (newmagDefValue >= 0) {
				copyStats.setMagicalDefense(newmagDefValue);
			}
			break;
		case PHYSICAL_DEFENSE:
			int phyDef = copyStats.getPhysicalDefense();
			int newPhyDefValue = calculateNewValue(phyDef, PHYSICAL_DEFENSE_ADJUST_AMOUNT, strengthen);
			if (newPhyDefValue >= 0) {
				copyStats.setPhysicalDefense(newPhyDefValue);
			}
			break;
		case ATTACK_RANGE:
			int attackRange = copyStats.getAttackRange();
			int newAtkRangeValue = calculateNewValue(attackRange, ATTACK_RANGE_ADJUST_AMOUNT, strengthen);
			if (newAtkRangeValue > 0) {
				copyStats.setAttackRange(newAtkRangeValue);
			}
			break;
		case MAX_HP:
			int maxHP = copyStats.getMaxHP();
			int newMaxHpValue = calculateNewValue(maxHP, MAX_HP_ADJUST_AMOUNT, strengthen);
			if (newMaxHpValue > 0) {
				copyStats.setMaxHP(newMaxHpValue);
			}
			break;
		case BASIC_ATTACK_COST:
			int basicAttackCost = copyStats.getBasicAttackCost();
			int newBasicAttackCostValue = calculateNewValue(basicAttackCost, BASIC_ATTACK_COST_ADJUST_AMOUNT, !strengthen);
			if (newBasicAttackCostValue > 0) {
				copyStats.setBasicAttackCost(newBasicAttackCostValue);
			}
			break;
		case ATTACK_POWER:
			int attackPower = copyStats.getAttackPower();
			int newAttackPowerValue = calculateNewValue(attackPower, ATTACK_POWER_ADJUST_AMOUNT, strengthen);
			if (newAttackPowerValue > 0) {
				copyStats.setAttackPower(newAttackPowerValue);
			}
			break;
		case AP_COST:
			int apCost = ability.getSpellData().getApCost();
			int newApCostValue = calculateNewValue(apCost, AP_COST_ADJUST_AMOUNT, !strengthen);
			if (newApCostValue > 0) {
				copyStats.setApCost(newApCostValue);
			}
			break;
		case DAMAGE:
			int damage = ability.getSpellData().getDamage();
			int newDamageValue = calculateNewValue(damage, DAMAGE_ADJUST_AMOUNT, strengthen);
			if (newDamageValue > 0) {
				copyStats.setDamage(newDamageValue);
			}
			break;
		case RANGE:
			int range = ability.getSpellData().getRange();
			int newRangeValue = calculateNewValue(range, RANGE_ADJUST_AMOUNT, strengthen);
			if (newRangeValue > 0) {
				copyStats.setRange(newRangeValue);
			}
			break;
		case AREA_OF_EFFECT_RANGE:
			int aoeRange = ability.getSpellData().getAreaOfEffectRange();
			int newAreaOfEffectRangeValue = calculateNewValue(aoeRange, AREA_OF_EFFECT_RANGE_ADJUST_AMOUNT, strengthen);
			if (newAreaOfEffectRangeValue > 0) {
				copyStats.setAreaOfEffectRange(newAreaOfEffectRangeValue);
			}
			break;
		case DURATION_IN_TURNS:
			int durationInTurns = ability.getSpellData().getDurationInTurns();
			int newdurationInTurnsValue = calculateNewValue(durationInTurns, DURATION_IN_TURNS_ADJUST_AMOUNT, strengthen);
			if ((newdurationInTurnsValue != 0) && (newdurationInTurnsValue > 0)) {
				copyStats.setDurationInTurns(newdurationInTurnsValue);
			}
			break;
		case AMOUNT:
			int amount = ability.getSpellData().getAmount();
			int newAmountValue = calculateNewValue(amount, AMOUNT_ADJUST_AMOUNT, strengthen);
			if ((newAmountValue != 0) && (newAmountValue > 0)) {
				copyStats.setDurationInTurns(newAmountValue);
			}
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
		MAGICAL_DEFENSE, PHYSICAL_DEFENSE, ATTACK_RANGE, MAX_HP, BASIC_ATTACK_COST, ATTACK_POWER, AP_COST, DAMAGE, RANGE, AREA_OF_EFFECT_RANGE, DURATION_IN_TURNS, AMOUNT;

		private static final List<StatsEnum> STATS = Collections.unmodifiableList(Arrays.asList(StatsEnum.values()));
		private static final int SIZE = STATS.size();

		public static StatsEnum getRandomStat() {
			return STATS.get(Utility.random.nextInt(SIZE));
		}
	}

}
