package de.mg.anthill.client;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import de.mg.anthill.logic.Ant;
import de.mg.anthill.logic.Cell;
import de.mg.anthill.logic.Ground;

public class anthillgwt implements EntryPoint {

	private static final int SIZE = 700;

	private Ground ground;
	private DrawingArea area;
	private Timer timer;

	@Override
	public void onModuleLoad() {

		final TextBox fieldSize = new TextBox();
		final TextBox ants = new TextBox();
		final TextBox foods = new TextBox();
		final Label error = new Label();
		RootPanel.get("fieldSize").add(fieldSize);
		RootPanel.get("ants").add(ants);
		RootPanel.get("foods").add(foods);
		RootPanel.get("errorLabel").add(error);

		final Button init = new Button("initialize");
		final Button start = new Button("start");
		final Button stop = new Button("stop");
		start.setEnabled(false);
		stop.setEnabled(false);
		RootPanel.get("init").add(init);
		RootPanel.get("start").add(start);
		RootPanel.get("stop").add(stop);

		area = new DrawingArea(SIZE, SIZE);
		RootPanel.get("anthill").add(area);

		init.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				stopSimulation();

				try {
					int valuefieldSize = Integer.valueOf(fieldSize.getValue());
					int valueAnts = Integer.valueOf(ants.getValue());
					int valueFoods = Integer.valueOf(foods.getValue());
					String errMsg = null;
					if (valuefieldSize < 15 || valuefieldSize > 50) {
						errMsg = "field size must be between 15 and 50";
					} else if (valueAnts < 1 || valueAnts > 500) {
						errMsg = "amount of ants must be between 1 and 500";
					} else if (valueFoods < 1 || valueFoods > 500) {
						errMsg = "amount of food must be between 1 and 500";
					}
					if (errMsg == null) {
						error.setVisible(false);
						initialize(valuefieldSize, valueAnts, valueFoods);
						drawAntHill(ground.getMatrix());
						start.setEnabled(true);
						stop.setEnabled(false);
					} else {
						error.setText(errMsg);
						error.setVisible(true);
					}
				} catch (NumberFormatException e) {
					error.setText("all values have to be entered");
					error.setVisible(true);
				}
			}
		});

		start.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				startSimulation();
				start.setEnabled(false);
				stop.setEnabled(true);
			}
		});

		stop.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				stopSimulation();
				stop.setEnabled(false);
				start.setEnabled(true);
			}
		});
	}

	private void initialize(int maxXY, int ants, int foodPlaces) {
		ground = new Ground(maxXY, maxXY, ants);
		ground.addFood(foodPlaces, 50);
	}

	private void startSimulation() {
		timer = new Timer() {
			public void run() {
				ground.nextStep();
				drawAntHill(ground.getMatrix());
			}
		};
		timer.scheduleRepeating(300);
	}

	private void stopSimulation() {
		if (timer != null && timer.isRunning()) {
			timer.cancel();
		}
	}

	private void drawAntHill(Cell[][] matrix) {

		area.clear();

		Rectangle border = new Rectangle(0, 0, SIZE, SIZE);
		border.setStrokeWidth(2);
		border.setStrokeColor("black");
		area.add(border);

		final float cellSize = 1.0F * SIZE / matrix.length;

		for (int x = 0; x < matrix.length; x++) {
			for (int y = 0; y < matrix[0].length; y++) {
				Cell cell = matrix[x][y];
				int drawX = Math.round(x * cellSize);
				int drawY = Math.round(y * cellSize);
				int drawSize = Math.round(cellSize);
				if (cell.isHomeBase()) {
					// brown rectangle, no fill
					Rectangle homeRec = new Rectangle(drawX + 1, drawY + 1,
							drawSize - 1, drawSize - 1);
					homeRec.setStrokeColor("brown");
					homeRec.setStrokeWidth(3);
					area.add(homeRec);
				}
				if (cell.getAmountOfFood() > 0) {
					// small green circle with rgb(0%,x%,0%)
					Circle foodCircle = new Circle(drawX + drawSize / 2, drawY
							+ drawSize / 2, drawSize / 2 - 5);
					// TODO
					int green = Math
							.round(20.0F / cell.getAmountOfFood() * 100.0F);
					foodCircle.setFillColor("rgb(0%," + green + "%,0%)");
					foodCircle.setStrokeWidth(0);
					area.add(foodCircle);
				}
				if (cell.getAmountOfAnts() > 0) {
					boolean withFood = false;
					for (Ant ant : cell.getAnts()) {
						if (ant.hasFood()) {
							withFood = true;
						}
					}
					if (!withFood) {
						// red middle-sized circle
						Circle ant = new Circle(drawX + drawSize / 2, drawY
								+ drawSize / 2, drawSize / 2 - 4);
						ant.setStrokeWidth(2);
						ant.setStrokeColor("red");
						ant.setFillOpacity(0.0);
						area.add(ant);
					} else {
						// red middle-sized circle, with light green filling and
						// opacity
						Circle ant = new Circle(drawX + drawSize / 2, drawY
								+ drawSize / 2, drawSize / 2 - 4);
						ant.setStrokeWidth(2);
						ant.setStrokeColor("red");
						ant.setFillColor("green");
						ant.setFillOpacity(0.4);
						area.add(ant);
					}
				}
			}
		}

	}
}
