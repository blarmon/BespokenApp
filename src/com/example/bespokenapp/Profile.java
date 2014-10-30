/*
 * This page will be called when the user selects a profile (either their own or another person's)
 */

package com.example.bespokenapp;

import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class Profile extends Activity implements ActionBar.TabListener{

	private SwipeRefreshLayout swipeView2;
	ViewPager myViewPager;
	SectionsPagerAdapter mySectionsPagerAdapter;
	static WebView myWebView, myWebView1, myWebView2, myWebView3;
	static String profileAddress = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		String address2 = getIntent().getExtras().getString("url");
		profileAddress = address2;
		//for some reason, when going through the "/my-profile" link it's appending "/followers" to the end of the url
		if (profileAddress.contains("followers")) {
			profileAddress = profileAddress.replace("/followers", "");
		}
		final WebView myWebView2;
		myWebView = (WebView) findViewById(R.id.profileWebView);
		myWebView.loadUrl(profileAddress);
		
		Log.d("address myWebView", profileAddress);
		
		myWebView.setBackgroundColor(Color.parseColor("#7fcfd6"));//set the background color of the webview to light blue
		myWebView.setWebViewClient(new WebViewClient() {
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


		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.

		mySectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		myViewPager = (ViewPager) findViewById(R.id.vpPager2);
		myViewPager.setAdapter(mySectionsPagerAdapter);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
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
		case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
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
		//not used
		Intent intent = new Intent(this, Follow.class);
		startActivity(intent);
	}

	public void goToSearchPage() {
		Intent intent = new Intent(this, SearchPage.class);
		startActivity(intent);
	}

	public void goToHomePage(){
		//not used
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


	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.

		myViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).

			if (position == 0) {
				return PlaceholderFragment3.newInstance(position + 1);
			}

			else if (position == 1){
				return PlaceholderFragment4.newInstance(position + 2);
			}
			else {
				return PlaceholderFragment5.newInstance(position + 3);
			}
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {

			case 0:

				String title1 = "Poems";
				return title1;

			case 1:

				String title2 = "Followers";
				return title2;
			case 2:
				String title3 = "Following";
				return title3;
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */

	public static class PlaceholderFragment3 extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */

		SwipeRefreshLayout swipeView3;

		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */

		public static PlaceholderFragment3 newInstance(int sectionNumber) {

			PlaceholderFragment3 fragment = new PlaceholderFragment3();
			Bundle args = new Bundle();

			args.putInt(ARG_SECTION_NUMBER, sectionNumber);

			fragment.setArguments(args);
			return fragment;

		}

		public PlaceholderFragment3() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.mypoems, container,
					false);

			myWebView1 = (WebView) rootView.findViewById(R.id.webview1);
			myWebView1.loadUrl(profileAddress + "/poems");
			myWebView1.setWebViewClient(new MyWebViewClient());
			
			Log.d("address myWebView1", profileAddress);

			//allows user to pull down to refresh the page
			swipeView3 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout3);	 
			swipeView3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					swipeView3.setRefreshing(true);
					myWebView1.reload();
					( new Handler()).postDelayed(new Runnable() {
						@Override
						public void run() {
							swipeView3.setRefreshing(false);
						}
					}, 3000);
				}
			});
			return rootView;
		}

		/*
		 * This method gives us custom control over what happens with the links we click.
		 * It's still problematic for going back to the main activity (it stays on the same page)
		 */

		public class MyWebViewClient extends WebViewClient {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				//This determines whether the user has clicked on either a poem or profile page, 
				//and then sends them to the appropriate activity.

				List<String> temp = Uri.parse(url).getPathSegments();

				if (temp.contains("user")) {

					((Profile)getActivity()).goToProfilePage(url);
					return true; //this ensures that the link isn't also opened in the parent activity.
				}

				else if (temp.contains("poem")) {
					((Profile)getActivity()).goToPoemPage(url);
					return true; //this ensures that the link isn't also opened in the parent activity.
				}

				else {
					return false;
				}
			}
		}
	}

	/**
	 * A second placeholder fragment containing a simple view.
	 */

	public static class PlaceholderFragment4 extends Fragment {

		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */

		SwipeRefreshLayout swipeView4;
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */

		public static PlaceholderFragment4 newInstance(int sectionNumber) {

			PlaceholderFragment4 fragment = new PlaceholderFragment4();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment4() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.mypoems2, container,
					false);

			myWebView2 = (WebView) rootView.findViewById(R.id.webview2);
			myWebView2.loadUrl(profileAddress + "/followers");
			myWebView2.setWebViewClient(new MyWebViewClient());

			Log.d("address myWebView2", profileAddress);
			
			//allows the user to pull down to refresh the page
			swipeView4 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout4);	 
			swipeView4.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					swipeView4.setRefreshing(true);
					myWebView2.reload();
					( new Handler()).postDelayed(new Runnable() {
						@Override
						public void run() {
							swipeView4.setRefreshing(false);
						}
					}, 3000);
				}
			});

			return rootView;
		}



		/*
		 * This method gives us custom control over what happens with the links we click.
		 * It's still problematic for going back to the main activity (it stays on the same page)
		 */

		private class MyWebViewClient extends WebViewClient {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				//This determines whether the user has clicked on either a poem or profile page, 
				//and then sends them to the appropriate activity.

				List<String> temp = Uri.parse(url).getPathSegments();

				if (temp.contains("user")) {
					((Profile)getActivity()).goToProfilePage(url);
					return true; //this ensures that the link isn't also opened in the parent activity.
				}

				else if (temp.contains("poem")) {

					((Profile)getActivity()).goToPoemPage(url);
					return true; //this ensures that the link isn't also opened in the parent activity.
				}

				else {
					return false;
				}
			}
		}
	}
	
	/**
	 * A third placeholder fragment containing a simple view.
	 */

	public static class PlaceholderFragment5 extends Fragment {

		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */

		SwipeRefreshLayout swipeView5;
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */

		public static PlaceholderFragment5 newInstance(int sectionNumber) {

			PlaceholderFragment5 fragment = new PlaceholderFragment5();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;

		}

		public PlaceholderFragment5() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.mypoems2, container,
					false);

			myWebView3 = (WebView) rootView.findViewById(R.id.webview2);
			myWebView3.loadUrl(profileAddress + "/following");
			myWebView3.setWebViewClient(new MyWebViewClient());
			
			Log.d("address myWebView2", profileAddress);

			//allows the user to pull down to refresh the page
			swipeView5 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout4);	 
			swipeView5.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					swipeView5.setRefreshing(true);
					myWebView3.reload();
					( new Handler()).postDelayed(new Runnable() {
						@Override
						public void run() {
							swipeView5.setRefreshing(false);
						}
					}, 3000);
				}
			});

			return rootView;
		}

		/*
		 * This method gives us custom control over what happens with the links we click.
		 * It's still problematic for going back to the main activity (it stays on the same page)
		 */

		private class MyWebViewClient extends WebViewClient {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				//This determines whether the user has clicked on either a poem or profile page, 
				//and then sends them to the appropriate activity.

				List<String> temp = Uri.parse(url).getPathSegments();

				if (temp.contains("user")) {
					((Profile)getActivity()).goToProfilePage(url);
					return true; //this ensures that the link isn't also opened in the parent activity.
				}

				else if (temp.contains("poem")) {

					((Profile)getActivity()).goToPoemPage(url);
					return true; //this ensures that the link isn't also opened in the parent activity.
				}

				else {
					return false;
				}
			}
		}
	}

	/*
	 * This method enables back-button functionality for the WebViews.  
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// Check if the key event was the Back button
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			//because of weird things happening with "/my-profile", force the activity to exit when clicking back.
			NavUtils.navigateUpFromSameTask(this); 
			return true;
		}

		// If it wasn't the Back key or there's no web page history, bubble up to the default
		// system behavior (probably exit the activity)

		return super.onKeyDown(keyCode, event);
	}
}
