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
		final int xComp = Integer.compare(x, o.x);
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
		final MyPoint p = (MyPoint) o;
		return (getX() == p.getX()) && (getY() == p.getY());
	}

	@Override
	public int hashCode() {
		// Talk about a fun time reverse engineering this one!
		long l = java.lang.Double.doubleToLongBits(getY());
		l = (l * 31) ^ java.lang.Double.doubleToLongBits(getX());
		return (int) ((l >> 32) ^ l);
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

	public MyPoint incrementX() {
		x++;
		return this;
	}

	public MyPoint incrementY() {
		y++;
		return this;
	}

	public MyPoint decrementX() {
		x--;
		return this;
	}

	public MyPoint decrementY() {
		y--;
		return this;
	}

	public MyPoint makeCopy() {
		return new MyPoint(x, y);
	}
}
