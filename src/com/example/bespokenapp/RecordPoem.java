/*
 * name: Lauren Naylor
 * This is the page the user will use to record their poetry - right now the post button is not functional
 */

package com.example.bespokenapp;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

public class RecordPoem extends Activity {

	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String outputFile = null;
	private ImageButton startBtn;
	private Button stopBtn;
	private Button deleteBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_poem);

		startBtn = (ImageButton)findViewById(R.id.micImage);
		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start(v);//starts recording
			}
		});


		stopBtn = (Button) findViewById(R.id.stop);
		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stop(v);//stops recording
				//then opens a popup window for playback
				LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
				v = layoutInflater.inflate(R.layout.playback, null);
				final PopupWindow popupWindow = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				popupWindow.showAtLocation(v, Gravity.TOP, 0, 0);
				EditText poemName = (EditText)v.findViewById(R.id.poemName);

				popupWindow.setFocusable(true);
				popupWindow.update();

				final Button playBack = (Button) v.findViewById(R.id.startButton);
				playBack.setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View v) {
								playback(v);//starts playback
							}
						});

				final Button stopPlayBack = (Button) v.findViewById(R.id.stopButton);
				stopPlayBack.setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View v) {
								stopPlayback(v);//stops playback
							}
						});

				final Button delete = (Button) v.findViewById(R.id.deleteButton);
				delete.setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View v) {//closes the popup window so that the user can record a new poem, which will overwrite their last recording
								popupWindow.dismiss();
								startBtn.setVisibility(0);
								stopBtn.setVisibility(4);

							}
						});
			}
		});
	}

	public void start(View view) {//starts recording and changes the mic button to the stop button
		myRecorder = new MediaRecorder();
		myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

		outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/poem.3gpp";

		myRecorder.setOutputFile(outputFile);
		try {
			myRecorder.prepare();
			myRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		startBtn.setVisibility(4);
		stopBtn.setEnabled(true);
		stopBtn.setVisibility(0);
	}

	public void stop(View view) {//stops recording

		try {

			myRecorder.stop();

			myRecorder.release();
			myRecorder = null;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void playback(View view) {//starts playback
		try {

			myPlayer = new MediaPlayer();
			myPlayer.setDataSource(outputFile);
			myPlayer.prepare();
			myPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stopPlayback(View view) {//stops playback
		try {
			if (myPlayer != null) {
				myPlayer.stop();
				myPlayer.release();
				myPlayer = null;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		case R.id.home:
			goToHomePage();
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
