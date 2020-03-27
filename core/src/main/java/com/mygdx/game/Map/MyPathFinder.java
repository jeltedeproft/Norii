package com.mygdx.game.Map;

import java.util.ArrayList;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGridGraph;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import com.mygdx.game.Entities.EntityAnimation.Direction;

public class MyPathFinder {
	private NavigationGridGraph<GridCell> navGrid;
	private GridFinderOptions opt;
	private AStarGridFinder<GridCell> finder;
	private final Map linkedMap;

	public MyPathFinder(final Map map) {
		linkedMap = map;
		navGrid = linkedMap.getNavLayer().navGrid;

		opt = new GridFinderOptions();
		opt.allowDiagonal = false;

		finder = new AStarGridFinder<GridCell>(GridCell.class, opt); //implement singleton
	}

	public List<GridCell> getCellsWithinCircle(final int x, final int y, final int range) {
		final List<GridCell> cells = new ArrayList<GridCell>();
		final GridCell center = navGrid.getCell(x, y);

		for (final GridCell[] gridcells : navGrid.getNodes()) {
			for (final GridCell gridcell : gridcells) {
				if (checkIfDistanceIsCloseEnough(center, gridcell, (range + 1))) {
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
				if (checkIfInLine(center, gridcell, (range + 1), direction)) {
					cells.add(gridcell);
				}
			}
		}
		return cells;
	}

	private boolean checkIfInLine(final GridCell center, final GridCell gridcell, final int range, final Direction direction) {
		if ((Math.abs(center.x - gridcell.x) + (Math.abs(center.y - gridcell.y))) <= range) {
			final List<GridCell> path = finder.findPath(center.x, center.y, gridcell.x, gridcell.y, navGrid);
			if ((path != null) && (path.size() < range) && (!path.isEmpty())) {
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

	private boolean checkIfDistanceIsCloseEnough(final GridCell center, final GridCell gridcell, final int range) {
		if ((Math.abs(center.x - gridcell.x) + (Math.abs(center.y - gridcell.y))) <= range) {
			final List<GridCell> path = finder.findPath(center.x, center.y, gridcell.x, gridcell.y, navGrid);
			if ((path != null) && (path.size() < range) && (!path.isEmpty())) {
				return true;
			}
		}
		return false;
	}

	public AStarGridFinder<GridCell> getFinder() {
		return finder;
	}

	public void dispose() {
		finder = null;
		opt = null;
		navGrid = null;
	}
}
