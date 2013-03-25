package nl.liacs.adk;

public interface ISnake {
	public float getX();
	public float getY();
	public double getRotation();
	public void setRotation(double target);
	public double getDeltaRotation();
	public void setDeltaRotation(double target);
	public boolean isAlive();
	public float getSpeed();
}
