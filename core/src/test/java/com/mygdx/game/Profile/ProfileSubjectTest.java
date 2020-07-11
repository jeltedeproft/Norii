package com.mygdx.game.Profile;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.utils.Array;

class ProfileSubjectTest {
	private static ProfileSubject sut;
	private static Array<ProfileObserver> observers;

	@BeforeAll
	public static void setUp() {
		observers = new Array<ProfileObserver>();
		sut = new ProfileSubject();
	}

	@AfterAll
	public static void teardown() {
		observers = null;
		sut = null;
	}

	@Test
	void testAddObserver() {
		ProfileObserver mockObserver = Mockito.mock(ProfileObserver.class);

		sut.addObserver(mockObserver);
	}

}
