package nl.liacs.adk;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.Log;

public class PowerupManager
{
	public static enum PowerupEffects
	{
		SPEED_UP,
		SPEED_DOWN,
		SPEED_UP_OTHERS,
		SPEED_DOWN_OTHERS,
		CLEAR_FIELD,
		INVERT_CONTROLS,
		INVERT_CONTROLS_OTHERS,
		SWITCH_HEADS,
		MARTYR,
		THICK_LINE,
		THICK_LINE_OTHERS,
		INVINCIBILITY,
	}

	private class Powerup {
		public float x;
		public float y;
		public float size;
		public float sizeSq;
		public PowerupEffects effect;
		public float timer;

		public Powerup(float x, float y, float size, PowerupEffects effect) {
			this.x = x;
			this.y = y;
			this.effect = effect;
			this.size = size;
			sizeSq = size * size;
			timer = 10;
			Log.d(Powerup.class.getCanonicalName(), "New powerup at (" + x
					+ ", " + y + ")");
		}
		
		public int getColor()
		{
			int color;
			switch (effect)
			{
			case SPEED_UP:
				color = 0xFF0000;
				break;
			case SPEED_DOWN:
				color = 0x0000FF;
				break;
			case SPEED_UP_OTHERS:
				color = 0xFF9999;
				break;
			case SPEED_DOWN_OTHERS:
				color = 0x9999FF;
				break;
			case CLEAR_FIELD:
				color = 0x000000;
				break;
			case INVERT_CONTROLS:
				color = 0x00B500;
				break;
			case INVERT_CONTROLS_OTHERS:
				color = 0xB1FCB1;
				break;
			case SWITCH_HEADS:
				color = 0x999999;
				break;
			case MARTYR:
				color = 0xFFFFFF;
				break;
			case THICK_LINE:
				color = 0xFF7E29;
				break;
			case THICK_LINE_OTHERS:
				color = 0xFFDD99;
				break;
			case INVINCIBILITY:
				color = 0xFFB5F5;
				break;
			default:
				color = 0xFFD3B5;
				break;
			}
			return color | 0xFF000000;
		}
	}

	private class PowerupEffect implements Runnable {
		private PowerupEffects effect;
		private Snake snake;
		private Handler handler;
		private int seconds;
		public boolean finished;

		public PowerupEffect(PowerupEffects effect, Snake snake) {
			finished = false;
			this.effect = effect;
			this.snake = snake;
			handler = new Handler();
			startEffect();
			handler.postDelayed(this, 1000);
		}

		public void run() {
			handler.removeCallbacks(this);
			seconds++;
			if (seconds >= 5)
			{
				stopEffect();
				return;
			}
			duringEffect();
			handler.postDelayed(this, 1000);
		}
		
		public void stop()
		{
			if (finished)
				return;
			handler.removeCallbacks(this);
			stopEffect();
		}

		private void startEffect() {
			switch (effect) {
			case SPEED_UP:
				snake.setSpeed(snake.getSpeed() * 1.5f);
				break;
			case SPEED_DOWN:
				snake.setSpeed(snake.getSpeed() / 1.5f);
				break;
			case SPEED_UP_OTHERS:
				for (int i = 0; i < game.snakes.size(); i++) {
					Snake s = game.snakes.get(i);
					if (s != snake)
						s.setSpeed(s.getSpeed() * 1.5f);
				}
				break;
			case SPEED_DOWN_OTHERS:
				for (int i = 0; i < game.snakes.size(); i++) {
					Snake s = game.snakes.get(i);
					if (s != snake)
						s.setSpeed(s.getSpeed() / 1.5f);
				}
				break;
			case CLEAR_FIELD:
				game.internalCanvas.drawColor(0xFF000000);
				break;
			case INVERT_CONTROLS:
				snake.invertControls = !snake.invertControls;
				break;
			case INVERT_CONTROLS_OTHERS:
				for (int i = 0; i < game.snakes.size(); i++)
				{
					Snake s = game.snakes.get(i);
					if (s != snake)
						s.invertControls = !s.invertControls;
				}
				break;
			case SWITCH_HEADS:
				{
					Snake temp = game.snakes.get(game.snakes.size()-1);
					float x = temp.getX();
					float y = temp.getY();
					double r = temp.getRotation();
					for (int i = 0; i < game.snakes.size(); i++)
					{
						Snake temp2 = game.snakes.get(i);
						if (!temp2.isAlive())
							continue;
						float tx = temp2.getX();
						float ty = temp2.getY();
						double tr = temp2.getRotation();
						temp2.setX(x);
						temp2.setY(y);
						temp2.setRotation(r);
						x = tx;
						y = ty;
						r = tr;
					}
				}
				break;
			case MARTYR:
				{
					int explosionRadius = 42;
					int fragmentCount = 42;
					int i;
					float fragStart;
					float fragEnd;
					float fragDir;
					Paint snakeColor = snake.getColor();
					Paint jagger = new Paint();
					jagger.setColor(0xff000000); // Paint it black!
					int x = (int) snake.getX();
					int y = (int) snake.getY();
					Canvas c = game.internalCanvas;
					c.drawCircle(x, y, explosionRadius, jagger);
					for (i = 0; i < fragmentCount; i++) {
						fragStart = (float) (Math.random() * explosionRadius);
						fragEnd = (float) (fragStart + Math.random()
								* (explosionRadius - fragStart));
						fragDir = (float) (Math.random() * Math.PI * 2);
						Path fragment = new Path();
						fragment.moveTo(
								(float) (x + fragStart * Math.cos(fragDir)),
								(float) (y + fragStart * Math.sin(fragDir)));
						fragment.lineTo((float) (x + fragEnd * Math.cos(fragDir)),
								(float) (y + fragEnd * Math.sin(fragDir)));
						c.drawPath(fragment, snakeColor);
					}
					if (!snake.invincible)
						snake.setAlive(false);
					break;
				}
			case THICK_LINE:
				snake.setWidth(snake.getWidth()*2);
				break;
			case THICK_LINE_OTHERS:
				for (int i = 0; i < game.snakes.size(); i++)
				{
					Snake s = game.snakes.get(i);
					if (s != snake)
						s.setWidth(s.getWidth()*2);
				}
				break;
			case INVINCIBILITY:
				snake.invincible = true;
				snake.setAlpha(128);
				break;
			}
		}
		
		private void duringEffect()
		{
			switch(effect)
			{
			case INVINCIBILITY:
				snake.setAlpha(128 + (127 * seconds) / 5);
			}
		}

		private void stopEffect() {
			finished = true;
			switch (effect) {
			case SPEED_UP:
				snake.setSpeed(snake.getSpeed() / 1.5f);
				break;
			case SPEED_DOWN:
				snake.setSpeed(snake.getSpeed() * 1.5f);
				break;
			case SPEED_UP_OTHERS:
				for (int i = 0; i < game.snakes.size(); i++) {
					Snake s = game.snakes.get(i);
					if (s != snake)
						s.setSpeed(s.getSpeed() / 1.5f);
				}
				break;
			case SPEED_DOWN_OTHERS:
				for (int i = 0; i < game.snakes.size(); i++) {
					Snake s = game.snakes.get(i);
					if (s != snake)
						s.setSpeed(s.getSpeed() * 1.5f);
				}
				break;
			case INVERT_CONTROLS:
				snake.invertControls = !snake.invertControls;
				break;
			case INVERT_CONTROLS_OTHERS:
				for (int i = 0; i < game.snakes.size(); i++)
				{
					Snake s = game.snakes.get(i);
					if (s != snake)
						s.invertControls = !s.invertControls;
				}
				break;
			case THICK_LINE:
				snake.setWidth(snake.getWidth()/2);
				break;
			case THICK_LINE_OTHERS:
				for (int i = 0; i < game.snakes.size(); i++)
				{
					Snake s = game.snakes.get(i);
					if (s != snake)
						s.setWidth(s.getWidth()/2);
				}
				break;
			case INVINCIBILITY:
				snake.invincible = false;
				snake.getColor().setAlpha(255);
				break;
			}
		}
	}

	private Powerup active[];
	private float timer;
	private GameFieldView game;
	private Paint paint;
	private PowerupEffects[] enabledEffects;
	private ArrayList<PowerupEffect> runningEffects;

	public PowerupManager(GameFieldView game) {
		ADKPrefs prefs = ADKPrefs.getSingleton();
		active = new Powerup[prefs.getNumPowerups()];
		timer = 10;
		this.game = game;
		paint = new Paint();
		String[] enabled = prefs.getPowerups();
		enabledEffects = new PowerupEffects[enabled.length];
		for (int i = 0; i < enabled.length; i++)
			enabledEffects[i] = PowerupEffects.valueOf(enabled[i]);
		runningEffects = new ArrayList<PowerupEffect>();
	}

	public void update(float deltaTime) {
		timer -= deltaTime;
		int numPowerups = 0;
		int empty = 0;

		for (int i = 0; i < active.length; i++) {
			if (active[i] == null) {
				empty = i;
				continue;
			}
			active[i].timer -= deltaTime;
			if (active[i].timer <= 0) {
				active[i] = null;
				empty = i;
				continue;
			}
			numPowerups++;
			for (int j = 0; j < game.snakes.size(); j++) {
				float dx = active[i].x - game.snakes.get(j).getX() * game.scale;
				float dy = active[i].y - game.snakes.get(j).getY() * game.scale;
				if (dx * dx + dy * dy < active[i].sizeSq) {
					applyEffect(active[i].effect, game.snakes.get(j));
					active[i] = null;
					break;
				}
			}
		}

		if (timer <= 0 && numPowerups < active.length && enabledEffects.length > 0) {
			float x = (float) Math.random() * game.getHeight();
			float y = (float) Math.random() * game.getHeight();
			PowerupEffects effect = enabledEffects[(int) (Math.random()*enabledEffects.length)];
			active[empty] = new Powerup(x, y, game.getHeight()/30, effect);
			active[empty].timer = active.length*5+1;
			timer = 5;
		}
		
		for (int i = 0; i < runningEffects.size(); i++)
		{
			if (runningEffects.get(i).finished)
			{
				runningEffects.remove(i);
				i--;
			}
		}
	}

	public void applyEffect(PowerupEffects effect, Snake snake) {
		runningEffects.add(new PowerupEffect(effect, snake));
		Log.d(PowerupManager.class.getCanonicalName(),
				"Powerup (" + effect.name() + ") gepakt door: "
						+ snake.getName());
	}

	public void draw(Canvas c) {
		for (int i = 0; i < active.length; i++) {
			if (active[i] == null)
				continue;
			paint.setARGB(255, 255, 255, 0);
			c.drawCircle(active[i].x, active[i].y, active[i].size, paint);
			paint.setColor(active[i].getColor());
			c.drawCircle(active[i].x, active[i].y, active[i].size/2, paint);
		}
	}
	
	public void stop()
	{
		for (int i = 0; i < runningEffects.size(); i++)
		{
			runningEffects.get(i).stop();
		}
		for (int i = 0; i < active.length; i++)
			active[i] = null;
	}
}
