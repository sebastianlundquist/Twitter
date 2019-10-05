package com.sebastianlundquist.twitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

	boolean signUpModeActive = true;
	TextView loginText;
	EditText userInput;
	EditText passwordInput;

	public void redirectUser() {
		if (ParseUser.getCurrentUser() != null) {
			Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
			startActivity(intent);
		}
	}

	public void signUpOrLogin(View view) {
		if (!userInput.getText().toString().matches("") && !passwordInput.getText().toString().matches("")) {
			if (signUpModeActive) {
				ParseUser newUser = new ParseUser();
				newUser.setUsername(userInput.getText().toString());
				newUser.setPassword(passwordInput.getText().toString());
				newUser.put("isFollowing", new ArrayList<String>());
				newUser.signUpInBackground(new SignUpCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							redirectUser();
						}
						else {
							Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
			else {
				ParseUser.logInInBackground(userInput.getText().toString(), passwordInput.getText().toString(), new LogInCallback() {
					@Override
					public void done(ParseUser user, ParseException e) {
						if (e == null) {
							redirectUser();
						}
						else {
							Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}
		else {
			Toast.makeText(this, "Username and password are required.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle("Twotter: Login");
		redirectUser();
		loginText = findViewById(R.id.loginText);
		loginText.setOnClickListener(this);
		userInput = findViewById(R.id.userInput);
		passwordInput = findViewById(R.id.passwordInput);
		ImageView logoView = findViewById(R.id.logoView);
		ConstraintLayout backgroundLayout = findViewById(R.id.backgroundLayout);
		logoView.setOnClickListener(this);
		backgroundLayout.setOnClickListener(this);
		passwordInput.setOnKeyListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.loginText) {
			Button signUpButton = findViewById(R.id.signUpButton);
			if (signUpModeActive) {
				signUpModeActive = false;
				signUpButton.setText(R.string.login);
				loginText.setText(R.string.or_sign_up);
			}
			else {
				signUpModeActive = true;
				signUpButton.setText(R.string.sign_up);
				loginText.setText(R.string.or_login);
			}
		}
		else if (view.getId() == R.id.logoView || view.getId() == R.id.backgroundLayout) {
			InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	@Override
	public boolean onKey(View view, int i, KeyEvent keyEvent) {
		if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
			signUpOrLogin(view);
		}
		return false;
	}
}
