package de.mg.anthill.logic;

import java.util.Set;

public interface GroundSensor {

	Position getHomeBasePosition();

	public Position[] getPossibleDirections(Position from);

	boolean takeFood(Position pos);

	boolean isAtHomeBase(Position pos);

	Set<AntCommunication> getOtherAnts(Ant ant);

	boolean foodAvailable(Position pos);

}
