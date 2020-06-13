package com.mygdx.game.Profile;

import com.badlogic.gdx.utils.Array;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.jupiter.api.Test;

public class ProfileSubjectTest {

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
    public void testAddObserver() {
        ProfileObserver mockObserver = Mockito.mock(ProfileObserver.class);

        sut.addObserver(mockObserver);
    }

}
