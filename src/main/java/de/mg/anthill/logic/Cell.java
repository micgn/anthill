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

import java.util.HashSet;
import java.util.Set;

public class Cell {

    private final Set<Ant> ants = new HashSet<>();
    private boolean homeBase = false;
    private int foodUnits = 0;

    public int getAmountOfAnts() {
        return ants.size();
    }

    public Set<Ant> getAnts() {
        return ants;
    }

    public void addAnt(Ant ant) {
        ants.add(ant);
    }

    public boolean isHomeBase() {
        return homeBase;
    }

    public void setHomeBase() {
        homeBase = true;
    }

    public void addFood(int units) {
        foodUnits += units;
    }

    public Integer getAmountOfFood() {
        return foodUnits;
    }

    public void takeFood() {
        if (foodUnits > 0) {
            foodUnits--;
        } else {
            throw new IllegalStateException("can not remove food here");
        }
    }

    public void antDeparted(Ant ant) {
        ants.remove(ant);
    }

    public void antArrived(Ant ant) {
        ants.add(ant);
    }

}
