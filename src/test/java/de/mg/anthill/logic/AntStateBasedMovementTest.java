/* 
 * Copyright 2015 Michael Gnatz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mg.anthill.logic;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class AntStateBasedMovementTest {

    @Test
    public void ant_with_food_moves_to_home_base() {

        // GIVEN some ant with food on the ground
        GroundSensor sensorMock = mock(GroundSensor.class);
        when(sensorMock.getHomeBasePosition()).thenReturn(new Position(1, 3));

        Ant ant = new Ant(5, 5, sensorMock, mock(GroundCallback.class));
        Whitebox.setInternalState(ant, "hasFood", true);

        // WHEN the ant moves several steps
        for (int i = 0; i < 10; i++) {
            ant.scuttleAround();
        }

        // THEN the ant reaches its home base
        assertThat(ant.getPos(), is(equalTo(new Position(1, 3))));

    }

    @Test
    public void ant_with_food_knowledge_moves_there() {

        // GIVEN some ant, which knows about food, but does not have food
        GroundSensor sensorMock = mock(GroundSensor.class);
        when(sensorMock.getPossibleDirections(anyObject())).thenReturn(
                new Position[]{new Position(0, 0)});

        Ant ant = new Ant(5, 5, sensorMock, mock(GroundCallback.class));
        Whitebox.setInternalState(ant, "foodPos", new Position(7, 1));
        assertFalse(ant.hasFood());

        // WHEN the ant moves several steps
        for (int i = 0; i < 10; i++) {
            ant.scuttleAround();
        }

        // THEN the ant reached the known place
        assertThat(ant.getPos(), is(equalTo(new Position(7, 1))));
    }

    @Test
    public void ant_at_known_food_position_without_finding_food_moves_anywhere() {

        // GIVEN some ant at iknown food position, with no food lying around
        GroundSensor sensorMock = mock(GroundSensor.class);
        when(sensorMock.takeFood(new Position(5, 5))).thenReturn(false);
        when(sensorMock.foodAvailable(new Position(5, 5))).thenReturn(false);

        Ant ant = new Ant(5, 5, sensorMock, mock(GroundCallback.class));
        Whitebox.setInternalState(ant, "foodPos", new Position(5, 5));

        // WHEN the ant moves
        when(sensorMock.getPossibleDirections(new Position(5, 5))).thenReturn(
                new Position[]{new Position(1, 1)});
        ant.scuttleAround();
        ant.scuttleAround();

        // THEN the ant does not stay at the place
        assertThat(ant.getPos(), not(is(equalTo(new Position(5, 5)))));
    }

    @Test
    public void ant_without_food_and_knowledge_moves_anywhere() {

        // GIVEN some ant, which does not know about food nor carries food
        Ground sensorMock = mock(Ground.class);
        when(sensorMock.getPossibleDirections(new Position(5, 5))).thenReturn(
                new Position[]{new Position(1, 1)});

        Ant ant = new Ant(5, 5, sensorMock, mock(GroundCallback.class));
        assertFalse(ant.hasFood());

        // WHEN the ant moves
        ant.scuttleAround();

        // THEN the ant does not stay at the place
        assertThat(ant.getPos(), not(is(equalTo(new Position(5, 5)))));
    }
}
