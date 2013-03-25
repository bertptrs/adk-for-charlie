package nl.liacs.adk;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

public class ADKActivity extends Activity {
	private GameFieldView game = null;
	private MediaPlayer mMplayer;
	private Handler mHandler;
	private ADKPrefs prefs;
	private ADKActivity selfReference = null;
	private Runnable gameStateChecker = new Runnable() {

		public void run() {
			if(game != null) {
				mHandler.removeCallbacks(this);
				if(game.getGameState() == GameState.GAME_OVER) {
					int targetScore = (game.snakes.size()-1)*10;
					int highestScore = -1;
					Snake highestSnake = null;
					for (Snake s : game.snakes)
					{
						if (s.getScore() > highestScore)
						{
							highestScore = s.getScore();
							highestSnake = s;
						}
					}
					if (highestScore >= targetScore)
					{
						Toast.makeText(selfReference, highestSnake.getName()
								+ getString(R.string.chuck),
								Toast.LENGTH_SHORT).show();
						finish();
					}
				}
				mHandler.postDelayed(this, 100);
				
			}
			
		}

	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		selfReference = this;
		ADKPrefs.setContext(this);
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);
		game = (GameFieldView) findViewById(R.id.Game);
		prefs = ADKPrefs.getSingleton();
		
		mHandler = new Handler();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		game.onPause();
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_preferences:
			this.finish();
			startActivity(new Intent(this, ADKPreferenceActivity.class));
			return true;
		case R.id.option_quittohome:
			if (getParent() != null)
				getParent().finish();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		game.onResume();
		super.onOptionsMenuClosed(menu);
	}

	@Override
	protected void onPause() {
		game.onPause();
		super.onPause();
		mHandler.removeCallbacks(gameStateChecker);
		if (!prefs.getMusicEnabled())
			return;
		mMplayer.stop();
		mMplayer.release();
	}

	@Override
	protected void onResume() {
		game.onResume();
		super.onResume();
		mHandler.postDelayed(gameStateChecker, 100);
		if (!prefs.getMusicEnabled())
			return;
		mMplayer = MediaPlayer.create(getApplicationContext(), R.raw.tetris);
		mMplayer.start();
		mMplayer.setLooping(true);
	}
}