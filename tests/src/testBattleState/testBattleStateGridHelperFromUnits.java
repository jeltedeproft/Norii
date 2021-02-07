package testBattleState;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.BattleStateGridHelper;
import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.utility.MyPoint;

import HeadlessRunnerTest.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class testBattleStateGridHelperFromUnits {
	BattleStateGridHelper gridHelper = BattleStateGridHelper.getInstance();
	final static MyPoint testPoints[][] = new MyPoint[20][20];
	final static Array<MyPoint> allPoints = new Array<>();
	final MyPoint center = new MyPoint(5, 5);

	@BeforeClass
	public static void setupTestClass() {
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				testPoints[i][j] = new MyPoint(i, j);
				allPoints.add(new MyPoint(i, j));
			}
		}
		allPoints.sort();
		SpellFileReader.loadSpellsInMemory();
	}

	@Before
	public void setupBeforeAllTests() {

	}

	@Test
	public void testLineCellRange2AoeRange2() {
		Ability ability = new Ability(AbilitiesEnum.FIREBALL);
		ability.getSpellData().setAreaOfEffect("CELL");
		ability.getSpellData().setRange(2);
		ability.getSpellData().setAreaOfEffectRange(2);
		ability.getSpellData().setLineOfSight("LINE");
		Array<MyPoint> unitPositions = new Array<>();
		unitPositions.addAll(new MyPoint[] { testPoints[5][6], testPoints[5][7], testPoints[5][4], testPoints[5][3], testPoints[4][5], testPoints[3][5], testPoints[6][5], testPoints[7][5] });
		Array<MyPoint> results = gridHelper.getTargetPositionsInRangeAbility(center, ability, allPoints);
		unitPositions.sort();
		results.sort();
		assertEquals(unitPositions, results);
	}

	@Test
	public void testLineHorizontalLineRange2AoeRange2() {
		Ability ability = new Ability(AbilitiesEnum.FIREBALL);
		ability.getSpellData().setAreaOfEffect("HORIZONTAL_LINE");
		ability.getSpellData().setRange(2);
		ability.getSpellData().setAreaOfEffectRange(2);
		ability.getSpellData().setLineOfSight("LINE");
		Array<MyPoint> unitPositions = new Array<>();
		unitPositions.addAll(new MyPoint[] { testPoints[7][7], testPoints[6][6], testPoints[4][4], testPoints[3][3], testPoints[3][4], testPoints[4][3], testPoints[7][6], testPoints[6][7], testPoints[6][4], testPoints[7][4],
				testPoints[6][3], testPoints[7][3], testPoints[4][6], testPoints[4][7], testPoints[3][6], testPoints[3][7] });
		Array<MyPoint> results = gridHelper.getTargetPositionsInRangeAbility(center, ability, allPoints);
		unitPositions.sort();
		results.sort();
		assertEquals(unitPositions, results);
	}

	@Test
	public void testLineVerticalLineRange2AoeRange2() {
		Ability ability = new Ability(AbilitiesEnum.FIREBALL);
		ability.getSpellData().setAreaOfEffect("VERTICAL_LINE");
		ability.getSpellData().setRange(2);
		ability.getSpellData().setAreaOfEffectRange(2);
		ability.getSpellData().setLineOfSight("LINE");
		Array<MyPoint> unitPositions = new Array<>();
		unitPositions.addAll(new MyPoint[] { testPoints[5][6], testPoints[5][7], testPoints[5][4], testPoints[5][3], testPoints[4][5], testPoints[3][5], testPoints[6][5], testPoints[7][5], testPoints[5][5], testPoints[8][5],
				testPoints[9][5], testPoints[2][5], testPoints[1][5], testPoints[5][8], testPoints[5][9], testPoints[5][2], testPoints[5][1] });
		Array<MyPoint> results = gridHelper.getTargetPositionsInRangeAbility(center, ability, allPoints);
		unitPositions.sort();
		results.sort();
		assertEquals(unitPositions, results);
	}

	@Test
	public void testLineCircleRange2AoeRange2() {
		Ability ability = new Ability(AbilitiesEnum.FIREBALL);
		ability.getSpellData().setAreaOfEffect("CIRCLE");
		ability.getSpellData().setRange(2);
		ability.getSpellData().setAreaOfEffectRange(2);
		ability.getSpellData().setLineOfSight("LINE");
		Array<MyPoint> unitPositions = new Array<>();
		unitPositions.addAll(new MyPoint[] { testPoints[5][5], testPoints[5][6], testPoints[5][7], testPoints[5][8], testPoints[5][9], testPoints[5][4], testPoints[5][3], testPoints[5][2], testPoints[5][1], testPoints[6][5],
				testPoints[6][6], testPoints[6][7], testPoints[6][8], testPoints[6][4], testPoints[6][3], testPoints[6][2], testPoints[7][5], testPoints[7][6], testPoints[7][7], testPoints[7][4], testPoints[7][3], testPoints[8][5],
				testPoints[8][6], testPoints[8][4], testPoints[9][5], testPoints[4][5], testPoints[4][6], testPoints[4][7], testPoints[4][8], testPoints[4][4], testPoints[4][3], testPoints[4][2], testPoints[3][5], testPoints[3][6],
				testPoints[3][7], testPoints[3][4], testPoints[3][3], testPoints[2][5], testPoints[2][6], testPoints[2][4], testPoints[1][5] });
		Array<MyPoint> results = gridHelper.getTargetPositionsInRangeAbility(center, ability, allPoints);
		unitPositions.sort();
		results.sort();
		assertEquals(unitPositions, results);
	}

	@Test
	public void testCastPointsTargetHitLineCellRange2AoeRange2() {
		BattleState battleState = new BattleState(20, 20);
		Ability ability = new Ability(AbilitiesEnum.FIREBALL);
		ability.getSpellData().setAreaOfEffect("CELL");
		ability.getSpellData().setRange(2);
		ability.getSpellData().setAreaOfEffectRange(2);
		ability.getSpellData().setLineOfSight("LINE");
		Set<MyPoint> unitPositions = new HashSet<>(Arrays.asList(testPoints[5][7]));
		MyPoint target = new MyPoint(5, 7);
		Set<MyPoint> results = gridHelper.getAllCastPointsWhereTargetIsHit(ability, target, center, battleState);
		assertEquals(unitPositions, results);

		unitPositions = new HashSet<>();
		target = new MyPoint(5, 12);
		results = gridHelper.getAllCastPointsWhereTargetIsHit(ability, target, center, battleState);
		assertEquals(unitPositions, results);

		unitPositions = new HashSet<>(Arrays.asList(testPoints[3][5]));
		target = new MyPoint(3, 5);
		results = gridHelper.getAllCastPointsWhereTargetIsHit(ability, target, center, battleState);
		assertEquals(unitPositions, results);
	}

	@Test
	public void testCastPointsTargetHitLineHorizontalLineRange2AoeRange2() {
		BattleState battleState = new BattleState(20, 20);
		Ability ability = new Ability(AbilitiesEnum.FIREBALL);
		ability.getSpellData().setAreaOfEffect("HORIZONTAL_LINE");
		ability.getSpellData().setRange(2);
		ability.getSpellData().setAreaOfEffectRange(2);
		ability.getSpellData().setLineOfSight("LINE");
		Set<MyPoint> unitPositions = new HashSet<>(Arrays.asList(testPoints[5][7]));
		MyPoint target = new MyPoint(5, 7);
		Set<MyPoint> results = gridHelper.getAllCastPointsWhereTargetIsHit(ability, target, center, battleState);
		assertEquals(unitPositions, results);

		unitPositions = new HashSet<>();
		target = new MyPoint(5, 12);
		results = gridHelper.getAllCastPointsWhereTargetIsHit(ability, target, center, battleState);
		assertEquals(unitPositions, results);

		unitPositions = new HashSet<>(Arrays.asList(testPoints[3][5]));
		target = new MyPoint(3, 5);
		results = gridHelper.getAllCastPointsWhereTargetIsHit(ability, target, center, battleState);
		assertEquals(unitPositions, results);
	}
}
