/*
 * This is the page the user will use to record their poetry and post it to the server
 */

package com.example.bespokenapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

public class RecordPoem extends Activity {

	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String outputFile = null;
	
	private ImageButton startBtn;
	private ImageButton stopBtn;
	private ImageButton playBack;
	private ImageButton pausePlayBack;
	
	private TextView timerValue;
	private TextView timerValue2;
	long timerLength;
	private long startTime = 0L;
	private Handler customHandler = new Handler();
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	int position = 0;
	long poemLength = 0L;
	
	String uniqueUserID;

	String poemNameString = "[No Title]";
	String poemTextString = "[No Text]";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_poem);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		uniqueUserID = getIntent().getExtras().getString("uniqueUser");
		
		timerValue = (TextView) findViewById(R.id.timerValue);
		
		//set the start button(picture of microphone) to start recording when pressed
		startBtn = (ImageButton)findViewById(R.id.micImage);
		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start(v);//starts recording
				//start the timer
				startTime = SystemClock.uptimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);
			}
		});

		//set the stop button to stop recording (and the timer) and open the playback popup
		stopBtn = (ImageButton) findViewById(R.id.stop);
		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stop(v);//stops recording
				
				customHandler.removeCallbacks(updateTimerThread);//stops the timer
				
				//then opens a popup window for playback
				LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
				v = layoutInflater.inflate(R.layout.playback, null);
				final PopupWindow popupWindow = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				popupWindow.showAtLocation(v, Gravity.TOP, 0, 200);
				
				//edit texts for the user to enter the poem name and info
				final EditText poemName = (EditText)v.findViewById(R.id.poemName);
		        final EditText poemText = (EditText)v.findViewById(R.id.poemInfo);

		        //make keyboard appear
				popupWindow.setFocusable(true);
				popupWindow.update();
				poemName.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				
				//get hold of the timer in the playback window
				timerValue2 = (TextView)v.findViewById(R.id.timerValue2);
				
				//get hold of the play and pause buttons
				pausePlayBack = (ImageButton) v.findViewById(R.id.pauseButton);
				playBack = (ImageButton) v.findViewById(R.id.startButton);
				//set play button to start playback
				playBack.setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View v) {
								playback(v);//starts playback
								playBack.setVisibility(8);//hides play button
								pausePlayBack.setVisibility(0);//shows pause button
								customHandler.postDelayed(updateTimerThread2, 0);//starts timer
							}
						});

				//set pause button to pause the playback
				pausePlayBack.setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View v) {
								pausePlayback(v);//pauses playback
								pausePlayBack.setVisibility(8);//hides pause button
								playBack.setVisibility(0);//shows play button
								customHandler.removeCallbacks(updateTimerThread2);//stops timer
							}
						});
				//set the delete button to close the popup window
				final Button delete = (Button) v.findViewById(R.id.deleteButton);
				delete.setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View v) {
								popupWindow.dismiss();//closes the popup window so that the user can record a new poem, which will overwrite their last recording
								//hide keyboard
								InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
								
								startBtn.setVisibility(0);//show start button
								stopBtn.setVisibility(8);//hide stop button
								position=0;//reset the position to 0, so when the user opens the playback window again, the playback will start at the beginning
								timerValue.setText("00:00");//reset timer value
							}
						});
				//set post button to post the user's recording
				final Button postButton = (Button) v.findViewById(R.id.postButton);
				postButton.setOnClickListener(
						new View.OnClickListener() {
														
							@Override
							public void onClick(View v) {
								//hide keyboard
								InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
								
								HttpClient httpClient = new DefaultHttpClient();

								//The purpose of this get request is to get the unique upload url generated by Google's blobstore.
								HttpGet getRequest = new HttpGet("http://bespokenapp.appspot.com/addPoem");
			                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
			                    String upload_url = "";
			                    String the_upload_url = "";
			                    			                    
			                    try {
			                    	//convert the contents of the webpage to a string.
			                    	enableStrictMode();
			                    	HttpResponse getResponse = httpClient.execute(getRequest);
			                    	HttpEntity entity = getResponse.getEntity(); 
				                    InputStream is = entity.getContent(); // Create an InputStream with the response
				                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				                    while ((the_upload_url = reader.readLine()) != null) {
				                    	if (the_upload_url.contains("/_ah/upload")) {
				                    		upload_url = the_upload_url;
				                    	}
				                    }
				                    
				                    is.close(); // Close the stream
				                    
								} catch (ClientProtocolException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
			                    
			                    upload_url = upload_url.trim();
			                    Log.d("upload_url", upload_url);
			                    
			                    //HttpPost postRequest = new HttpPost("http://bespokenapp.appspot.com/testForm");
			                    HttpPost postRequest = new HttpPost(upload_url);
			                    
								try {
									enableStrictMode(); //this is necessary to let us post the request.
									
							        poemNameString = poemName.getText().toString();
							        if (poemNameString == null) {
							        	poemNameString = "[No Title]";
							        }
							        //Editable asdf = poemText.getText();
							        poemTextString = poemText.getText().toString();
							        if (poemTextString == null) {
							        	poemTextString = "[No Text]";
							        }
								    StringBody poem_name = new StringBody(poemNameString);
								    StringBody poem_text = new StringBody(poemTextString);
								    StringBody uniqueUserIDsb = new StringBody(uniqueUserID);
								    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
								    entity.addPart("poem_name", poem_name);
								    entity.addPart("poem_text", poem_text);
								    File myFile = new File(outputFile);
								    FileBody fb = new FileBody(myFile);
								    entity.addPart("unique_user_ID", uniqueUserIDsb); //This is so we can set the poem entity's uniqueUserID field manually.
								    entity.addPart("file", fb);
									postRequest.setEntity(entity);
									HttpResponse response = httpClient.execute(postRequest);
									
									String postResponseOutput = "Null   ";
							        InputStream iStream = response.getEntity().getContent();
							        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
				                    while ((the_upload_url = reader.readLine()) != null) {
				                    	postResponseOutput += the_upload_url;
				                    }
				                    Log.d("uniqueUserID from intent", uniqueUserID);
				                    Log.d("HttpResponse Output", postResponseOutput);
									
								} catch (ClientProtocolException e) {
									// Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// Auto-generated catch block
									e.printStackTrace();
								}
								
								popupWindow.dismiss(); //clse the popup window after the file is posted to the blobstore.		
								goToHomePage(); 
							}
						});
				
			}
		});
	}
	
	//method for updating the timer on the record page - this was based off of a tutorial at http://examples.javacodegeeks.com/android/core/os/handler/android-timer-example/
	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			//get the number of milliseconds since the start button is pushed
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			//convert to minutes and seconds
			int secs = (int) (timeInMilliseconds/1000);
			int mins = secs/60;
			secs = secs %60;
			//display in the timer text view
			timerValue.setText("" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
			customHandler.postDelayed(this, 0);	
		}
	};

	//method for updating the timer in the playback popup
	private Runnable updateTimerThread2 = new Runnable() {
		public void run() {
			
			//the getCurrentPosition() method will return the number of milliseconds the player is from the start of the recording;
			//then convert this to minutes and seconds
			int secs = (int) (myPlayer.getCurrentPosition()/1000);
			int mins = secs/60;
			secs = secs %60;
			//display in the timer text view
			timerValue2.setText("" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
			customHandler.postDelayed(this, 0);	
			//if the player reaches the end of the recording, stop the timer
			if (!myPlayer.isPlaying()) {
				customHandler.removeCallbacks(updateTimerThread2);
				position = 0;//reset position
				pausePlayBack.setVisibility(8);//hide pause button
				playBack.setVisibility(0);//show play button
				timerValue2.setText("00:00");//reset timer
			}

		}

	};

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
		startBtn.setVisibility(8);
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
			myPlayer.seekTo(position);//start from whatever the position is; if the user has paused, the player will pick up where they left off
			myPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void pausePlayback(View view) {//stops playback
		try {
			if (myPlayer != null) {
				myPlayer.stop();
				position = myPlayer.getCurrentPosition();//set position to where ever the user paused
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
		getMenuInflater().inflate(R.menu.record_poem, menu);
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
	
	/* this is a dirty workaround that allows us to make http post requests */
	public void enableStrictMode()
	{
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	 
	    StrictMode.setThreadPolicy(policy);
	}
}
