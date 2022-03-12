package com.jelte.norii.utility;

import com.badlogic.gdx.Gdx;

public class MyPoint implements Comparable<MyPoint> {
	private static final String TAG = MyPoint.class.getSimpleName();
	public int x;
	public int y;

	public MyPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(MyPoint o) {
		final int xComp = Integer.compare(x, o.x);
		if (xComp == 0) {
			return Integer.compare(y, o.y);
		}
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

	public String toStringWithLeading0(int size) {
		String paddedX = String.format("%0" + size + "d", x);
		String paddedY = String.format("%0" + size + "d", y);
		return paddedX + paddedY;
	}

	public MyPoint randomize(int xLimit, int yLimit) {
		x = Utility.random.nextInt(xLimit);
		y = Utility.random.nextInt(yLimit);
		return this;
	}

	public MyPoint randomChange() {
		return randomChange(Utility.random.nextInt(4));
	}

	public MyPoint randomChange(int randomInt) {
		switch (randomInt) {
		case 0:
			return incrementX();
		case 1:
			return incrementY();
		case 2:
			return decrementX();
		case 3:
			return decrementY();
		default:
			Gdx.app.log(TAG, "cant possible return any other number in this function");
			return null;
		}
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
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
