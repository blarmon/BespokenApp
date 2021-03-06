//This page load up the page before the actual login page.  It's just a nice image and a button that says 'login with google'
//it takes you to login, which will take you to main activity automatically if you're logged in already


package com.example.bespokenapp;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
public class PreLogin extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pre_login);
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		//get the height and width of the screen for any device
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		
		//set height and width of imageview to that height and width
		ImageView image = (ImageView) findViewById(R.id.image);
		image.getLayoutParams().height = height;
		image.getLayoutParams().width = width;
		image.requestLayout();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pre_login, menu);
		return true;
	}
	
	//called on button press, goes to login page (declaredn in xml file).  deletes prelogin from the stack and gets rid of animation between activities via flags
	public void goToLogin(View view) {
		finish();
		Intent intent = getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
		Intent intent1 = new Intent(this, Login.class);
		startActivity(intent1);
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
