package com.sebastianlundquist.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

	ArrayList<String> users = new ArrayList<>();
	ArrayAdapter adapter;

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
