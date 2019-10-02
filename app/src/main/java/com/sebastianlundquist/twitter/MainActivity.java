package com.sebastianlundquist.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	public void redirectUser() {
		if (ParseUser.getCurrentUser() != null) {
			Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
			startActivity(intent);
		}
	}

	public void signUpLogin(View view) {
		final EditText userInput = findViewById(R.id.userInput);
		final EditText passwordInput = findViewById(R.id.passwordInput);
		ParseUser.logInInBackground(userInput.getText().toString(), passwordInput.getText().toString(), new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if (e == null) {
					redirectUser();
				}
				else {
					ParseUser newUser = new ParseUser();
					newUser.setUsername(userInput.getText().toString());
					newUser.setPassword(passwordInput.getText().toString());
					newUser.put("isFollowing", new ArrayList<String>());
					newUser.signUpInBackground(new SignUpCallback() {
						@Override
						public void done(ParseException e) {
							if (e == null) {
								Log.i("Signup", "Success");
								redirectUser();
							}
							else {
								Toast.makeText(MainActivity.this, e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle("Twotter: Login");
		redirectUser();
	}
}
