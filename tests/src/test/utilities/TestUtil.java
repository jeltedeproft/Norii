package test.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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

	public static void resultsToFile(String filename, Object object) {
		try {
			PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			outFile.print(object);
			outFile.close();
		} catch (FileNotFoundException e) {
			System.err.println(filename + "cannot be found.");
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public static void regressionTest(String filename1, String filename2) {
		try {
			FileReader fr1 = new FileReader(filename1);
			BufferedReader reader1 = new BufferedReader(fr1);

			FileReader fr2 = new FileReader(filename2);
			BufferedReader reader2 = new BufferedReader(fr2);

			TestUtil.assertReaders(reader1, reader2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
