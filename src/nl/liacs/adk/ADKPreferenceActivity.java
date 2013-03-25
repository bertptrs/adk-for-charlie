package nl.liacs.adk;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ADKPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
	}
}
