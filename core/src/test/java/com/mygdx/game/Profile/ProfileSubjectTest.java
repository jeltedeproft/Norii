package com.mygdx.game.Profile;

import com.badlogic.gdx.utils.Array;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProfileSubjectTest {

    private static ProfileSubject sut;

    private static Array<ProfileObserver> observers;


    @BeforeClass
    public static void setUp() {
        observers = new Array<ProfileObserver>();
        sut = new ProfileSubject();
    }

    @AfterClass
    public static void teardown() {
        observers = null;
        sut = null;
    }

    @Test
    public void testAddObserver() {
        ProfileObserver mockObserver = Mockito.mock(ProfileObserver.class);

        sut.addObserver(mockObserver);
    }

}
