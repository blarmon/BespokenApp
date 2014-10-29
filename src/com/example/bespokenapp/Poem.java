/* This is the page that will be called when the user selects a poem
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


public class Poem extends Activity implements ActionBar.TabListener{

	private SwipeRefreshLayout swipeView;
	ViewPager myViewPager;
	SectionsPagerAdapter mSectionsPagerAdapter;
	static WebView myWebView1, myWebView2;
	static String poemAddress = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poem);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		String address = getIntent().getExtras().getString("url");
		poemAddress = address;
		final WebView myWebView;
		myWebView = (WebView) findViewById(R.id.poemWebView);
		myWebView.loadUrl(address);
		myWebView.setWebViewClient(new CustomWebViewClient()); 


		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.

		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		myViewPager = (ViewPager) findViewById(R.id.vpPager);
		myViewPager.setAdapter(mSectionsPagerAdapter);


	}
	
	//This is specifically for the link to the profile page to start in a new "profile" activity
	public class CustomWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			//This determines whether the user has clicked on either a poem or profile page, 
			//and then sends them to the appropriate activity.

			List<String> temp = Uri.parse(url).getPathSegments();

			if (temp.contains("user") || temp.contains("my-profile")) {

				goToProfilePage(url);
				return true; //this ensures that the link isn't also opened in the parent activity.
			}

			else {
				return false;
			}
		}
	}
	public void goToProfilePage(String profileURL){
		Intent intent = new Intent(this, Profile.class);
		intent.putExtra("url", profileURL);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.poem_menu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
		
		default:

			return super.onOptionsItemSelected(item);
		}
	}



	public void trashClicked(){
		//here
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

			else {
				return PlaceholderFragment4.newInstance(position + 2);
			}
		}



		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}



		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {

			case 0:

				String title1 = "Poet's Notes";
				return title1;

			case 1:

				String title2 = "Feedback";
				return title2;

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
			myWebView1.loadUrl(poemAddress + "/info");
			myWebView1.setWebViewClient(new MyWebViewClient());

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

					((MainActivity)getActivity()).goToProfilePage(url);
					return true; //this ensures that the link isn't also opened in the parent activity.
				}

				else if (temp.contains("poem")) {
					((MainActivity)getActivity()).goToPoemPage(url);
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
			myWebView2.loadUrl(poemAddress + "/comments");
			myWebView2.setWebViewClient(new MyWebViewClient());
			
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
					Log.d("went to profile page?", "after");
					((MainActivity)getActivity()).goToProfilePage(url);
					Log.d("went to profile page?", "after");
					return true; //this ensures that the link isn't also opened in the parent activity.
				}

				else if (temp.contains("poem")) {
					
					((MainActivity)getActivity()).goToPoemPage(url);
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

		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView1.canGoBack()) {
			myWebView1.goBack();
			return true;
		}

		else if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView2.canGoBack()) {
			myWebView2.goBack();
			return true;
		}

		// If it wasn't the Back key or there's no web page history, bubble up to the default
		// system behavior (probably exit the activity)

		return super.onKeyDown(keyCode, event);
	}
}



