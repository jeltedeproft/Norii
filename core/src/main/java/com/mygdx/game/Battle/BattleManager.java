package com.mygdx.game.Battle;

import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.mygdx.game.AI.AITeamLeader;
import com.mygdx.game.Battle.BattleStates.ActionBattleState;
import com.mygdx.game.Battle.BattleStates.AttackBattleState;
import com.mygdx.game.Battle.BattleStates.BattleState;
import com.mygdx.game.Battle.BattleStates.DeploymentBattleState;
import com.mygdx.game.Battle.BattleStates.MovementBattleState;
import com.mygdx.game.Battle.BattleStates.SelectUnitBattleState;
import com.mygdx.game.Battle.BattleStates.SpellBattleState;
import com.mygdx.game.Entities.AiEntity;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.PlayerEntity;
import com.mygdx.game.Magic.Ability;
import com.mygdx.game.Map.MyPathFinder;

public class BattleManager {
	private BattleState deploymentBattleState;
	private BattleState selectUnitBattleState;
	private BattleState movementBattleState;
	private BattleState attackBattleState;
	private BattleState spellBattleState;
	private BattleState actionBattleState;
	private BattleState currentBattleState;

	private PlayerEntity activeUnit;
	private AITeamLeader aiTeamLeader;
	private Ability currentSpell;
	private MyPathFinder pathFinder;
	private boolean playerTurn;

	private List<PlayerEntity> playerUnits;
	private List<AiEntity> aiUnits;
	private Entity lockedUnit;

	public BattleManager(final List<PlayerEntity> playerUnits, final List<AiEntity> aiUnits, AITeamLeader aiTeamLeader) {
		initVariables(playerUnits, aiUnits, aiTeamLeader);

		deploymentBattleState = new DeploymentBattleState(this);
		selectUnitBattleState = new SelectUnitBattleState(this);
		movementBattleState = new MovementBattleState(this);
		attackBattleState = new AttackBattleState(this);
		spellBattleState = new SpellBattleState(this);
		actionBattleState = new ActionBattleState(this);

		currentBattleState = deploymentBattleState;
		currentBattleState.entry();
	}

	private void initVariables(final List<PlayerEntity> playerUnits, final List<AiEntity> aiUnits, AITeamLeader aiTeamLeader) {
		this.playerUnits = playerUnits;
		this.aiUnits = aiUnits;
		this.aiTeamLeader = aiTeamLeader;
		activeUnit = playerUnits.get(0);
		playerTurn = true;
		lockedUnit = null;
	}

	public void setUnitActive(Entity entity) {
		PlayerEntity playerEntity = (PlayerEntity) entity;
		activeUnit.setFocused(false);
		activeUnit.setActive(false);

		activeUnit = playerEntity;
		activeUnit.setFocused(true);
		activeUnit.setActive(true);
	}

	public void swapTurn() {
		playerUnits.forEach(PlayerEntity::applyModifiers);
		aiUnits.forEach(AiEntity::applyModifiers);
		playerTurn = !playerTurn;

		if (!playerTurn) {
			aiTeamLeader.act(playerUnits, aiUnits);
		}

		setCurrentBattleState(getSelectUnitBattleState());
		getCurrentBattleState().entry();

	}

	public void setPathFinder(MyPathFinder myPathFinder) {
		this.pathFinder = myPathFinder;
	}

	public MyPathFinder getPathFinder() {
		return pathFinder;
	}

	public PlayerEntity getActiveUnit() {
		return activeUnit;
	}

	public List<PlayerEntity> getPlayerUnits() {
		return playerUnits;
	}

	public List<AiEntity> getAiUnits() {
		return aiUnits;
	}

	public Ability getCurrentSpell() {
		return currentSpell;
	}

	public void setCurrentSpell(final Ability currentSpell) {
		this.currentSpell = currentSpell;
	}

	public boolean isPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
	}

	public Entity getLockedUnit() {
		return lockedUnit;
	}

	public void setLockedUnit(Entity lockedUnit) {
		this.lockedUnit = lockedUnit;
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

	public BattleState getCurrentBattleState() {
		return currentBattleState;
	}

	public void setCurrentBattleState(final BattleState currentBattleState) {
		this.currentBattleState = currentBattleState;
	}

	public BattleState getSelectUnitBattleState() {
		return selectUnitBattleState;
	}

	public void setSelectUnitBattleState(BattleState selectUnitBattleState) {
		this.selectUnitBattleState = selectUnitBattleState;
	}

	public List<Entity> getUnits() {
		return ListUtils.union(playerUnits, aiUnits);
	}
}
