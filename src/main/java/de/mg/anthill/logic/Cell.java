package de.mg.anthill.logic;

import java.util.HashSet;
import java.util.Set;

public class Cell {

	private final Set<Ant> ants = new HashSet<Ant>();
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
