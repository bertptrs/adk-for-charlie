package nl.liacs.adk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ADKMainMenu extends Activity {
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
    }
    
    public void onClickStart(View v)
    {
    	startActivity(new Intent(this, ADKActivity.class));
    }
    
    public void onClickPreferences(View v)
    {
    	startActivity(new Intent(this, ADKPreferenceActivity.class));
    }
    
    public void onClickClose(View v) {
    	finish();
    }
}
