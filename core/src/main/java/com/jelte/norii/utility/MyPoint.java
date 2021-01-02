package com.jelte.norii.utility;

import java.awt.Point;

public class MyPoint extends Point implements Comparable<MyPoint> {
	public MyPoint(int x, int y) {
		super(x, y);
	}

	@Override
	public int compareTo(MyPoint o) {
		int xComp = Integer.compare(x, o.x);
		if (xComp == 0)
			return Integer.compare(y, o.y);
		else
			return xComp;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	private long getCompareKey() {
		return (long) ((getX() * getX()) + (getY() * getY()));
	}
}
