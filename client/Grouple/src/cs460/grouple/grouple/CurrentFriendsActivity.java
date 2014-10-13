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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Build;

public class CurrentFriendsActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_friends);
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		Global global = ((Global)getApplicationContext());
		String email = global.getCurrentUser();
		System.out.println("Email: " + email);
		new getFriendsTask()
				.execute("http://98.213.107.172/android_connect/get_friends.php?email="
						+ email);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.current_friends, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{

		public PlaceholderFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_current_friends,
					container, false);
			return rootView;
		}
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
					System.out.println("New line: " + line);
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
	private class getFriendsTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0]);
		}

		protected void onPostExecute(String result)
		{
			LayoutInflater li = getLayoutInflater();
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					ArrayList<String> friends = new ArrayList<String>();
					JSONArray jsonFriends = (JSONArray)jsonObject.getJSONArray("friends").getJSONArray(0);
					
					if (jsonFriends != null)
					{
						System.out.println(jsonFriends.toString() + "\n" + jsonFriends.length());
		
						//looping thru json and adding to an array
						for (int i = 0; i < jsonFriends.length(); i++)
						{					
							String raw = jsonFriends.get(i).toString().replace("\"","").replace("]", "").replace("[", "");
							String row = raw.substring(0,1).toUpperCase() + raw.substring(1);
							friends.add(row);
							System.out.println("Row: " + row +"\nCount: " + i);
						}
						//looping thru array and inflating listitems to the friend requests list
						for (int i = 0; i < friends.size(); i++)
						{
							RelativeLayout currentFriendsRL =  (RelativeLayout)findViewById(R.id.currentFriendsRelativeLayout);
							
						
							
							li.inflate(R.layout.listitem_friend, currentFriendsRL);
							GridLayout rowRL = (GridLayout)currentFriendsRL.findViewById(R.id.friendGridLayout);
							rowRL.setId(i);//(newIDStr);
							((TextView)rowRL.findViewById(R.id.emailTextViewFLI)).setText(friends.get(i));
					
							int y = 100*(i+1);
							rowRL.setY(y);
						}
					}
					//successful
					//startHomeActivity();
				} else
				{
					// failed
					//TextView loginFail = (TextView) findViewById(R.id.loginFailTextViewLA);
					//loginFail.setVisibility(0);
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
