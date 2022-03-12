package com.jelte.norii.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
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
import com.badlogic.gdx.files.FileHandle;
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
	private List<String> lineOfSights = new ArrayList<>();
	private List<String> paths = new ArrayList<>();
	private FileHandle pathsFile;
	private FileHandle losFile;
	private boolean preprocessingFinished = false;

	private static MyPathFinder pathFinder;

	public void setMap(final Map map) {
		linkedMap = map;
		navGrid = linkedMap.getNavLayer().navGrid;
		gridFinderOptions = new GridFinderOptions();
		gridFinderOptions.allowDiagonal = false;
		aStarGridFinder = new AStarGridFinder<>(GridCell.class, gridFinderOptions);
		pathsFile = Gdx.files.local("maps/" + map.getMapType().name() + "_paths.txt");
		losFile = Gdx.files.local("maps/" + map.getMapType().name() + "_lineOfSights.txt");
	}

	public void preprocessMap() {
		String line;
		try (BufferedReader reader = pathsFile.reader(1028, "utf-8");) {
			while ((line = reader.readLine()) != null) {
				paths.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (BufferedReader reader = losFile.reader(1028, "utf-8");) {
			while ((line = reader.readLine()) != null) {
				lineOfSights.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		preprocessingFinished = true;
	}

	public float getPreprocessingMapProgress() {
		return 1;
		// return ((preprocessI * linkedMap.getMapWidth()) + preprocessJ) / (float) (linkedMap.getMapWidth() * linkedMap.getMapHeight());
	}

	// PATH FINDING
	public boolean canUnitWalkTo(Entity unit, GridCell cell) {
		final GridCell center = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		return isCloseEnough(center, cell, unit.getAp()) && pathExists(center, cell);
	}

	public boolean canUnitWalkTo(Entity unit, MyPoint point) {
		final GridCell center = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		final GridCell target = navGrid.getCell(point.x, point.y);
		return isCloseEnough(center, target, unit.getAp()) && pathExists(center, target);
	}

	public boolean canUnitWalkTo(Entity unit, TiledMapPosition pos) {
		final GridCell center = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		final GridCell target = navGrid.getCell(pos.getTileX(), pos.getTileY());
		return isCloseEnough(center, target, unit.getAp()) && pathExists(center, target);
	}

	private boolean pathExists(final GridCell center, final GridCell gridcell) {
		final MyPoint start = new MyPoint(center.x, center.y);
		final MyPoint end = new MyPoint(gridcell.x, gridcell.y);
		return paths.contains(start.toStringWithLeading0(2) + end.toStringWithLeading0(2));
	}

	private boolean pathExists(TiledMapPosition start, TiledMapPosition end) {
		return paths.contains(new MyPoint(start.getTileX(), start.getTileY()).toStringWithLeading0(2) + new MyPoint(end.getTileX(), end.getTileY()).toStringWithLeading0(2));
	}

	public List<GridCell> pathTowards(MyPoint start, MyPoint goal, int ap) {
		final TiledMapPosition startPos = new TiledMapPosition().setPositionFromTiles(start.x, start.y);
		final TiledMapPosition goalPos = new TiledMapPosition().setPositionFromTiles(goal.x, goal.y);
		return pathTowards(startPos, goalPos, ap);
	}

	public List<GridCell> pathTowards(TiledMapPosition start, TiledMapPosition goal, int ap) {
		oldTime = System.currentTimeMillis();
		if (pathExists(start, goal)) {
			final List<GridCell> path = aStarGridFinder.findPath(start.getTileX(), start.getTileY(), goal.getTileX(), goal.getTileY(), navGrid);
			if (path.size() <= ap) {
				return path;
			}

			return chippedPath(path, ap);
		}

		oldTime = AbilityTargetMoveGenerator.debugTime("find path from " + start + " to " + goal, oldTime);
		return adjustGoal(start, goal);
	}

	private List<GridCell> adjustGoal(TiledMapPosition start, TiledMapPosition goal) {
		List<GridCell> path = null;
		while ((path == null) || path.isEmpty()) {
			oldTime = AbilityTargetMoveGenerator.debugTime("trying from : " + start + " to " + goal, oldTime);
			goal = tryAdjacentTile(start, goal);
			path = aStarGridFinder.findPath(start.getTileX(), start.getTileY(), goal.getTileX(), goal.getTileY(), navGrid);
		}
		oldTime = AbilityTargetMoveGenerator.debugTime("goal adjusted", oldTime);
		Gdx.app.debug("myPathFinder", "path after adjustment = " + path);
		return path;
	}

	private TiledMapPosition tryAdjacentTile(TiledMapPosition start, TiledMapPosition goal) {
		if (start.getTileX() < goal.getTileX()) {
			return goal.changeX(-1);
		}
		if (start.getTileX() > goal.getTileX()) {
			return goal.changeX(1);
		}
		if (start.getTileY() < goal.getTileY()) {
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

		if ((!unitsAreBlocking) && (Boolean.FALSE.equals(lineOfSights.contains(startingPoint.toStringWithLeading0(2) + endingPoint.toStringWithLeading0(2))))) {
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
		final int xinc = (x1 < x2) ? 1 : -1;
		final int yinc = (y1 < y2) ? 1 : -1;

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
			} else if (!navGrid.isWalkable(x1, y1)) {
				return false;
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
			if ((pos.getTileX() == x2) && (pos.getTileY() == y2) && ((pos.getTileX() != targetX) || (pos.getTileY() != targetY))) {
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
		if (pathFinder == null) {
			pathFinder = new MyPathFinder();
		}
		return pathFinder;
	}
}
