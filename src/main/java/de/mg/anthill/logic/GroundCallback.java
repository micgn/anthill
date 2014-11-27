package de.mg.anthill.logic;

public interface GroundCallback {

	void move(Ant ant, Position oldPos, Position pos);

	void dropFood(Position pos);

}
