package de.mg.anthill.logic;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import de.mg.anthill.logic.Ant;
import de.mg.anthill.logic.AntCommunication;
import de.mg.anthill.logic.Cell;
import de.mg.anthill.logic.Ground;
import de.mg.anthill.logic.Position;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Ground.class)
public class GroundTest {

	@Test
	public void ground_initialized_with_ants() throws Exception {

		// mock setup
		whenNew(Ant.class).withAnyArguments().thenReturn(mock(Ant.class));

		Random randomMock = mock(Random.class);
		when(randomMock.nextInt(anyInt())).thenReturn(3);
		whenNew(Random.class).withAnyArguments().thenReturn(randomMock);

		// WHEN a ground of size 30x20 with 20 ants is initialized
		Ground ground = new Ground(30, 20, 20);

		// THEN the ground is of size 30x20
		assertThat(ground.getMatrix().length, equalTo(30));
		assertThat(ground.getMatrix()[0].length, equalTo(20));

		// THEN 20 ants have been created
		verifyNew(Ant.class, times(20)).withArguments(3, 3, ground, ground);

		// THEN the ground contains 20 ants
		int amount = 0;
		Cell[][] matrix = ground.getMatrix();
		for (int x = 0; x < 30; x++)
			for (int y = 0; y < 20; y++)
				amount += matrix[x][y].getAmountOfAnts();
		// only > 0 since always the same ant is placed, maybe on the same cell
		assertTrue(amount > 0);

		// THEN the middle (15,10) contains the home base
		assertTrue(matrix[15][10].isHomeBase());
		assertTrue(ground.isAtHomeBase(new Position(15, 10)));

		// THEN the any other cell (9,9) contains NOT the home base
		assertFalse(matrix[9][9].isHomeBase());
		assertFalse(ground.isAtHomeBase(new Position(9, 9)));
	}

	@Test
	public void add_food_to_ground() throws Exception {

		Cell cell = mock(Cell.class);
		whenNew(Cell.class).withAnyArguments().thenReturn(cell);

		Random random = mock(Random.class);
		whenNew(Random.class).withAnyArguments().thenReturn(random);
		when(random.nextInt(anyInt())).thenReturn(1);

		// GIVEN some ground
		Ground ground = new Ground(30, 20, 20);

		// WHEN up to 10 units of food (randomly) are added 5 times to the
		// ground at random places
		ground.addFood(5, 10);

		// THEN all the food has been added to ground cells
		verify(cell, times(5)).addFood(1);
	}

	@Test
	public void not_all_directions_are_possible() {

		// GIVEN some ground
		Ground ground = new Ground(5, 5, 0);

		// THEN for a given position only certain directions are possibe

		assertThat(ground.getPossibleDirections(new Position(2, 2)).length,
				equalTo(8));

		assertThat(
				Arrays.asList(ground.getPossibleDirections(new Position(0, 0))),
				not(hasItems(new Position(-1, 0), new Position(0, -1),
						new Position(-1, -1))));

		assertThat(
				Arrays.asList(ground.getPossibleDirections(new Position(4, 4))),
				not(hasItems(new Position(1, 0), new Position(0, 1),
						new Position(1, 1))));

		assertThat(
				Arrays.asList(ground.getPossibleDirections(new Position(1, 4))),
				not(hasItems(new Position(1, 1), new Position(0, 1))));

		// testing not really complete here
	}

	@Test
	public void dropped_food_is_availabe() {

		// GIVEN position on ground without food
		Ground ground = new Ground(5, 5, 0);
		assertFalse(ground.foodAvailable(new Position(1, 1)));

		// WHEN food is dropped
		ground.dropFood(new Position(1, 1));

		// THEN food is available
		assertTrue(ground.foodAvailable(new Position(1, 1)));
	}

	@Test
	public void taken_food_is_not_available() {

		// GIVEN position on ground with food
		Ground ground = new Ground(5, 5, 0);
		ground.dropFood(new Position(1, 1));
		assertTrue(ground.foodAvailable(new Position(1, 1)));

		// WHEN food is taken
		ground.takeFood(new Position(1, 1));

		// THEN no food is available
		assertFalse(ground.foodAvailable(new Position(1, 1)));
	}

	@Test
	public void ground_can_provide_other_ants_on_same_position() {

		// stupid test?

		// GIVEN 10 ants on cell 1,1
		Ground ground = new Ground(1, 1, 10);

		// THEN the other ants can be provided
		@SuppressWarnings("unchecked")
		Set<Ant> ants = (Set<Ant>) Whitebox.getInternalState(ground, "ants");
		Ant anyAnt = ants.iterator().next();
		Set<AntCommunication> other = ground.getOtherAnts(anyAnt);
		assertEquals(9, other.size());
	}
}
