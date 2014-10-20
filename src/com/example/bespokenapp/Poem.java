/*
 * This is the page that will be called when the user selects a poem
 */

package com.example.bespokenapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Poem extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poem);
		
		String address = getIntent().getExtras().getString("url");
		WebView myWebView;
		myWebView = (WebView) findViewById(R.id.poemWebView);
		myWebView.loadUrl(address);
		myWebView.setWebViewClient(new WebViewClient()); 

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
