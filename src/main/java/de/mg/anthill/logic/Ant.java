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

import java.util.Random;

public class Ant implements AntCommunication {

    private final GroundSensor sensor;
    private final GroundCallback groundCallback;

    private static final Random random = new Random(System.currentTimeMillis());

    private Position pos;
    private boolean hasFood = false;
    private Position foodPos;

    public Ant(int x, int y, GroundSensor sensor, GroundCallback groundCallback) {
        this.groundCallback = groundCallback;
        this.sensor = sensor;
        pos = new Position(x, y);
    }

    public void scuttleAround() {

        Position oldPos = pos;
        if (hasFood) {
            stepInDirectionOf(sensor.getHomeBasePosition());
        } else if (foodPos != null) {
            stepInDirectionOf(foodPos);
        } else {
            Position[] dirs = sensor.getPossibleDirections(pos);
            Position dir = dirs[random.nextInt(dirs.length)];
            pos = new Position(pos.x + dir.x, pos.y + dir.y);
        }
        groundCallback.move(this, oldPos, pos);

        if (!hasFood && !sensor.isAtHomeBase(pos)) {
            hasFood = sensor.takeFood(pos);
            if (sensor.foodAvailable(pos)) {
                foodPos = pos;
            } else if (pos.equals(foodPos)) {
                foodPos = null;
            }
        } else if (hasFood && sensor.isAtHomeBase(pos)) {
            groundCallback.dropFood(pos);
            hasFood = false;
        }

        if (foodPos == null) {
            for (AntCommunication ant : sensor.getOtherAnts(this)) {
                foodPos = ant.askForFoodPosition();
                if (foodPos != null) {
                    break;
                }
            }
        }
    }

    public Position getPos() {
        return pos;
    }

    private void stepInDirectionOf(Position to) {
        int newx = pos.x + Math.max(Math.min(to.x - pos.x, 1), -1);
        int newy = pos.y + Math.max(Math.min(to.y - pos.y, 1), -1);
        pos = new Position(newx, newy);
    }

    public boolean hasFood() {
        return hasFood;
    }

    @Override
    public Position askForFoodPosition() {
        return foodPos;
    }

}
