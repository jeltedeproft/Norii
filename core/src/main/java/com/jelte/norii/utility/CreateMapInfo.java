package com.jelte.norii.utility;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGridGraph;
import org.xguzm.pathfinding.grid.NavigationGridGraphNode;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.map.Map;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.map.MapManager;

public class CreateMapInfo {
	static MapManager mapMgr;
	private static Map linkedMap;
	private static AStarGridFinder<GridCell> aStarGridFinder;
	private static NavigationGridGraph<GridCell> navGrid;
	private static GridFinderOptions gridFinderOptions = new GridFinderOptions();
	private static Array<MyPoint> walkablePoints = new Array<>();
	private static Array<Integer> paths = new Array<>();
	private static Array<Integer> lineOfSights = new Array<>();

	public CreateMapInfo(MapType mapType, MapManager mapMgr) {
		gridFinderOptions.allowDiagonal = false;
		aStarGridFinder = new AStarGridFinder<>(GridCell.class, gridFinderOptions);
		CreateMapInfo.mapMgr = mapMgr;
		CreateMapInfo.mapMgr.loadMap(mapType);
		linkedMap = mapMgr.getCurrentMap();
		preprocessMap();
		fillArray();
		FileHandle pathsFile = Gdx.files.local("maps/" + mapType.name() + "_paths.txt");
		FileHandle losFile = Gdx.files.local("maps/" + mapType.name() + "_lineOfSights.txt");
		writePathsToFile(pathsFile);
		writeLosToFile(losFile);
		System.out.println("finished preporcessing the map");
	}

	private static void preprocessMap() {
		System.out.println("starting preprocess");
		navGrid = mapMgr.getCurrentMap().getNavLayer().navGrid;
		int preprocessI = 0;
		int preprocessJ = 0;
		while (preprocessI < linkedMap.getMapWidth()) {
			while (preprocessJ < linkedMap.getMapHeight()) {
				final MyPoint start = new MyPoint(preprocessI, preprocessJ);
				if (navGrid.getCell(preprocessI, preprocessJ).isWalkable()) {
					walkablePoints.add(start);
				}
				preprocessJ++;
			}
			preprocessJ = 0;
			preprocessI++;
		}
	}

	private void fillArray() {
		System.out.println("filling array");
		int index = 1;
		int size = walkablePoints.size * walkablePoints.size;
		Array<MyPoint> walkablePointsCopy = new Array<>(walkablePoints);
		for (MyPoint start : walkablePoints) {
			for (MyPoint end : walkablePointsCopy) {
				System.out.println("filling point " + index + "/" + size);
				index++;
				// PATHS
				final List<GridCell> path = aStarGridFinder.findPath(start.x, start.y, end.x, end.y, navGrid);
				if (path != null) {
					int parsedInt = Integer.parseInt("" + start.toStringWithLeading0(2) + end.toStringWithLeading0(2));
					paths.add(parsedInt);
				}

				// LOS
				final GridCell unitCell = navGrid.getCell(start.x, start.y);
				final GridCell targetCell = navGrid.getCell(end.x, end.y);
				final NavigationGridGraphNode node = unitCell;
				final NavigationGridGraphNode neigh = targetCell;
				if (calculateLineOfSight(node, neigh)) {
					int parsedInt = Integer.parseInt("" + start.toStringWithLeading0(2) + end.toStringWithLeading0(2));
					lineOfSights.add(parsedInt);
				}
			}
		}
	}

	private void writePathsToFile(FileHandle file) {
		System.out.println("writing paths to file");
		int size = paths.size;
		int index = 1;
		for (int path : paths) {
			System.out.println("writing path " + index + "/" + size);
			index++;
			file.writeString(path + "\n", true);
		}
	}

	private void writeLosToFile(FileHandle file) {
		System.out.println("writing los to file");
		int size = lineOfSights.size;
		int index = 1;
		for (int los : lineOfSights) {
			System.out.println("writing los " + index + "/" + size);
			index++;
			file.writeString(los + "\n", true);
		}
	}

	private static boolean calculateLineOfSight(final NavigationGridGraphNode node, final NavigationGridGraphNode neigh) {
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
			if (!navGrid.isWalkable(x1, y1)) {
				return false;
			}

		}
		return true;
	}

}
