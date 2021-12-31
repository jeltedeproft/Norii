package com.jelte.norii.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xguzm.pathfinding.NavigationNode;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGridGraph;
import org.xguzm.pathfinding.grid.NavigationGridGraphNode;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import com.badlogic.gdx.Gdx;
import com.jelte.norii.ai.movegenerator.AbilityTargetMoveGenerator;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;
import com.jelte.norii.utility.Utility;

public class MyPathFinder {
	private NavigationGridGraph<GridCell> navGrid;
	private GridFinderOptions gridFinderOptions;
	private AStarGridFinder<GridCell> aStarGridFinder;
	private Map linkedMap;
	private Long oldTime = System.currentTimeMillis();
	private HashMap<MyPoint, HashMap<MyPoint, List<GridCell>>> precalculatedPaths;
	private HashMap<MyPoint, HashMap<MyPoint, Boolean>> precalculatedLineOfSightsWithNonBlockableUnits;
	private int preprocessI = 0;
	private int preprocessJ = 0;
	private boolean preprocessingFinished = false;

	private static MyPathFinder pathFinder;

	public void setMap(final Map map) {
		linkedMap = map;
		navGrid = linkedMap.getNavLayer().navGrid;
		gridFinderOptions = new GridFinderOptions();
		gridFinderOptions.allowDiagonal = false;
		aStarGridFinder = new AStarGridFinder<>(GridCell.class, gridFinderOptions);
		precalculatedPaths = new HashMap<>();
		precalculatedLineOfSightsWithNonBlockableUnits = new HashMap<>();
	}

	public void preprocessMap() {
		if (!preprocessingFinished) {
			if (preprocessI < linkedMap.getMapWidth()) {
				if (preprocessJ < linkedMap.getMapHeight()) {
					final MyPoint point = new MyPoint(preprocessI, preprocessJ);
					precalculatedPaths.put(point, calculatePathsToEveryOtherCell(point));
					precalculatedLineOfSightsWithNonBlockableUnits.put(point, calculateLineOfSightWithoutBlocksToEveryOtherCell(point));
					preprocessJ++;
				} else {
					preprocessJ = 0;
					preprocessI++;
				}
			} else {
				preprocessingFinished = true;
				System.out.println("processing map finished");
			}
		}
	}

	private HashMap<MyPoint, List<GridCell>> calculatePathsToEveryOtherCell(MyPoint point) {
		final HashMap<MyPoint, List<GridCell>> pathsToEveryOtherCell = new HashMap<>();
		for (int i = 0; i < linkedMap.getMapWidth(); i++) {
			for (int j = 0; j < linkedMap.getMapHeight(); j++) {
				final MyPoint end = new MyPoint(i, j);
				final List<GridCell> path = aStarGridFinder.findPath(point.x, point.y, i, j, navGrid);
				if (path != null) {
					final List<GridCell> copy = new ArrayList<>(path);
					pathsToEveryOtherCell.put(end, copy);
				} else {
					pathsToEveryOtherCell.put(end, null);
				}
			}
		}
		return pathsToEveryOtherCell;
	}

	private HashMap<MyPoint, Boolean> calculateLineOfSightWithoutBlocksToEveryOtherCell(MyPoint point) {
		final HashMap<MyPoint, Boolean> lineOfSightEveryOtherCell = new HashMap<>();
		for (int i = 0; i < linkedMap.getMapWidth(); i++) {
			for (int j = 0; j < linkedMap.getMapHeight(); j++) {
				final MyPoint end = new MyPoint(i, j);
				final GridCell unitCell = navGrid.getCell(point.x, point.y);
				final GridCell targetCell = navGrid.getCell(end.x, end.y);
				final NavigationGridGraphNode node = unitCell;
				final NavigationGridGraphNode neigh = targetCell;
				final boolean isLineOfSight = calculateLineOfSight(null, false, node, neigh);
				lineOfSightEveryOtherCell.put(end, isLineOfSight);
			}
		}
		return lineOfSightEveryOtherCell;
	}

	// PATH FINDING
	public boolean canUnitWalkTo(Entity unit, GridCell cell) {
		final GridCell center = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		return isCloseEnough(center, cell, unit.getAp()) && pathExists(center, cell, unit.getAp());
	}

	public boolean canUnitWalkTo(Entity unit, MyPoint point) {
		final GridCell center = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		final GridCell target = navGrid.getCell(point.x, point.y);
		return isCloseEnough(center, target, unit.getAp()) && pathExists(center, target, unit.getAp());
	}

	public boolean canUnitWalkTo(Entity unit, TiledMapPosition pos) {
		final GridCell center = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		final GridCell target = navGrid.getCell(pos.getTileX(), pos.getTileY());
		return isCloseEnough(center, target, unit.getAp()) && pathExists(center, target, unit.getAp());
	}

	public TiledMapPosition getClosestMoveSpotNextToUnit(Entity mover, Entity target) {
		final MyPoint start = new MyPoint(mover.getCurrentPosition().getTileX(), mover.getCurrentPosition().getTileY());
		final MyPoint end = new MyPoint(target.getCurrentPosition().getTileX(), target.getCurrentPosition().getTileY());
		final List<GridCell> path = precalculatedPaths.get(start).get(end);
		for (final GridCell cell : path) {
			if (isNextTo(cell, target.getCurrentPosition())) {
				return new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
			}
		}
		return null;
	}

	public List<GridCell> getPathFromUnitToUnit(Entity mover, Entity target) {
		final MyPoint start = new MyPoint(mover.getCurrentPosition().getTileX(), mover.getCurrentPosition().getTileY());
		final MyPoint end = new MyPoint(target.getCurrentPosition().getTileX(), target.getCurrentPosition().getTileY());
		return removeEndPoint(precalculatedPaths.get(start).get(end));
	}

	private boolean pathExists(final GridCell center, final GridCell gridcell, final int range) {
		final MyPoint start = new MyPoint(center.x, center.y);
		final MyPoint end = new MyPoint(gridcell.x, gridcell.y);
		final List<GridCell> path = precalculatedPaths.get(start).get(end);
		return ((path != null) && (path.size() <= range) && (!path.isEmpty()));
	}

	public List<GridCell> pathTowards(MyPoint start, MyPoint goal, int ap) {
		final TiledMapPosition startPos = new TiledMapPosition().setPositionFromTiles(start.x, start.y);
		final TiledMapPosition goalPos = new TiledMapPosition().setPositionFromTiles(goal.x, goal.y);
		return pathTowards(startPos, goalPos, ap);
	}

	public List<GridCell> pathTowards(TiledMapPosition start, TiledMapPosition goal, int ap) {
		oldTime = System.currentTimeMillis();
		final List<GridCell> path = precalculatedPaths.get(start.getTilePosAsPoint()).get(goal.getTilePosAsPoint());
		oldTime = AbilityTargetMoveGenerator.debugTime("find path from " + start + " to " + goal, oldTime);
		if (path == null) {
			return adjustGoal(start, goal, path);
		}

		if (path.size() <= ap) {
			return path;
		}

		return chippedPath(path, ap);
	}

	private List<GridCell> adjustGoal(TiledMapPosition start, TiledMapPosition goal, List<GridCell> path) {
		while ((path == null) || path.isEmpty()) {
			oldTime = AbilityTargetMoveGenerator.debugTime("trying from : " + start + " to " + goal, oldTime);
			goal = tryAdjacentTile(start, goal);
			path = precalculatedPaths.get(start.getTilePosAsPoint()).get(goal.getTilePosAsPoint());
		}
		oldTime = AbilityTargetMoveGenerator.debugTime("goal adjusted", oldTime);
		Gdx.app.debug("myPathFinder", "path after adjustment = " + path);
		return path;
	}

	private TiledMapPosition tryAdjacentTile(TiledMapPosition start, TiledMapPosition goal) {
		if (start.getTileX() < goal.getTileX()) {
			return goal.changeX(-1);
		} else if (start.getTileX() > goal.getTileX()) {
			return goal.changeX(1);
		} else if (start.getTileY() < goal.getTileY()) {
			return goal.changeY(-1);
		}
		return goal.changeY(1);
	}

	private List<GridCell> chippedPath(List<GridCell> path, int ap) {
		while (path.size() > ap) {
			path.remove(path.size() - 1);
		}
		oldTime = AbilityTargetMoveGenerator.debugTime("path chipped", oldTime);
		return path;
	}

	// LINE OF SIGHT(DOES NOT USE PREPROCESSING)
	public boolean lineOfSight(Entity caster, MyPoint targetPos, List<Entity> sortedUnits, final boolean unitsAreBlocking) {
		final ArrayList<TiledMapPosition> positionsUnits = (ArrayList<TiledMapPosition>) Utility.collectPositionsUnits(sortedUnits);
		final GridCell unitCell = navGrid.getCell(caster.getCurrentPosition().getTileX(), caster.getCurrentPosition().getTileY());
		final GridCell targetCell = navGrid.getCell(targetPos.x, targetPos.y);

		return lineOfSight(unitCell, targetCell, positionsUnits, unitsAreBlocking);
	}

	public boolean lineOfSight(Entity unit, final GridCell to, List<Entity> sortedUnits, final boolean unitsAreBlocking) {
		final ArrayList<TiledMapPosition> positionsUnits = (ArrayList<TiledMapPosition>) Utility.collectPositionsUnits(sortedUnits);
		final GridCell unitCell = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		final GridCell targetCell = navGrid.getCell(to.getX(), to.getY());

		return lineOfSight(unitCell, targetCell, positionsUnits, unitsAreBlocking);
	}

	public boolean lineOfSight(Entity caster, Entity target, List<Entity> sortedUnits, final boolean unitsAreBlocking) {
		final ArrayList<TiledMapPosition> positionsUnits = (ArrayList<TiledMapPosition>) Utility.collectPositionsUnits(sortedUnits);
		final GridCell unitCell = navGrid.getCell(caster.getCurrentPosition().getTileX(), caster.getCurrentPosition().getTileY());
		final GridCell targetCell = navGrid.getCell(target.getCurrentPosition().getTileX(), target.getCurrentPosition().getTileY());

		return lineOfSight(unitCell, targetCell, positionsUnits, unitsAreBlocking);
	}

	public boolean lineOfSight(Entity caster, TiledMapPosition targetPos, List<Entity> sortedUnits, final boolean unitsAreBlocking) {
		final ArrayList<TiledMapPosition> positionsUnits = (ArrayList<TiledMapPosition>) Utility.collectPositionsUnits(sortedUnits);
		final GridCell unitCell = navGrid.getCell(caster.getCurrentPosition().getTileX(), caster.getCurrentPosition().getTileY());
		final GridCell targetCell = navGrid.getCell(targetPos.getTileX(), targetPos.getTileY());

		return lineOfSight(unitCell, targetCell, positionsUnits, unitsAreBlocking);
	}

	public boolean lineOfSight(TiledMapPosition from, TiledMapPosition to) {
		final GridCell unitCell = navGrid.getCell(from.getTileX(), from.getTileY());
		final GridCell targetCell = navGrid.getCell(to.getTileX(), to.getTileY());
		return lineOfSight(unitCell, targetCell, null, false);
	}

	public boolean lineOfSight(MyPoint from, MyPoint to) {
		final GridCell unitCell = navGrid.getCell(from.x, from.y);
		final GridCell targetCell = navGrid.getCell(to.x, to.y);
		return lineOfSight(unitCell, targetCell, null, false);
	}

	public boolean lineOfSight(final NavigationNode from, final NavigationNode to, final List<TiledMapPosition> positionsUnits, final boolean unitsAreBlocking) {
		if ((from == null) || (to == null)) {
			return false;
		}

		final NavigationGridGraphNode node = (NavigationGridGraphNode) from;
		final NavigationGridGraphNode neigh = (NavigationGridGraphNode) to;

		MyPoint startingPoint = new MyPoint(node.getX(), node.getY());
		MyPoint endingPoint = new MyPoint(neigh.getX(), neigh.getY());

		if ((!unitsAreBlocking) && (Boolean.FALSE.equals(precalculatedLineOfSightsWithNonBlockableUnits.get(startingPoint).get(endingPoint)))) {
			return false;
		}

		return calculateLineOfSight(positionsUnits, unitsAreBlocking, node, neigh);
	}

	private boolean calculateLineOfSight(final List<TiledMapPosition> positionsUnits, final boolean unitsAreBlocking, final NavigationGridGraphNode node, final NavigationGridGraphNode neigh) {
		int x1 = node.getX();
		int y1 = node.getY();
		final int x2 = neigh.getX();
		final int y2 = neigh.getY();
		final int dx = Math.abs(x1 - x2);
		final int dy = Math.abs(y1 - y2);
		final int xinc = (x1 < x2)	? 1
									: -1;
		final int yinc = (y1 < y2)	? 1
									: -1;

		int error = dx - dy;

		for (int n = dx + dy; n > 0; n--) {
			final int e2 = 2 * error;
			if (e2 > -dy) {
				error -= dy;
				x1 += xinc;
			}
			if (e2 < dx) {
				error += dx;
				y1 += yinc;
			}
			if (unitsAreBlocking) {
				if (!navGrid.isWalkable(x1, y1) || isUnitOnCell(x1, y1, x2, y2, positionsUnits)) {
					return false;
				}
			} else {
				if (!navGrid.isWalkable(x1, y1)) {
					return false;
				}
			}

		}
		return true;
	}

	public MyPoint getPositionFurthestAwayFrom(MyPoint pos) {
		final TiledMapPosition goal = getPositionFurthestAwayFrom(new TiledMapPosition().setPositionFromTiles(pos.x, pos.y));
		return new MyPoint(goal.getTileX(), goal.getTileY());
	}

	public TiledMapPosition getPositionFurthestAwayFrom(TiledMapPosition pos) {
		final int height = navGrid.getHeight();
		final int width = navGrid.getWidth();
		int maxDistance = 0;
		int maxX = 0;
		int maxY = 0;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				final int distance = calculateDistance(i, j, pos.getTileX(), pos.getTileY());
				if (distance > maxDistance) {
					maxDistance = distance;
					maxX = i;
					maxY = j;
				}
			}
		}
		return new TiledMapPosition().setPositionFromTiles(maxX, maxY);
	}

	private int calculateDistance(int x1, int y1, int x2, int y2) {
		return (Math.abs(x1 - x2) + (Math.abs(y1 - y2)));
	}

	private List<GridCell> removeEndPoint(List<GridCell> path) {
		path.remove(path.size() - 1);
		return path;
	}

	private boolean isNextTo(GridCell cell, TiledMapPosition target) {
		return ((Math.abs(cell.x - target.getTileX()) == 1) && (Math.abs(cell.y - target.getTileY()) == 0)) || ((Math.abs(cell.x - target.getTileX()) == 0) && (Math.abs(cell.y - target.getTileY()) == 1));
	}

	public void filterPositionsByLineOfSight(Entity unit, Set<MyPoint> positions, List<TiledMapPosition> sortedUnits, final boolean unitsAreBlocking) {
		for (final Iterator<MyPoint> posIterator = positions.iterator(); posIterator.hasNext();) {
			final MyPoint pos = posIterator.next();
			final GridCell unitCell = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
			final GridCell targetCell = navGrid.getCell(pos.x, pos.y);
			if (!(lineOfSight(unitCell, targetCell, sortedUnits, unitsAreBlocking))) {
				posIterator.remove();
			}
		}
	}

	public void filterUnwalkablePositions(Set<MyPoint> positions) {
		for (final Iterator<MyPoint> posIterator = positions.iterator(); posIterator.hasNext();) {
			final MyPoint pos = posIterator.next();
			if (!navGrid.isWalkable(pos.x, pos.y)) {
				posIterator.remove();
			}
		}
	}

	public void filterPositionsByWalkability(Entity unit, Set<MyPoint> positions) {
		for (final Iterator<MyPoint> posIterator = positions.iterator(); posIterator.hasNext();) {
			final MyPoint pos = posIterator.next();
			if (!(canUnitWalkTo(unit, pos))) {
				posIterator.remove();
			}
		}
	}

	private boolean isUnitOnCell(final int x2, final int y2, final int targetX, final int targetY, final List<TiledMapPosition> positionsUnits) {
		for (final TiledMapPosition pos : positionsUnits) {
			if ((pos.getTileX() == x2) && (pos.getTileY() == y2) && !((pos.getTileX() == targetX) && (pos.getTileY() == targetY))) {
				return true;
			}
		}
		return false;
	}

	private boolean isCloseEnough(final GridCell center, final GridCell gridcell, final int range) {
		return ((Math.abs(center.x - gridcell.x) + (Math.abs(center.y - gridcell.y))) <= range);
	}

	public AStarGridFinder<GridCell> getFinder() {
		return aStarGridFinder;
	}

	public boolean isPreprocessingFinished() {
		return preprocessingFinished;
	}

	public void dispose() {
		aStarGridFinder = null;
		gridFinderOptions = null;
		navGrid = null;
	}

	// private constructor to force use of
	// getInstance() to create Singleton object
	private MyPathFinder() {
	}

	public static MyPathFinder getInstance() {
		if (pathFinder == null)
			pathFinder = new MyPathFinder();
		return pathFinder;
	}
}
