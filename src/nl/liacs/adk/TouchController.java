package nl.liacs.adk;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchController implements IController, OnTouchListener {
	private ISnake snake;
	private float deadzone = 30; // pixels

	public TouchController() {
		snake = null;
	}

	public ISnake getSnake() {
		return snake;
	}

	public void setSnake(ISnake snake) {
		this.snake = snake;
	}

	public void setRotation(double rotation) {
		if (snake != null)
			snake.setDeltaRotation(rotation);
	}

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			((GameFieldView) v).onClickHandler(v);
		case MotionEvent.ACTION_MOVE:
			float x = event.getX();
			float half = v.getWidth() / 2;
			float distance = Math.abs(x - half) / half;
			if (x < half - deadzone) {
				// Log.v("ADK.TouchController", "OnTouch left");
				setRotation(-distance);
			} else if (x > half + deadzone) {
				// Log.v("ADK.TouchController", "OnTouch right");
				setRotation(distance);
			} else {
				// Log.v("ADK.TouchController", "OnTouch, center (ignored)");
				setRotation(0);
			}
			return true;
		case MotionEvent.ACTION_UP:
			// Log.v("ADK.TouchController", "OnTouch up");
			setRotation(0);
			return true;
		}
		return false;
	}

	public void setListeners(View v) {
		v.setOnTouchListener(this);
	}

	public void unsetListeners() {
	}

	public void update() {
	}
}
