package com.mygdx.game.Map;

import java.util.ArrayList;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.NavigationGridGraph;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import com.badlogic.gdx.Gdx;

public class MyPathFinder{
	private static final String TAG = MyPathFinder.class.getSimpleName();
	
	private NavigationGridGraph<GridCell> navGrid;
	private GridFinderOptions opt;
	private AStarGridFinder<GridCell> finder;
	private Map linkedMap;
	
	public MyPathFinder(Map map) {
		linkedMap = map;
		navGrid = linkedMap.getNavLayer().navGrid;
		
		opt = new GridFinderOptions();
		opt.allowDiagonal = false;
			
		finder = new AStarGridFinder<GridCell>(GridCell.class, opt); //implement singleton
	}
	
	public List<GridCell> getCellsWithin(int x, int y, int range) {
		List<GridCell> cells = new ArrayList<GridCell>();
		GridCell center = navGrid.getCell(x, y);

		for(GridCell[] gridcells : navGrid.getNodes()) {
			for(GridCell gridcell : gridcells) {
				if (checkIfDistanceIsCloseEnough(center,gridcell,(range + 1))) {
					cells.add(gridcell);
				}
			}
		}
		return cells;
	}
	
	private boolean checkIfDistanceIsCloseEnough(GridCell center, GridCell gridcell, int range) {
		if((Math.abs(center.x - gridcell.x) + (Math.abs(center.y - gridcell.y))) <= range){
			List<GridCell> path = finder.findPath(center.x, center.y, gridcell.x, gridcell.y, navGrid);
			if((path != null) && (path.size() < range)) {
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
