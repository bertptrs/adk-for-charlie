package nl.liacs.adk;

import java.util.ArrayList;

import nl.liacs.adk.PowerupManager.PowerupEffects;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ADKPrefs {
	// Singleton
	private static ADKPrefs instance = null;
	private static Context context = null;
	
	public static void setContext(Context c)
	{
		context = c;
	}
	
	public static ADKPrefs getSingleton()
	{
		if (instance == null)
			instance = new ADKPrefs();
		return instance;
	}
	
	// Class
	private SharedPreferences prefs;
	
	private ADKPrefs()
	{
		prefs = context.getSharedPreferences("nl.liacs.adk_preferences", Context.MODE_PRIVATE);
	}
	
	public String getControllerName()
	{
		return prefs.getString("ControllerType", "Touch");
	}
	
	public IController getController()
	{
		String type = getControllerName();
		
		if (type.equals("AI"))
			return new AIController();
		if (type.equals("Orientation"))
			return new OrientationController();
		
		// else
		return new TouchController();
	}
	
	public void setController(String type)
	{
		Editor e = prefs.edit();
		e.putString("ControllerType", type);
		e.commit();
	}
	
	public int getSnakeCount()
	{
		return Integer.valueOf(prefs.getString("SnakeCount", "3"));
	}
	
	public void setSnakeCount(int count)
	{
		Editor e = prefs.edit();
		e.putString("SnakeCount", String.valueOf(count));
		e.commit();
	}
	
	public float getSnakeSpeed()
	{
		return Float.valueOf(prefs.getString("SnakeSpeed", "50"));
	}
	
	public void setSnakeSpeed(float speed)
	{
		Editor e = prefs.edit();
		e.putString("SnakeSpeed", String.valueOf(speed));
		e.commit();
	}
	
	public String[] getPowerups()
	{
		ArrayList<String> powerups = new ArrayList<String>();
		PowerupEffects effects[] = PowerupEffects.values();
		for (int i = 0; i < effects.length; i++)
		{
			if (prefs.getBoolean(effects[i].toString(), true))
				powerups.add(effects[i].toString());
		}
		String p[] = new String[powerups.size()];
		return powerups.toArray(p);
	}
	
	public int getNumPowerups()
	{
		return Integer.valueOf(prefs.getString("NumPowerups", "2"));
	}
	
	public boolean getMusicEnabled()
	{
		return prefs.getBoolean("MusicEnabled", false);
	}
	
	public int getGapChance()
	{
		return Integer.valueOf(prefs.getString("GapChance", "1500"));
	}
	
	public boolean isAIHighQuality() {
		return prefs.getBoolean("HQAI", true);
	}
}
