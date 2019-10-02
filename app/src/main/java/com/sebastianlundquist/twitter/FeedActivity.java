package com.sebastianlundquist.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		setTitle("Twotter: Feed");

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
}
