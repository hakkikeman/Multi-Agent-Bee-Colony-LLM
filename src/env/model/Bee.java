package model;

import graphic.EnvironmentApplication;
import javafx.scene.paint.Paint;
import model.enumeration.BeeRole;

public class Bee {
	private String id;
	private BeeRole role;
	private int age;
	private Position position = new Position();
	private int pollenCollected;

	public Bee(String id, BeeRole role) {
		this.id = id;
		this.role = role;
	}

	public void setPosition(int x, int y) {
		this.position.setXY(x, y);
	}

	public String getId() {
		return id;
	}

	public int getAge() {
		return age;
	}

	public Position getPosition() {
		return position;
	}

	public void setPollenCollected(int ammount) {
		this.pollenCollected = ammount;
	}

	public int takePollenCollected() {
		int ammount = pollenCollected;
		pollenCollected = 0;
		return ammount;
	}

	public BeeRole getRole() {
		return role;
	}

	public void setRole(BeeRole newRole) {
		this.role = newRole;
	}

	public Paint getColor() {
		switch (role) {
			case nurse:
				return EnvironmentApplication.colorBeeFeeder;
			case sentinel:
				return EnvironmentApplication.colorBeeSentinel;
			case monarch:
				return EnvironmentApplication.colorBeeQueen;
			default:
				return EnvironmentApplication.colorBeeWorker;
		}
	}
}
