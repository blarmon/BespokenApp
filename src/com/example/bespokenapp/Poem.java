/*
 * This is the page that will be called when the user selects a poem
 */

package com.example.bespokenapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Poem extends Activity {

	SwipeRefreshLayout swipeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poem);

		final String address = getIntent().getExtras().getString("url");
		final WebView myWebView;
		myWebView = (WebView) findViewById(R.id.poemWebView);
		myWebView.loadUrl(address);
		myWebView.setWebViewClient(new WebViewClient()); 

		swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);	 
		swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				swipeView.setRefreshing(true);
				myWebView.loadUrl(address);
				( new Handler()).postDelayed(new Runnable() {
					@Override
					public void run() {
						swipeView.setRefreshing(false);
					}
				}, 3000);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.poem_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.trash:
			trashClicked();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void trashClicked(){
		//here
	}
}


