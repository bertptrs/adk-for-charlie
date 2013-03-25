package nl.liacs.adk;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Paint;
import android.util.Log;

public class Snake implements ISnake {
	private float x;
	private float y;
	private double rotation;
	private double deltaRotation = 0;
	private float speed;
	private double rotationSpeed;
	private Paint color;
	private String name;
	private int score;
	private boolean alive;
	private float gapLength = 0;
	private int width = 2;
	public boolean invertControls;
	public boolean invincible;

	public Snake() {
		name = "DefaultName";
		alive = true;
		invertControls = false;
		invincible = false;
		setSpeed(ADKPrefs.getSingleton().getSnakeSpeed());
		Log.d(Snake.class.getCanonicalName(), "Snake with speed " + speed + " created.");
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float target) {
		x = target;
	}

	public void setY(float target) {
		y = target;
	}

	public double getRotation() {
		return rotation;
	}

	public double getDeltaRotation() {
		return deltaRotation;
	}

	// Set rotation to new target angle.
	// Auto compress value to [0, 2PI]
	public void setRotation(double target) {
		rotation = target;
		while (rotation < 0)
			rotation += 2 * Math.PI;

		while (rotation > 2 * Math.PI)
			rotation -= 2 * Math.PI;
	}

	public void setDeltaRotation(double target) {
		deltaRotation = target*rotationSpeed;
	}

	public Paint getColor() {
		return color;
	}

	public void setColor(Paint color) {
		this.color = color;
		color.setStrokeWidth(width);
	}
	
	public void setAlpha(int alpha)
	{
		color.setAlpha(alpha);
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public void setWidth(int width)
	{
		this.width = width;
		color.setStrokeWidth(width);
	}

	@Override
	public String toString() {
		return "Snake {X:" + x + "; Y: " + y + "}";
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void move(float amount) {
		if (invertControls)
			rotation -= deltaRotation * amount;
		else
			rotation += deltaRotation * amount;
		x += Math.cos(rotation) * speed * amount;
		y += Math.sin(rotation) * speed * amount;
	}

	public String getName() {
		return name;
	}

	public List<Integer> pixelsOnMove(float amount) {
		List<Integer> coords = new ArrayList<Integer>();
		float dx = (float) (speed * amount * Math.cos(rotation));
		float dy = (float) (speed * amount * Math.sin(rotation));
		float cx = x;
		float cy = y;
		float tx = x + dx;
		float ty = y + dy;

		float lambda;

		if (Math.abs(dx) > Math.abs(dy)) {
			lambda = 1f / Math.abs(dx) * dy;
			while ((cx < tx && dx > 0) || (cx > tx && dx < 0)) {
				cx += (dx > 0 ? 1 : -1);
				cy += lambda;
				coords.add(Integer.valueOf((int) cx));
				coords.add(Integer.valueOf((int) cy));
			}
		} else {
			lambda = 1f / Math.abs(dy) * dx;
			while ((cy < ty && dy > 0) || (cy > ty && dy < 0)) {
				cy += (dy > 0 ? 1 : -1);
				cx += lambda;
				coords.add(Integer.valueOf((int) cx));
				coords.add(Integer.valueOf((int) cy));
			}
		}
		
		//Log.d(Snake.class.getCanonicalName(), (coords.size()/2) + " coordinates between (" + x + "," + y +") and ("+tx + "," + ty +")");
		//for(int i = 0; i < coords.size(); i += 2) {
		//	Log.d(Snake.class.getCanonicalName(), "x"+coords.get(i)+",y"+coords.get(i+1));
		//}

		return coords;
	}
	
	public void increaseScore() {
		score++;		
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public void setAlive(Boolean b) {
		alive = b;
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
	public void setSpeed(float speed)
	{
		this.speed = speed;
		rotationSpeed = (speed/50) * Math.PI;
	}
	
	public void setGapLength(float l) {
		gapLength = l;
	}
	
	public void reduceGapLength(float l) {
		gapLength -= l * speed;
	}
	
	public float getGapLength() {
		return gapLength;
	}

}
