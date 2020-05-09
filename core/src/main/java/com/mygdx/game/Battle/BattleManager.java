package com.mygdx.game.Battle;

import java.util.HashMap;

import org.xguzm.pathfinding.grid.GridCell;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Magic.Ability;
import com.mygdx.game.Map.BattleMap;

public class BattleManager {
	private BattleState deploymentBattleState;
	private BattleState movementBattleState;
	private BattleState attackBattleState;
	private BattleState spellBattleState;
	private BattleState actionBattleState;
	private BattleState waitOpponentBattleState;
	private BattleState currentBattleState;

	private Entity activeUnit;
	private int activeUnitIndex;
	private final int numberOfUnits;
	private Ability currentSpell;

	private final Entity[] sortedUnits;
	private final HashMap<Entity, boolean[][]> walkPosibilitiesForUnit;
	private final HashMap<Entity, boolean[][]> spellPosibilitiesForUnit;

	public BattleManager(final Entity[] allSortedUnits) {
		sortedUnits = allSortedUnits;
		activeUnitIndex = 0;
		numberOfUnits = sortedUnits.length;
		activeUnit = allSortedUnits[activeUnitIndex];
		walkPosibilitiesForUnit = new HashMap<Entity, boolean[][]>();
		spellPosibilitiesForUnit = new HashMap<Entity, boolean[][]>();

		deploymentBattleState = new DeploymentBattleState(this);
		movementBattleState = new MovementBattleState(this);
		attackBattleState = new AttackBattleState(this);
		spellBattleState = new SpellBattleState(this);
		actionBattleState = new ActionBattleState(this);
		waitOpponentBattleState = new WaitOpponentBattleState(this);

		currentBattleState = deploymentBattleState;
		currentBattleState.entry();

	}

	public void nextUnitActive() {
		activeUnit.setActive(false);
		activeUnit.setFocused(false);

		activeUnitIndex = (activeUnitIndex + 1) % numberOfUnits;
		activeUnit = sortedUnits[activeUnitIndex];

		startUnitTurn();
	}

	public void startUnitTurn() {
		activeUnit.setFocused(true);
		activeUnit.setAp(activeUnit.getMaxAp());
		activeUnit.setActive(true);
		activeUnit.applyModifiers();
	}

	public void initializeMoveSpellPosibilities(BattleMap map) {
		for (final Entity unit : sortedUnits) {
			final int height = map.getTilemapHeightInTiles();
			final int width = map.getTilemapWidthInTiles();
			final boolean[][] walkPosibilities = new boolean[height][width];
			final boolean[][] spellPosibilities = new boolean[height][width];
			for (final GridCell[] cellArray : map.getNavLayer().getNodes()) {
				for (final GridCell cell : cellArray) {
					if (cell.isWalkable() && map.getPathfinder().canUnitWalkTo(unit, cell)) {
						walkPosibilities[cell.y][cell.x] = true;
					}

					if (map.getPathfinder().lineOfSight(unit, cell, sortedUnits)) {
						spellPosibilities[cell.y][cell.x] = true;
					}
				}
			}
		}
	}

	public void recalculateMoveAndSpellPosibilitiesForUnit(Entity unit) {

	}

	public boolean[][] getMovePosibilitiesForUnit(Entity unit) {
		return walkPosibilitiesForUnit.get(unit);
	}

	public boolean[][] getSpellPosibilitiesForUnit(Entity unit) {
		return spellPosibilitiesForUnit.get(unit);
	}

	public Entity getActiveUnit() {
		return activeUnit;
	}

	public Entity[] getUnits() {
		return sortedUnits;
	}

	public Ability getCurrentSpell() {
		return currentSpell;
	}

	public void setCurrentSpell(final Ability currentSpell) {
		this.currentSpell = currentSpell;
	}

	public BattleState getDeploymentBattleState() {
		return deploymentBattleState;
	}

	public void setDeploymentBattleState(final BattleState deploymentBattleState) {
		this.deploymentBattleState = deploymentBattleState;
	}

	public BattleState getMovementBattleState() {
		return movementBattleState;
	}

	public void setMovementBattleState(final BattleState movementBattleState) {
		this.movementBattleState = movementBattleState;
	}

	public BattleState getAttackBattleState() {
		return attackBattleState;
	}

	public void setAttackBattleState(final BattleState attackBattleState) {
		this.attackBattleState = attackBattleState;
	}

	public BattleState getSpellBattleState() {
		return spellBattleState;
	}

	public void setSpellBattleState(final BattleState spellBattleState) {
		this.spellBattleState = spellBattleState;
	}

	public BattleState getActionBattleState() {
		return actionBattleState;
	}

	public void setActionBattleState(final BattleState actionBattleState) {
		this.actionBattleState = actionBattleState;
	}

	public BattleState getWaitOpponentBattleState() {
		return waitOpponentBattleState;
	}

	public void setWaitOpponentBattleState(final BattleState waitOpponentBattleState) {
		this.waitOpponentBattleState = waitOpponentBattleState;
	}

	public BattleState getCurrentBattleState() {
		return currentBattleState;
	}

	public void setCurrentBattleState(final BattleState currentBattleState) {
		this.currentBattleState = currentBattleState;
	}

	public HashMap<Entity, boolean[][]> getWalkPosibilitiesForUnit() {
		return walkPosibilitiesForUnit;
	}

	public HashMap<Entity, boolean[][]> getSpellPosibilitiesForUnit() {
		return spellPosibilitiesForUnit;
	}
}
