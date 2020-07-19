package com.mygdx.game.Map;

import java.util.ArrayList;
import java.util.List;

import org.xguzm.pathfinding.NavigationNode;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGridGraph;
import org.xguzm.pathfinding.grid.NavigationGridGraphNode;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityAnimation.Direction;

import Utility.TiledMapPosition;
import Utility.Utility;

public class MyPathFinder {
	private NavigationGridGraph<GridCell> navGrid;
	private GridFinderOptions gridFinderOptions;
	private AStarGridFinder<GridCell> aStarGridFinder;
	private final Map linkedMap;

	public MyPathFinder(final Map map) {
		linkedMap = map;
		navGrid = linkedMap.getNavLayer().navGrid;
		gridFinderOptions = new GridFinderOptions();
		gridFinderOptions.allowDiagonal = false;
		aStarGridFinder = new AStarGridFinder<GridCell>(GridCell.class, gridFinderOptions);
	}

	public AStarGridFinder<GridCell> getFinder() {
		return aStarGridFinder;
	}

	public List<GridCell> getLineOfSightWithinCircle(final int x, final int y, final int range, final ArrayList<TiledMapPosition> positionsUnits) {
		final List<GridCell> cells = new ArrayList<GridCell>();
		final GridCell center = navGrid.getCell(x, y);

		for (final GridCell[] gridcells : navGrid.getNodes()) {
			for (final GridCell gridcell : gridcells) {
				if (isCloseEnough(center, gridcell, range) && pathExists(center, gridcell, range) && lineOfSight(center, gridcell, positionsUnits)) {
					cells.add(gridcell);
				}
			}
		}
		return cells;
	}

	public List<GridCell> getLineOfSightWithinLine(final int x, final int y, final int range, final Direction direction, final ArrayList<TiledMapPosition> positionsUnits) {
		final List<GridCell> cells = new ArrayList<GridCell>();
		final GridCell center = navGrid.getCell(x, y);

		for (final GridCell[] gridcells : navGrid.getNodes()) {
			for (final GridCell gridcell : gridcells) {
				if (checkIfInLine(center, gridcell, range, direction) && lineOfSight(center, gridcell, positionsUnits)) {
					cells.add(gridcell);
				}
			}
		}
		return cells;
	}

	public List<GridCell> getCellsWithinCircle(final int x, final int y, final int range) {
		final List<GridCell> cells = new ArrayList<GridCell>();
		final GridCell center = navGrid.getCell(x, y);

		for (final GridCell[] gridcells : navGrid.getNodes()) {
			for (final GridCell gridcell : gridcells) {
				if (isCloseEnough(center, gridcell, range) && pathExists(center, gridcell, range)) {
					cells.add(gridcell);
				}
			}
		}
		return cells;
	}

	public List<GridCell> getCellsWithinLine(final int x, final int y, final int range, final Direction direction) {
		final List<GridCell> cells = new ArrayList<GridCell>();
		final GridCell center = navGrid.getCell(x, y);

		for (final GridCell[] gridcells : navGrid.getNodes()) {
			for (final GridCell gridcell : gridcells) {
				if (checkIfInLine(center, gridcell, range, direction)) {
					cells.add(gridcell);
				}
			}
		}
		return cells;
	}

	public boolean checkIfInLine(Entity caster, Entity target, final int range, final Direction direction) {
		final GridCell casterCell = navGrid.getCell(caster.getCurrentPosition().getTileX(), caster.getCurrentPosition().getTileY());
		final GridCell targetCell = navGrid.getCell(target.getCurrentPosition().getTileX(), target.getCurrentPosition().getTileY());
		return checkIfInLine(casterCell, targetCell, range, direction);
	}

	private boolean checkIfInLine(final GridCell center, final GridCell gridcell, final int range, final Direction direction) {
		if (isCloseEnough(center, gridcell, range)) {
			final List<GridCell> path = aStarGridFinder.findPath(center.x, center.y, gridcell.x, gridcell.y, navGrid);
			if ((path != null) && (path.size() <= range) && (!path.isEmpty())) {
				switch (direction) {
				case UP:
					return (center.x == gridcell.x) && (center.y <= gridcell.y);
				case DOWN:
					return (center.x == gridcell.x) && (center.y >= gridcell.y);
				case LEFT:
					return (center.x >= gridcell.x) && (center.y == gridcell.y);
				case RIGHT:
					return (center.x <= gridcell.x) && (center.y == gridcell.y);
				default:
					return false;
				}
			}
			return false;
		}
		return false;
	}

	private boolean isCloseEnough(final GridCell center, final GridCell gridcell, final int range) {
		return ((Math.abs(center.x - gridcell.x) + (Math.abs(center.y - gridcell.y))) <= range);
	}

	private boolean pathExists(final GridCell center, final GridCell gridcell, final int range) {
		final List<GridCell> path = aStarGridFinder.findPath(center.x, center.y, gridcell.x, gridcell.y, navGrid);
		return ((path != null) && (path.size() <= range) && (!path.isEmpty()));
	}

	public boolean lineOfSight(Entity unit, final GridCell to, Entity[] sortedUnits) {
		final ArrayList<TiledMapPosition> positionsUnits = (ArrayList<TiledMapPosition>) Utility.collectPositionsUnits(sortedUnits);
		final GridCell unitCell = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		final GridCell targetCell = navGrid.getCell(to.getX(), to.getY());

		return lineOfSight(unitCell, targetCell, positionsUnits);
	}

	public boolean lineOfSight(Entity caster, Entity target, Entity[] sortedUnits) {
		final ArrayList<TiledMapPosition> positionsUnits = (ArrayList<TiledMapPosition>) Utility.collectPositionsUnits(sortedUnits);
		final GridCell unitCell = navGrid.getCell(caster.getCurrentPosition().getTileX(), caster.getCurrentPosition().getTileY());
		final GridCell targetCell = navGrid.getCell(target.getCurrentPosition().getTileX(), target.getCurrentPosition().getTileY());

		return lineOfSight(unitCell, targetCell, positionsUnits);
	}

	public boolean lineOfSight(Entity caster, TiledMapPosition targetPos, Entity[] sortedUnits) {
		final ArrayList<TiledMapPosition> positionsUnits = (ArrayList<TiledMapPosition>) Utility.collectPositionsUnits(sortedUnits);
		final GridCell unitCell = navGrid.getCell(caster.getCurrentPosition().getTileX(), caster.getCurrentPosition().getTileY());
		final GridCell targetCell = navGrid.getCell(targetPos.getTileX(), targetPos.getTileY());

		return lineOfSight(unitCell, targetCell, positionsUnits);
	}

	public boolean lineOfSight(final NavigationNode from, final NavigationNode to, final ArrayList<TiledMapPosition> positionsUnits) {
		if (from == null || to == null) {
			return false;
		}

		final NavigationGridGraphNode node = (NavigationGridGraphNode) from;
		final NavigationGridGraphNode neigh = (NavigationGridGraphNode) to;

		int x1 = node.getX(), y1 = node.getY();
		final int x2 = neigh.getX(), y2 = neigh.getY();
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
			if (!navGrid.isWalkable(x1, y1) || isUnitOnCell(x1, y1, x2, y2, positionsUnits)) {
				return false;
			}
		}
		return true;
	}

	private boolean isUnitOnCell(final int x2, final int y2, final int targetX, final int targetY, final ArrayList<TiledMapPosition> positionsUnits) {
		for (final TiledMapPosition pos : positionsUnits) {
			if (pos.getTileX() == x2 && pos.getTileY() == y2 && !(pos.getTileX() == targetX && pos.getTileY() == targetY)) {
				return true;
			}
		}
		return false;
	}

	public boolean canUnitWalkTo(Entity unit, GridCell cell) {
		final GridCell center = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		return isCloseEnough(center, cell, unit.getAp()) && pathExists(center, cell, unit.getAp());
	}

	public boolean canUnitWalkTo(Entity unit, TiledMapPosition pos) {
		final GridCell center = navGrid.getCell(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		final GridCell target = navGrid.getCell(pos.getTileX(), pos.getTileY());
		return isCloseEnough(center, target, unit.getAp()) && pathExists(center, target, unit.getAp());
	}

	public TiledMapPosition getClosestMoveSpotNextToUnit(Entity mover, Entity target) {
		final int startX = mover.getCurrentPosition().getTileX();
		final int startY = mover.getCurrentPosition().getTileY();
		final int endX = target.getCurrentPosition().getTileX();
		final int endY = target.getCurrentPosition().getTileY();
		final List<GridCell> path = aStarGridFinder.findPath(startX, startY, endX, endY, navGrid);
		for (final GridCell cell : path) {
			if (isNextTo(cell, target.getCurrentPosition())) {
				return new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
			}
		}
		return null;
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

	public List<GridCell> pathTowards(TiledMapPosition start, TiledMapPosition goal, int ap) {
		final List<GridCell> path = aStarGridFinder.findPath(start.getTileX(), start.getTileY(), goal.getTileX(), goal.getTileY(), navGrid);

		if (path == null) {
			return adjustGoal(start, goal, path);
		}

		if (path.size() <= ap) {
			return path;
		}

		return chippedPath(path, ap);
	}

	private List<GridCell> adjustGoal(TiledMapPosition start, TiledMapPosition goal, List<GridCell> path) {
		while (path == null) {
			goal = tryAdjacentTile(start, goal);
			path = aStarGridFinder.findPath(start.getTileX(), start.getTileY(), goal.getTileX(), goal.getTileY(), navGrid);
		}
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

		return path;
	}

	private int calculateDistance(int x1, int y1, int x2, int y2) {
		return (Math.abs(x1 - x2) + (Math.abs(y1 - y2)));
	}

	public List<GridCell> getPathFromUnitToUnit(Entity mover, Entity target) {
		final int startX = mover.getCurrentPosition().getTileX();
		final int startY = mover.getCurrentPosition().getTileY();
		final int endX = target.getCurrentPosition().getTileX();
		final int endY = target.getCurrentPosition().getTileY();
		return removeEndPoint(aStarGridFinder.findPath(startX, startY, endX, endY, navGrid));
	}

	private List<GridCell> removeEndPoint(List<GridCell> path) {
		path.remove(path.size() - 1);
		return path;
	}

	private boolean isNextTo(GridCell cell, TiledMapPosition target) {
		return ((Math.abs(cell.x - target.getTileX()) == 1) && (Math.abs(cell.y - target.getTileY()) == 0)) || ((Math.abs(cell.x - target.getTileX()) == 0) && (Math.abs(cell.y - target.getTileY()) == 1));
	}

	public void dispose() {
		aStarGridFinder = null;
		gridFinderOptions = null;
		navGrid = null;
	}
}
