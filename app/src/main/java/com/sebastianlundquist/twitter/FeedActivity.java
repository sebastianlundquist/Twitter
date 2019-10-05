package com.sebastianlundquist.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

	public void refreshTweets() {
		final ListView twootList = findViewById(R.id.twootList);
		final List<Map<String, String>> twootData = new ArrayList<>();

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Twoot");
		query.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));
		query.orderByDescending("createdAt");
		query.setLimit(20);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject twoot : objects) {
						Map<String, String> twootInfo = new HashMap<>();
						twootInfo.put("content", twoot.getString("twoot"));
						twootInfo.put("username", twoot.getString("username"));
						twootData.add(twootInfo);
					}
					SimpleAdapter simpleAdapter = new SimpleAdapter(FeedActivity.this, twootData, android.R.layout.simple_list_item_2, new String[] {"content", "username"}, new int[] { android.R.id.text1, android.R.id.text2 });
					twootList.setAdapter(simpleAdapter);
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		setTitle("Twotter: Feed");
		refreshTweets();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshTweets();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.twoot) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Send a Twoot");
			final EditText twootEditText = new EditText(this);
			builder.setView(twootEditText);
			builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					Log.i("Info", twootEditText.getText().toString());
					ParseObject twoot = new ParseObject("Twoot");
					twoot.put("twoot", twootEditText.getText().toString());
					twoot.put("username", ParseUser.getCurrentUser().getUsername());
					twoot.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							if (e == null) {
								Toast.makeText(FeedActivity.this, "Tweet posted!", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(FeedActivity.this, "Tweet failed :(", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.cancel();
				}
			});
			builder.show();
		}
		else if (item.getItemId() == R.id.follow) {
			Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
			startActivity(intent);
		}
		else if (item.getItemId() == R.id.logout) {
			ParseUser.logOut();
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
