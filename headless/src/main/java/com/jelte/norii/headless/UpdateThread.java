package com.jelte.norii.headless;

import static java.lang.Thread.sleep;

public class UpdateThread implements Runnable {
	public GameServer gameServer;

	@Override
	public void run() {
		while (true) {
			try {
				gameServer.matchPlayers();
				sleep(3000);
			} catch (final Exception e) {
				System.out.println(e.getStackTrace());
			}
		}
	}
}
