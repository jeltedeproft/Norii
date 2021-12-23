package balancer;

import com.jelte.norii.magic.Ability;

public class AbilityStats {
	private Ability ability;

	// stats
	private int apCost;
	private int mpCost;
	private int damage;
	private int range;
	private int areaOfEffectRange;

	public AbilityStats(Ability ability) {
		apCost = ability.getSpellData().getApCost();
		mpCost = ability.getSpellData().getMpCost();
		damage = ability.getSpellData().getDamage();
		range = ability.getSpellData().getRange();
		areaOfEffectRange = ability.getSpellData().getAreaOfEffectRange();
	}

	public Ability getAbility() {
		return ability;
	}

	public int getApCost() {
		return apCost;
	}

	public int getMpCost() {
		return mpCost;
	}

	public int getDamage() {
		return damage;
	}

	public int getRange() {
		return range;
	}

	public int getAreaOfEffectRange() {
		return areaOfEffectRange;
	}

}
