package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;

import android.support.v4.app.Fragment;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends ActionBarActivity
{
	Button loginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		ActionBar ab = getActionBar();
		ab.hide();
		Log.d("app666", "we created");
		
		//populating views in global (for access from global)
		/*ArrayList<View> views = new ArrayList<View>();
		View homeLayout = (View)findViewById(R.id.homeLayout);
		View userLayout = (View)findViewById(R.id.userLayout);
		View groupsLayout = (View)findViewById(R.id.groupsLayout);
		View eventsLayout = (View)findViewById(R.id.eventsLayout);
		View friendsLayout = (View)findViewById(R.id.friendsLayout);
		View friendRequestsLayout = (View)findViewById(R.id.friendRequestsLayout);
		View addFriendLayout = (View)findViewById(R.id.addFriendLayout);
		View currentFriendsLayout = (View)findViewById(R.id.currentFriendsLayout);
		views.add(homeLayout);
		views.add(userLayout);
		views.add(groupsLayout);
		views.add(eventsLayout);
		views.add(friendsLayout);
		views.add(friendRequestsLayout);
		views.add(addFriendLayout);
		views.add(currentFriendsLayout);
		Global global = ((Global) getApplicationContext());
		//global.setViews(views);*/

		//START KILL SWITCH LISTENER
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				// close activity
				if (intent.getAction().equals("CLOSE_ALL"))
				{
					Log.d("app666", "we killin the login it");
					//System.exit(1);
					finish();
				}
			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
		// End Kill switch listener
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void startRegisterActivity(View view)
	{
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	public void startHomeActivity()
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}

	public String readJSONFeed(String URL)
	{
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(URL);
		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200)
			{
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null)
				{
					stringBuilder.append(line);
				}
				inputStream.close();
			} else
			{
				Log.d("JSON", "Failed to download file");
			}
		} catch (Exception e)
		{
			Log.d("readJSONFeed", e.getLocalizedMessage());
		}
		return stringBuilder.toString();
	}

	public void loginButton(View view)
	{
		// Create helper and if successful, will bring the correct home
		// activity.
		EditText emailEditText = (EditText) findViewById(R.id.emailEditTextLA);
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditTextLA);
		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		Global global = ((Global) getApplicationContext());

		global.setCurrentUser(email);
		

		new getLoginTask()
				.execute("http://98.213.107.172/android_connect/get_login.php?email="
						+ email + "&password=" + password);

	}

	private class getLoginTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0]);
		}

		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// successful
					Global global = ((Global) getApplicationContext());
					// check for current number of friend requests
					global.fetchNumFriendRequests();
					//Sets this users name.
					global.fetchName();
					Thread.sleep(500); //Sleeping to let home activity start up
					startHomeActivity();
				} 
				else
				{
					// failed
					System.out.println("failed");
					TextView loginFail = (TextView) findViewById(R.id.loginFailTextViewLA);
					loginFail.setText(jsonObject.getString("message"));
					loginFail.setVisibility(0);
				}
			} 
			catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		System.out.println("ARe we not yet there");
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			System.exit(0);
		}
		return false;
	}
}