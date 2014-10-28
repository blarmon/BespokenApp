/*
 * This is the home page - it brings up the login screen and has tabs at the top for "my feed" and "top poems"
 * The other pages can be accessed through the action bar at the top
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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	static WebView myWebView1, myWebView2;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
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
		case R.id.logout:
			logOut();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void logOut(){
		WebView myWebView = (WebView) findViewById(R.id.webview2);
		myWebView.loadUrl("http://www.facebook.com/l.php?u=http%3A%2F%2Fbespokenapp.appspot.com%2F_ah%2Flogout%3Fcontinue%3Dhttps%253A%252F%252Fwww.google.com%252Faccounts%252FLogout%253Fcontinue%253Dhttps%253A%252F%252Fappengine.google.com%252F_ah%252Flogout%25253Fcontinue%25253Dhttp%253A%252F%252Fbespokenapp.appspot.com%252F%2526service%253Dah&h=lAQEcQGJW");
		myWebView.setWebViewClient(new WebViewClient(){
			public void onPageFinished(WebView view, String url) {
				doneLoading();
			}
		});
	}
	
	void doneLoading(){
		Intent intent = new Intent(this, Login.class);
		String loggedOut = "logged out";
		intent.putExtra("loggedOut", loggedOut);
		startActivity(intent);
	}

	public void goToRecordPage(){
		Intent intent = new Intent(this, RecordPoem.class);
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
	/*
	public void goToRecordPage(){
		Intent intent = new Intent(this, RecordPoem.class);
		 startActivity(intent);
	}
	public void goToRecordPage(){
		Intent intent = new Intent(this, RecordPoem.class);
		 startActivity(intent);
	}
	 */

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
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
				return PlaceholderFragment.newInstance(position + 1);
			}
			else {
				return PlaceholderFragment2.newInstance(position + 2);
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
				String title1 = "My Feed";
				return title1;
			case 1:
				String title2 = "Top Poems";
				return title2;
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		SwipeRefreshLayout swipeView3;
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.mypoems, container,
					false);

			myWebView1 = (WebView) rootView.findViewById(R.id.webview1);
			myWebView1.loadUrl("http://bespokenapp.appspot.com");
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
	public static class PlaceholderFragment2 extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		SwipeRefreshLayout swipeView4;
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment2 newInstance(int sectionNumber) {
			PlaceholderFragment2 fragment = new PlaceholderFragment2();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment2() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.mypoems2, container,
					false);

			myWebView2 = (WebView) rootView.findViewById(R.id.webview2);
			myWebView2.loadUrl("http://bespokenapp.appspot.com/top-poems");
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
