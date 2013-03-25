package nl.liacs.adk;

import android.graphics.Bitmap;
import android.view.View;

public class AIController implements IController {
	private ISnake snake = null;
	private GameFieldView view = null;
	private int checkDistance = 0;

	public ISnake getSnake() {
		return snake;
	}

	public void setSnake(ISnake snake) {
		this.snake = snake;
		checkDistance = (int) (snake.getSpeed()*1.1);
	}

	public void setListeners(View v) {
		view = (GameFieldView) v;
	}

	public void unsetListeners() {
	}
	
	public int checkLine(float x, float y, double angle)
	{
		float xstep = (float) Math.cos(angle);
		float ystep = (float) Math.sin(angle);
		int checkx, checky;
		Bitmap b = view.b;
		for (int i = 1; i < checkDistance; i++)
		{
			checkx = (int) (x + i*xstep);
			checky = (int) (y + i*ystep);
			if (checkx < 0 || checkx >= b.getWidth() || checky < 0 || checky >= b.getHeight())
				return i;
			int color = b.getPixel(checkx, checky);
			if ((color & 0xFFFFFF) != 0)
				return i;
		}
		return checkDistance;
	}

	public void update() {
		if (!snake.isAlive())
			return;
		float x = snake.getX();
		float y = snake.getY();
		double angle = snake.getRotation();
		int maxDistance = checkLine(x, y, angle);
		double maxAngle = 0;
		int tempDistance = checkLine(x, y, angle-Math.PI/3);
		if (tempDistance > maxDistance)
		{
			maxDistance = tempDistance;
			maxAngle = -Math.PI/3;
		}
		tempDistance = checkLine(x, y, angle-2*Math.PI/3);
		if (tempDistance > maxDistance)
		{
			maxDistance = tempDistance;
			maxAngle = -2*Math.PI/3;
		}
		tempDistance = checkLine(x, y, angle+Math.PI/3);
		if (tempDistance > maxDistance)
		{
			maxDistance = tempDistance;
			maxAngle = Math.PI/3;
		}
		tempDistance = checkLine(x, y, angle+2*Math.PI/3);
		if (tempDistance > maxDistance)
		{
			maxDistance = tempDistance;
			maxAngle = 2*Math.PI/3;
		}
		snake.setDeltaRotation(maxAngle);
	}
}
