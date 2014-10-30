package com.example.bespokenapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		//create a webview, any webview that loads a google appspot page will take you to a login screen if you are not currently logged in
		WebView myWebView;
		myWebView = (WebView) findViewById(R.id.profileWebViewLogin);
		myWebView.loadUrl("http://bespokenapp.appspot.com");
		boolean test = false;

		myWebView.setWebViewClient(new WebViewClient(){
			//does something when a page is finished loading
			public void onPageFinished(WebView view, String url) {

				//the login page contains servicelogin, we parse that url, if we're not on the login screen we start up mainactivity
				if (!url.contains("ServiceLogin"))
				{
					doneLoading();

				}
			}
		});

	}

	void doneLoading(){
		//restart the activity and take it off the stack.  this sucks but I was running out of time, and I can't let you press back to go back on the login screen
		finish();
		startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
		
		//start up our feed page with no animation, it looks smoother, especially if they are already logged in and we are bypassing login
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}




}
