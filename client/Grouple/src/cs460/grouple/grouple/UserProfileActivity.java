package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import cs460.grouple.grouple.R;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * UserActivity displays the profile page of the logged-in user.
 * NEED TO CHANGE THIS TO USERPROFILEACTIVITY AND JUST REMOVE FRIENDPROFILE
 * CHECK .isCurrentUser() and then enable / disable EDIT PROFILE
 */
public class UserProfileActivity extends ActionBarActivity
{
	private ImageView iv;
	private Bitmap bmp;
	BroadcastReceiver broadcastReceiver;
	Intent parentIntent;
	Intent upIntent;
	View view;
	User user; //user who's profile this is
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_user_profile);
		//Can we do our user load in the profile to save loading time from earlier or will the sync be off?
		
		view = findViewById(R.id.userProfileContainer);
		load(view);
	}

	public void initActionBar()
	{
		Global global = ((Global) getApplicationContext());
		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		upButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				upIntent.putExtra("up", "true");
				startActivity(upIntent);
				finish();
			}
		});
		actionbarTitle.setText(user.getFullName() + "'s Profile"); //PANDA
	}

	public void load(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		//grabbing the user with the given email in the extras
		user = global.loadUser(extras.getString("email"));
		//thinking of loading in the friends names / emails and group names / ids at this time.
		//user.loadFriends();
		//user.loadGroups();
		
		String className = extras.getString("ParentClassName");


		/* Notifications */
		//global.fetchNumFriends(global.getCurrentUser());
		//global.fetchNumGroups(global.getCurrentUser());
		// User Profile Buttons
		Log.d("userprofileload", "friendsbuttonupa below");
		((Button) view.findViewById(R.id.friendsButtonUPA)).setText("Friends\n(" + user.getNumFriends() + ")");
		//((Button) view.findViewById(R.id.groupsButtonUPA))
			//	.setText("Groups\n(" + numGroups + ")");
		// set numfriends, numgroups, and numevents
		
		
		//View user = findViewById(R.id.userContainer);

		//global.setNotifications(user); PANDA

		// execute php script, using the current users email address to populate
		// the textviews
		initProfile();

		// initializing the action bar and killswitch listener
		initActionBar();
		initKillswitchListener();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);

		// Set up the image view
		if (iv == null)
		{
			iv = (ImageView) findViewById(R.id.profilePhoto);
		}
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
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			bmp = null;
			iv = null;
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("ParentClassName", "UserActivity");
			intent.putExtra("up", "false");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			upIntent.putExtra("up", "false");
			startActivity(upIntent);
			finish();
		}
		return false;
	}

	public void startEditProfileActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, ProfileEditActivity.class);
		intent.putExtra("ParentClassName", "UserActivity");
		startActivity(intent);
		bmp = null;
		iv = null;
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	public void initProfile()
	{
		//grab all of the values from the user
		String bio = user.getBio();
		//String age = jsonProfileArray.getString(2);
		String location = user.getLocation();
		String img = null;//jsonProfileArray.getString(5);

		// decode image back to android bitmap format
		/*byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
		if (decodedString != null)
		{
			bmp = BitmapFactory.decodeByteArray(decodedString, 0,
					decodedString.length);
		}
		// set the image
		if (bmp != null)
		{
			if (iv == null)
			{
				iv = (ImageView) findViewById(R.id.profilePhoto);

			}
			iv.setImageBitmap(bmp);
			img = null;
			decodedString = null;
		}
*/
		// TextView nameTextView = (TextView)
		// findViewById(R.id.nameEditTextEPA);
		TextView ageTextView = (TextView) findViewById(R.id.ageTextView);
		TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
		TextView bioTextView = (TextView) findViewById(R.id.bioTextView);
		// JSONObject bioJson = jsonProfileArray.getJSONObject(0);
		// nameTextView.setText(name);
	//	ageTextView.setText(age + " years old");
		bioTextView.setText(bio);
		locationTextView.setText(location);	
	}

	public void startGroupsCurrentActivity(View view)
	{
		Intent intent = new Intent(this, GroupsCurrentActivity.class);
		Global global = ((Global) getApplicationContext());
		intent.putExtra("ParentClassName", "UserActivity");
	//	intent.putExtra("email", global.getCurrentUser());
		intent.putExtra("mod", "true");
		//intent.putExtra("ParentEmail", global.getCurrentUser());
		startActivity(intent);
		bmp = null;
		iv = null;
	}

	public void startFriendsCurrentActivity(View view)
	{
		Intent intent = new Intent(this, FriendsCurrentActivity.class);
		Global global = ((Global) getApplicationContext());
		//String email = global.getCurrentUser();

		intent.putExtra("ParentClassName", "UserActivity");
		//intent.putExtra("ParentEmail", email);
		//intent.putExtra("Name", global.getName()); //PANDA
		intent.putExtra("email", user.getEmail());
		intent.putExtra("mod", "true");
		startActivity(intent);
		bmp = null;
		iv = null;
	}

	public void startEventsActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, EventsActivity.class);
		intent.putExtra("ParentClassName", "UserActivity");
		startActivity(intent);
		bmp = null;
		iv = null;
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
