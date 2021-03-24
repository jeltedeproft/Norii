package com.jelte.norii.utility;

public class MyPoint implements Comparable<MyPoint> {
	public int x;
	public int y;

	public MyPoint(int x, int y) {
		this.x = x;
		this.y = y;
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
	public boolean equals(Object o) {
		if (!(o instanceof MyPoint)) {
			return false;
		}
		MyPoint p = (MyPoint) o;
		return (getX() == p.getX()) && (getY() == p.getY());
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	private long getCompareKey() {
		return (getX() * getX()) + (getY() * getY());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
