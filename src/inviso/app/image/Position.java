package inviso.app.image;

public class Position {
	public Scene scene;
	public boolean isChargingDock;
	public double angle;
	
	public Position(Scene scene, boolean isChargingDock, double angle) {
		this.scene = scene;
		this.isChargingDock = isChargingDock;
		this.angle = angle;
	}
}
