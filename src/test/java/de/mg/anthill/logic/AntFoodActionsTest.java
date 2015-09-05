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

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class AntFoodActionsTest {

    @Test
    public void ant_takes_food() throws Exception {

        // GIVEN some ant without food
        GroundSensor sensorMock = mock(GroundSensor.class);
        when(sensorMock.getPossibleDirections(anyObject())).thenReturn(
                new Position[]{new Position(1, 1)});

        GroundCallback groundCallbackMock = mock(GroundCallback.class);

        Ant ant = new Ant(5, 5, sensorMock, groundCallbackMock);
        assertFalse(ant.hasFood());

        // WHEN the ant moves to a cell with food, which is not the home base
        when(sensorMock.takeFood(anyObject())).thenReturn(true);
        ant.scuttleAround();

        // THEN the ant takes the food and it is no longher on the ground
        assertTrue(ant.hasFood());

        verify(sensorMock, times(1)).takeFood(anyObject());
    }

    @Test
    public void ant_drops_food_at_home_base() {

        // GIVEN some ant with food
        GroundSensor sensorMock = mock(GroundSensor.class);
        when(sensorMock.getHomeBasePosition()).thenReturn(new Position(1, 3));

        GroundCallback groundCallbackMock = mock(GroundCallback.class);

        Ant ant = new Ant(5, 5, sensorMock, groundCallbackMock);
        Whitebox.setInternalState(ant, "hasFood", true);
        assertTrue(ant.hasFood());

        // WHEN the ant reaches its home base
        when(sensorMock.isAtHomeBase(anyObject())).thenReturn(true);

        ant.scuttleAround();

        // THEN the ant drops the food and the home base has the food afterwards
        assertFalse(ant.hasFood());
        verify(groundCallbackMock, times(1)).dropFood(anyObject());
    }

    @Test
    public void ant_does_not_take_food_at_home_base_and_does_not_remeber_food_position() {

        // GIVEN some ant without food, next to home base where food is lying
        GroundSensor sensorMock = mock(GroundSensor.class);
        when(sensorMock.getPossibleDirections(anyObject())).thenReturn(
                new Position[]{new Position(1, 1)});
        when(sensorMock.isAtHomeBase(new Position(6, 6))).thenReturn(true);
        when(sensorMock.takeFood(new Position(6, 6))).thenReturn(true);
        when(sensorMock.foodAvailable(new Position(6, 6))).thenReturn(true);

        Ant ant = new Ant(5, 5, sensorMock, mock(GroundCallback.class));

        // WHEN the ant moves to home base
        ant.scuttleAround();

		// THEN the ant does not take the food, does not have food and does not
        // rememeber the home base as food position
        verify(sensorMock, times(0)).takeFood(new Position(6, 6));

        assertFalse(ant.hasFood());

        Position foodPos = (Position) Whitebox.getInternalState(ant, "foodPos");
        assertNull(foodPos);

    }
}
