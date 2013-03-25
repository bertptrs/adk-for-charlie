package nl.liacs.adk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class GameFieldView extends View {
	public int gapChance;
	public Bitmap b;
	public static int edgeMargin = 60;
	public static int gapMaxLength = 10;
	public static int gapMinLength = 5;
	Canvas internalCanvas;
	GameFieldView selfReference;
	Date lastEngineLoop;
	IController controllers[];
	Context selfContext;
	static final int width = 240;
	static final int height = 240;
	List<Snake> snakes;
	private int currentController = 0;
	private PowerupManager powerups;
	public float scale;
	private int gameState = GameState.UNDEFINED;
	Random rand = new Random();

	Runnable refreshTask = new Runnable() {
		public void run() {
			removeCallbacks(this);
			postDelayed(this, 40);
			invalidate();
			if (ADKPrefs.getSingleton().isAIHighQuality()) {
				// Way faster than a spelled out for loop.
				for(IController c : controllers) {
					c.update();
				}
			} else {
				controllers[currentController].update();
				currentController++;
				if (currentController == controllers.length)
					currentController = 0;
			}
		}
	};

	void awardPoints() {
		for (int i = 0; i < snakes.size(); i++) {
			if (snakes.get(i).isAlive())
				snakes.get(i).increaseScore();
		}
	}

	public GameFieldView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (gameState != GameState.UNDEFINED)
			return; // Already initialized at some point
		selfContext = context;
		b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		internalCanvas = new Canvas(b);

		selfReference = this;

		powerups = new PowerupManager(this);

		setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				onClickHandler(v);

			}
		});

		snakes = new ArrayList<Snake>();
		initSnakes();

		prepareGame();

	}

	public void prepareGame() {
		internalCanvas.drawARGB(255, 0, 0, 0);

		gameState = GameState.INITIALIZING;

		randomizeSnakes();

		currentController = 0;
		gapChance = ADKPrefs.getSingleton().getGapChance();

		lastEngineLoop = new Date();

		powerups.stop();

		postDelayed(new Runnable() {
			public void run() {
				removeCallbacks(this);
				gameState = GameState.RUNNING;
				lastEngineLoop = new Date();
				postDelayed(refreshTask, 10);

			}
		}, 2000);

		invalidate();
	}

	private void randomizeSnakes() {
		for (int i = 0; i < snakes.size(); i++) {
			Snake n = snakes.get(i);
			n.setX(edgeMargin + rand.nextInt(width - 2 * edgeMargin));
			n.setY(edgeMargin + rand.nextInt(height - 2 * edgeMargin));
			n.setRotation(rand.nextDouble() * 2 * Math.PI);
			n.setAlive(true);
		}
	}

	@Override
	protected void onDraw(Canvas c) {
		if (gameState == GameState.PAUSED || gameState == GameState.GAME_OVER) {
			drawGameBox(internalCanvas);

			Matrix trans = new Matrix();
			trans.setScale(c.getHeight() / (float) height, c.getHeight()
					/ (float) height);

			c.drawBitmap(b, trans, null);

			powerups.draw(c);

			drawText(c);
			return;
		}
		Paint paint;

		// Ensure timing
		Date now = new Date();
		float amount = (now.getTime() - lastEngineLoop.getTime()) / 1000.0f;
		if (gameState == GameState.INITIALIZING)
			amount = 0.1f;
		lastEngineLoop = now;

		powerups.update(amount);

		int aliveCount = 0;
		for (int i = 0; i < snakes.size(); i++) {
			Snake s = snakes.get(i);
			if (!s.isAlive())
				continue;
			aliveCount++;
			paint = s.getColor();

			float x = s.getX();
			float y = s.getY();

			List<Integer> coords = s.pixelsOnMove(amount);

			if (!s.invincible && dies(coords, b)) {
				// Kill it. Kill it with FIRE!
				s.setAlive(false);
				Log.d(GameFieldView.class.getCanonicalName(), "Snake " + i
						+ " just died. Loser.");
				aliveCount--;
				awardPoints();
			}

			s.move(amount);

			if (s.getGapLength() > 0) {
				s.reduceGapLength(amount);
				continue;
			} else if (rand.nextFloat() * gapChance / s.getSpeed() < 2) {
				// Make a new gap! :D
				s.setGapLength(gapMinLength + rand.nextFloat()
						* (gapMaxLength - gapMinLength));
			}

			Path snakePath = new Path();
			snakePath.moveTo(x, y);
			snakePath.lineTo(s.getX(), s.getY());
			internalCanvas.drawPath(snakePath, paint);
		}

		if (aliveCount < 2 && gameState != GameState.GAME_OVER) {
			removeCallbacks(refreshTask);
			Toast t;
			if (aliveCount == 1) {
				int snakeN = 0;
				while (!snakes.get(snakeN).isAlive())
					snakeN++;
				t = Toast.makeText(selfContext, snakes.get(snakeN).getName()
						+ getContext().getString(R.string.charlie),
						Toast.LENGTH_SHORT);
			} else {
				t = Toast.makeText(selfContext, R.string.bill,
						Toast.LENGTH_SHORT);
			}
			t.show();
			gameState = GameState.GAME_OVER;
		}

		drawGameBox(internalCanvas);

		Matrix trans = new Matrix();
		scale = getHeight() / (float) height;
		trans.setScale(scale, scale);

		c.drawBitmap(b, trans, null);

		powerups.draw(c);

		drawText(c);

	}

	private boolean dies(List<Integer> coords, Bitmap field) {
		for (int i = 0; i < coords.size(); i += 2) {
			int x = coords.get(i);
			int y = coords.get(i + 1);

			if (x < 0 || y < 0 || x >= width || y >= height)
				return true;

			if ((b.getPixel(x, y) & 0xFFFFFF) != 0) {
				return true;
			}
		}
		return false;
	}

	private void drawText(Canvas c) {
		Paint black = new Paint();
		black.setARGB(255, 0, 0, 0);
		c.drawRect(new Rect(c.getHeight() + 5, 0, c.getWidth(), c.getHeight()),
				black);

		Paint textPaint;
		int i;
		float w;

		for (i = 0; i < snakes.size(); i++) {
			Snake s = snakes.get(i);
			w = s.getColor().getStrokeWidth();
			s.getColor().setStrokeWidth(0);
			textPaint = s.getColor();
			textPaint.setAntiAlias(true);
			float textSize = getHeight() / (float) 20;
			textPaint.setTextSize(textSize);
			c.drawText(s.getName(), c.getHeight() + textSize, textSize
					* (2 * i + 1), textPaint);
			c.drawText(String.valueOf(s.getScore()), c.getHeight() + textSize,
					textSize * (2 * i + 2), textPaint);
			textPaint.setStrokeWidth(w);
			textPaint.setAntiAlias(false);
		}
	}

	private void drawGameBox(Canvas c) {
		Paint gameBoxPaint = new Paint();
		gameBoxPaint.setARGB(255, 255, 255, 0);
		gameBoxPaint.setStyle(Paint.Style.STROKE);
		gameBoxPaint.setStrokeWidth(2);
		c.drawRect(new Rect(0, 0, width, height), gameBoxPaint);
	}

	private void initSnakes() {
		int snakeCount = ADKPrefs.getSingleton().getSnakeCount();
		int i;
		Snake n;
		Paint p;
		IController controller;
		controllers = new IController[snakeCount];
		for (i = 0; i < snakeCount; i++) {
			n = new Snake();
			p = new Paint();
			switch (i) {
			case 0:
				p.setARGB(255, 255, 0, 0);
				n.setName(getContext().getString(R.string.player1name));
				break;
			case 1:
				p.setARGB(255, 255, 255, 255);
				n.setName(getContext().getString(R.string.player2name));
				break;
			case 2:
				p.setARGB(255, 0, 255, 0);
				n.setName(getContext().getString(R.string.player3name));
				break;
			case 3:
				p.setARGB(255, 0, 0, 255);
				n.setName(getContext().getString(R.string.player4name));
				break;
			case 4:
				p.setARGB(255, 0, 255, 255);
				n.setName(getContext().getString(R.string.player5name));
				break;
			case 5:
				p.setARGB(255, 255, 0, 255);
				n.setName(getContext().getString(R.string.player6name));
				break;
			}
			p.setStyle(Paint.Style.STROKE);
			n.setColor(p);
			snakes.add(i, n);
			if (i > 0)
				controller = new AIController();
			else
				controller = ADKPrefs.getSingleton().getController();
			controller.setListeners(this);
			controller.setSnake(n);
			controllers[i] = controller;
		}
	}

	public int getGameState() {
		return gameState;
	}

	public void onPause() {
		if (gameState == GameState.RUNNING) {
			gameState = GameState.PAUSED;
			removeCallbacks(refreshTask);
		}
	}

	public void onResume() {
		if (gameState == GameState.PAUSED) {
			gameState = GameState.RUNNING;
			postDelayed(refreshTask, 40);
		}
		lastEngineLoop = new Date();
	}

	public void onClickHandler(View v) {
		if (gameState == GameState.GAME_OVER)
			prepareGame();

	}
}
