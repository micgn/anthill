package de.mg.anthill.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Ground implements GroundSensor, GroundCallback {

	private final Cell[][] matrix;
	private final int maxX, maxY;
	private final Set<Ant> ants;
	private final Position home;

	public Ground(int maxX, int maxY, int amountOfAnts) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.matrix = new Cell[maxX][maxY];
		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				matrix[x][y] = new Cell();
			}
		}

		Random gen = new Random(System.currentTimeMillis());
		this.ants = new HashSet<Ant>();
		for (int i = 0; i < amountOfAnts; i++) {

			// place ants randomly
			int x = gen.nextInt(maxX);
			int y = gen.nextInt(maxY);

			Ant ant = new Ant(x, y, (GroundSensor) this, (GroundCallback) this);
			ants.add(ant);

			matrix[x][y].addAnt(ant);
		}

		home = new Position(maxX / 2, maxY / 2);
		matrix[home.x][home.y].setHomeBase();
	}

	public Cell[][] getMatrix() {
		return matrix;
	}

	public void addFood(int foodPlaces, int maxFoodUnits) {
		Random gen = new Random(System.currentTimeMillis());
		for (int i = 0; i < foodPlaces; i++) {
			int units = Math.max(gen.nextInt(maxFoodUnits), 1);
			int x = gen.nextInt(matrix.length);
			int y = gen.nextInt(matrix[0].length);
			matrix[x][y].addFood(units);
		}

	}

	public void nextStep() {
		for (Ant ant : ants)
			ant.scuttleAround();
	}

	@Override
	public Position getHomeBasePosition() {
		return home;
	}

	@Override
	public boolean isAtHomeBase(Position pos) {
		return pos.equals(home);
	}

	@Override
	public Position[] getPossibleDirections(Position from) {
		List<Position> dirs = new ArrayList<Position>();
		if (from.x > 0)
			dirs.add(new Position(-1, 0));
		if (from.y > 0)
			dirs.add(new Position(0, -1));
		if (from.x > 0 && from.y > 0)
			dirs.add(new Position(-1, -1));
		if (from.x < maxX - 1)
			dirs.add(new Position(1, 0));
		if (from.y < maxY - 1)
			dirs.add(new Position(0, 1));
		if (from.x < maxX - 1 && from.y < maxY - 1)
			dirs.add(new Position(1, 1));
		if (from.x > 0 && from.y < maxY - 1)
			dirs.add(new Position(-1, 1));
		if (from.x < maxX - 1 && from.y > 0)
			dirs.add(new Position(1, -1));

		return dirs.toArray(new Position[dirs.size()]);
	}

	@Override
	public void move(Ant ant, Position oldPos, Position newPos) {
		matrix[oldPos.x][oldPos.y].antDeparted(ant);
		matrix[newPos.x][newPos.y].antArrived(ant);
	}

	@Override
	public void dropFood(Position pos) {
		matrix[pos.x][pos.y].addFood(1);
	}

	@Override
	public boolean takeFood(Position pos) {
		if (matrix[pos.x][pos.y].getAmountOfFood() > 0) {
			matrix[pos.x][pos.y].takeFood();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean foodAvailable(Position pos) {
		return matrix[pos.x][pos.y].getAmountOfFood() > 0;
	}

	@Override
	public Set<AntCommunication> getOtherAnts(Ant ant) {

		Set<AntCommunication> result = new HashSet<AntCommunication>();
		Cell cell = matrix[ant.getPos().x][ant.getPos().y];
		result.addAll(cell.getAnts());
		result.remove(ant);
		return result;
	}

	@Override
	public String toString() {

		String result = "";

		for (int y = 0; y < maxY; y++)
			result += '-';
		result += "\n";
		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				Cell cell = matrix[x][y];
				char c = ' ';
				if (cell.getAmountOfAnts() > 0)
					if (cell.getAmountOfAnts() == 1)
						c = 'a';
					else
						c = 'A';
				else if (cell.isHomeBase())
					c = 'H';
				else if (cell.getAmountOfFood() > 0) {
					if (cell.getAmountOfFood() == 1)
						c = '1';
					else
						c = '>';
				}
				result += c;
			}
			result += "|\n";
		}
		for (int y = 0; y < maxY; y++)
			result += '-';
		result += "\n";

		return result;
	}

}
