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
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

	ArrayList<String> users = new ArrayList<>();
	ArrayAdapter adapter;

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
								Toast.makeText(UsersActivity.this, "Tweet posted!", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(UsersActivity.this, "Tweet failed :(", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					Log.i("Info", "No twoot 4 u");
					dialogInterface.cancel();
				}
			});
			builder.show();
		}
		else if (item.getItemId() == R.id.feed) {
			Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
			startActivity(intent);
		}
		else if (item.getItemId() == R.id.logout) {
			ParseUser.logOut();
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users);
		setTitle("Twotter: User List");

		final ListView usersList = findViewById(R.id.usersList);
		usersList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users);
		usersList.setAdapter(adapter);
		usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				CheckedTextView checkedTextView = (CheckedTextView)view;
				if (checkedTextView.isChecked()) {
					ParseUser.getCurrentUser().add("isFollowing", users.get(i));
				}
				else {
					ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(i));
					List tempUsers = ParseUser.getCurrentUser().getList("isFollowing");
					ParseUser.getCurrentUser().remove("isFollowing");
					ParseUser.getCurrentUser().put("isFollowing", tempUsers);
				}
				ParseUser.getCurrentUser().saveInBackground();
			}
		});

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> objects, ParseException e) {
				if (e == null && objects.size() > 0) {
					for (ParseUser user : objects) {
						users.add(user.getUsername());
					}
					adapter.notifyDataSetChanged();
					for (String name : users) {
						if (ParseUser.getCurrentUser().getList("isFollowing").contains(name)) {
							usersList.setItemChecked(users.indexOf(name), true);
						}
					}
				}
			}
		});
	}
}
