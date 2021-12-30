package com.jelte.norii.ai.movegenerator;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.battle.battlestate.Move;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;

public class AbilityMoveGenerator implements MoveGenerator {

	@Override
	public List<Move> getMoves(UnitOwner player, BattleState battleState, int ap) {
		List<Move> moves = new ArrayList<>();

		while (ap > 0) {
			moves.add(getMove(player, battleState));
			ap--;
		}

		return moves;
	}

	@Override
	public Array<UnitTurn> getAllMovesUnit(Ability ability, Entity aiUnit, BattleState battleState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Move getMove(UnitOwner player, BattleState battleState) {
		Entity unit = getRandomUnit(player, battleState);
		switch (unit.getAbility().getAbilityEnum()) {
		case ARROW:
			return generateArrowBehaviour(unit, battleState);
		case CRACKLE:
			return generateCrackleBehaviour();
		case EXPLOSION:
			return generateExplosionBehaviour();
		case FIREBALL:
			return generateFireballBehaviour();
		case HAMMERBACK:
			return generateHammerbackBehaviour();
		case HAMMERBACKBACK:
			return generateHammerbackBackBehaviour();
		case HEAL:
			return generateHealBehaviour();
		case HOURGLASS:
			return generateHourGlassBehaviour();
		case ICEFIELD:
			return generateIceFieldBehaviour();
		case INVISIBLE:
			return generateInvisibleBehaviour();
		case LOVE:
			return generateLoveBehaviour();
		case MINDWARP:
			return generateMindwarpBehaviour();
		case PLANT_SHIELD:
			return generatePlantShieldBehaviour();
		case PORTAL:
			return generatePortalBehaviour();
		case PULL:
			return generatePullBehaviour();
		case PUSH:
			return generatePushBehaviour();
		case SUMMON:
			return generateSummonBehaviour();
		case SWAP:
			return generateSwapBehaviour();
		case TRANSPORT:
			return generateTransportBehaviour();
		case TURN_TO_STONE:
			return generateTurnToStoneBehaviour();
		default:
			return null;
		}
	}

	private Move generateArrowBehaviour(Entity randomUnit, BattleState battleState) {
		return null;
	}

	private Move generateCrackleBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateExplosionBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateFireballBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateHammerbackBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateHammerbackBackBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateHealBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateHourGlassBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateIceFieldBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateInvisibleBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateLoveBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateMindwarpBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generatePlantShieldBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generatePortalBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generatePullBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generatePushBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateSummonBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateSwapBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateTransportBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Move generateTurnToStoneBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	private Entity getRandomUnit(UnitOwner player, BattleState battleState) {
		Entity randomUnit;
		if (player.isAI()) {
			randomUnit = battleState.getRandomAiUnit();
		} else {
			randomUnit = battleState.getRandomPlayerUnit();
		}
		return randomUnit;
	}
}
