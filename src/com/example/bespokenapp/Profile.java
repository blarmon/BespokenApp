/*
 * This page will be called when the user selects a profile (either their own or another person's)
 */

package com.example.bespokenapp;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Profile extends Activity {
	
	private SwipeRefreshLayout swipeView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		String address2 = getIntent().getExtras().getString("url");
		final WebView myWebView2;
		myWebView2 = (WebView) findViewById(R.id.profileWebView);
		myWebView2.loadUrl(address2);
		myWebView2.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {

					//This determines whether the user has clicked on either a poem or profile page, 
					//and then sends them to the appropriate activity.
					List<String> temp = Uri.parse(url).getPathSegments();
					if (temp.contains("followers")) {
						goToFollow();
						return true; //this ensures that the link isn't also opened in the parent activity.
					}
					else if (temp.contains("following")) {
						goToFollow();
						return true; //this ensures that the link isn't also opened in the parent activity.
					}
					else {
						return false;
					}
			}
		}); 
		
		swipeView2 = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout2);	 
		swipeView2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				swipeView2.setRefreshing(true);
				myWebView2.reload();
				( new Handler()).postDelayed(new Runnable() {
					@Override
					public void run() {
						swipeView2.setRefreshing(false);
					}
				}, 3000);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.profile:
			goToProfilePage("http://bespokenapp.appspot.com/my-profile");
			return true;
		case R.id.search:
			goToSearchPage();
			return true;
		case R.id.record:
			goToRecordPage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void goToRecordPage(){
		Intent intent = new Intent(this, RecordPoem.class);
		startActivity(intent);
	}
	
	public void goToFollow() {
		Intent intent = new Intent(this, Follow.class);
		startActivity(intent);
	}
	
	public void goToSearchPage() {
		Intent intent = new Intent(this, SearchPage.class);
		startActivity(intent);
	}

	public void goToHomePage(){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void goToProfilePage(String profileURL){
		Intent intent = new Intent(this, Profile.class);
		intent.putExtra("url", profileURL);
		startActivity(intent);
	}
	public void goToPoemPage(String poemURL){
		Intent intent = new Intent(this, Poem.class);
		intent.putExtra("url", poemURL);
		startActivity(intent);
	}
}
