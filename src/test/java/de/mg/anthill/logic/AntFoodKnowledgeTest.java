package de.mg.anthill.logic;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import de.mg.anthill.logic.Ant;
import de.mg.anthill.logic.AntCommunication;
import de.mg.anthill.logic.GroundCallback;
import de.mg.anthill.logic.GroundSensor;
import de.mg.anthill.logic.Position;

public class AntFoodKnowledgeTest {

	private Ant initAnt(int foodOnGround) {

		GroundSensor sensorMock = mock(GroundSensor.class);
		when(sensorMock.getPossibleDirections(new Position(5, 5))).thenReturn(
				new Position[] { new Position(1, 1) });
		when(sensorMock.takeFood(new Position(6, 6))).thenReturn(true);
		when(sensorMock.foodAvailable(new Position(6, 6))).thenReturn(
				foodOnGround > 1);

		Ant ant = new Ant(5, 5, sensorMock, mock(GroundCallback.class));

		return ant;
	}

	@Test
	public void ant_finding_food_remembers_food_location() {

		// GIVEN some ant without food, and a food location with 5 units
		Ant ant = initAnt(5);

		// WHEN ant finds several units of food
		ant.scuttleAround();

		// THEN ant remembers the food location
		Position newPosition = (Position) Whitebox.getInternalState(ant,
				"foodPos");
		assertThat(newPosition, is(equalTo(new Position(6, 6))));
	}

	@Test
	public void ant_taking_rest_of_food_forgets_about_food_location() {

		// GIVEN some ant without food, and a food location with 1 units
		Ant ant = initAnt(1);

		// WHEN ant finds one unit of food
		ant.scuttleAround();

		// THEN ant does not rememeber the food location
		Position newPosition = (Position) Whitebox.getInternalState(ant,
				"foodPos");
		assertNull(newPosition);
	}

	private Ant initAnt(GroundSensor sensorMock, Position foodPos) {

		Ant ant = new Ant(5, 5, sensorMock, mock(GroundCallback.class));
		if (foodPos != null)
			Whitebox.setInternalState(ant, "foodPos", foodPos);

		return ant;
	}

	@Test
	public void ants_meeting_share_knowledge() {

		GroundSensor sensorMock = mock(GroundSensor.class);
		when(sensorMock.getPossibleDirections(new Position(5, 5))).thenReturn(
				new Position[] { new Position(1, 1) });

		Set<AntCommunication> antsSet = new HashSet<AntCommunication>();
		Ant answeringAnt = new Ant(50, 50, null, null);
		antsSet.add(answeringAnt);
		Whitebox.setInternalState(answeringAnt, "foodPos", new Position(50, 50));
		when(sensorMock.getOtherAnts(anyObject())).thenReturn(antsSet);

		// GIVEN several ant, some with food knowledge and some without
		Ant[] ants = new Ant[] { initAnt(sensorMock, new Position(0, 0)),
				initAnt(sensorMock, null), initAnt(sensorMock, null),
				initAnt(sensorMock, new Position(1, 1)),
				initAnt(sensorMock, null) };

		// WHEN those ants meet on the same cell
		for (Ant a : ants)
			a.scuttleAround();

		// THEN all ants previously without food knowledge have food knowldege
		// (from any of the other ants)
		for (Ant a : ants) {
			Position foodPos = (Position) Whitebox.getInternalState(a,
					"foodPos");
			assertThat(
					foodPos,
					anyOf(equalTo(new Position(0, 0)), equalTo(new Position(1,
							1)), equalTo(new Position(50, 50))));
		}
	}

}
