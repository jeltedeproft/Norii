package com.NoriiTests.BattleTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.InputMultiplexer;
import com.mygdx.game.Battle.BattleManager;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntitySpriteFilePath;

public class BattleManagerTest {
	private BattleManager battleManager;
	private InputMultiplexer inputMiltiplexer;
	private Entity[] entities;
	
	@Before
	private void initVariables() {
		this.entities = new Entity[2];
		this.entities[0] = new Entity("testUnit1", EntitySpriteFilePath.COMMANDER);
		this.battleManager = new BattleManager(null, null);
	}
	

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
