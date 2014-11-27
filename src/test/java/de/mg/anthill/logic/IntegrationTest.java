package de.mg.anthill.logic;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import de.mg.anthill.logic.Ant;
import de.mg.anthill.logic.Cell;
import de.mg.anthill.logic.Ground;
import de.mg.anthill.logic.Position;

public class IntegrationTest {

	// no mocking at all here

	@Test
	public void moving_ants_position_gets_updated_on_ground() {

		// GIVEN some ground with 1 ant
		Ground ground = new Ground(10, 10, 1);

		// WHEN ant gets told to scuffle
		ground.nextStep();

		// THEN the ants position is the same as the ground's position of the
		// ant

		Ant ant = null;
		Position groundPos = null;

		Cell[][] matrix = ground.getMatrix();
		for (int x = 0; x < matrix.length; x++) {
			for (int y = 0; y < matrix[0].length; y++) {
				Cell cell = matrix[x][y];
				if (!cell.getAnts().isEmpty()) {
					ant = cell.getAnts().iterator().next();
					groundPos = new Position(x, y);
					break;
				}

			}
		}

		assertNotNull(ant);
		assertEquals(groundPos, ant.getPos());
	}

	@Test
	public void all_food_transported_into_home_base() throws Exception {

		// GIVEN some ground with food
		int maxX = 10, maxY = 20;
		Ground ground = new Ground(maxX, maxY, 10);
		ground.addFood(10, 4);

		int totalFood = 0;
		Cell[][] matrix = ground.getMatrix();
		for (int x = 0; x < maxX; x++)
			for (int y = 0; y < maxY; y++) {
				Cell cell = matrix[x][y];
				totalFood += cell.getAmountOfFood();
			}

		System.out.println("total food = " + totalFood);
		System.out.println(ground);

		// WHEN ants are moving for some time
		for (int i = 0; i < 10000; i++)
			ground.nextStep();

		System.out.println(ground);

		// THEN all food is in the home base
		matrix = ground.getMatrix();
		for (int x = 0; x < maxX; x++)
			for (int y = 0; y < maxY; y++) {
				Cell cell = matrix[x][y];
				if (cell.isHomeBase())
					assertThat(cell.getAmountOfFood(), equalTo(totalFood));
				else
					assertThat(cell.getAmountOfFood(), equalTo(0));
			}
	}

	@Ignore
	@Test
	public void show_ground() throws Exception {

		Ground ground = new Ground(20, 40, 30);
		ground.addFood(2, 300);
		System.out.println(ground);

		for (int i = 0; i < 5000; i++) {
			Thread.sleep(200);
			ground.nextStep();

			System.out.println(ground);
		}

	}
}
