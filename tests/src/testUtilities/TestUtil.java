package testUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;

public class TestUtil {

	public static void collectRandomUnits(Array<Entity> team, int numberOfUnits, UnitOwner owner) {
		while (team.size <= numberOfUnits) {
			team.add(new Entity(EntityTypes.randomEntityType(), owner, false));
		}
	}

	public static void assertReaders(BufferedReader expected, BufferedReader actual) throws IOException {
		String line;
		while ((line = expected.readLine()) != null) {
			assertEquals(line, actual.readLine());
		}

		assertNull("Actual had more lines then the expected.", actual.readLine());
		assertNull("Expected had more lines then the actual.", expected.readLine());
	}

}
