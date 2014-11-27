package de.mg.anthill.logic;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import de.mg.anthill.logic.Ant;
import de.mg.anthill.logic.GroundCallback;
import de.mg.anthill.logic.GroundSensor;
import de.mg.anthill.logic.Position;
import static org.powermock.api.mockito.PowerMockito.mock;

public class AntSimpleMovementTest {

	@Test
	public void ant_moves_towards_internal_given_target() throws Exception {

		// GIVEN some ant at 15,15
		Ant ant = new Ant(15, 15, mock(GroundSensor.class),
				mock(GroundCallback.class));

		// WHEN the ant gets set some destination
		Whitebox.<Void> invokeMethod(ant, "stepInDirectionOf", new Position(20,
				10));

		// THEN the ant really moves into this direction
		assertThat(ant.getPos(), is(equalTo(new Position(16, 14))));
	}

	@Test
	public void ant_reaches_given_internal_target() throws Exception {

		// GIVEN some ant at 15,15
		Ant ant = new Ant(15, 15, mock(GroundSensor.class),
				mock(GroundCallback.class));

		// WHEN the ant gets set some destination
		for (int i = 0; i < 10; i++)
			Whitebox.<Void> invokeMethod(ant, "stepInDirectionOf",
					new Position(20, 10));

		// THEN the ant really moves into this direction
		assertThat(ant.getPos(), is(equalTo(new Position(20, 10))));
	}
}
