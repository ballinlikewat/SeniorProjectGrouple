package cs460.grouple.grouple;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SettingsActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initKillswitchListener();
		setContentView(R.layout.activity_settings);
		/*Action bar*/
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);

		actionbarTitle.setText("Settings");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			Global global = ((Global) getApplicationContext());
			global.setAcceptEmail("");
			global.setCurrentUser("");
			global.setDeclineEmail("");
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void initKillswitchListener()
	{
		// START KILL SWITCH LISTENER
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction("CLOSE_ALL");
				broadcastReceiver = new BroadcastReceiver()
				{
					@Override
					public void onReceive(Context context, Intent intent)
					{
						// close activity
						if (intent.getAction().equals("CLOSE_ALL"))
						{
							Log.d("app666", "we killin the login it");
							// System.exit(1);
							finish();
						}

					}
				};
				registerReceiver(broadcastReceiver, intentFilter);
				// End Kill switch listener
	}

}
