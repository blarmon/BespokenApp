/*
 * name: Lauren Naylor
 * This is the page the user will use to record their poetry - right now the post button is not functional
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
	//uniqueUserID = "blah!";
	//Intent testIntent = getIntent();

	String poemNameString = "[No Title]";
	String poemTextString = "[No Text]";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_poem);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		uniqueUserID = getIntent().getExtras().getString("uniqueUser");
		
		timerValue = (TextView) findViewById(R.id.timerValue);
		

		startBtn = (ImageButton)findViewById(R.id.micImage);
		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start(v);//starts recording
				startTime = SystemClock.uptimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);
			}
		});


		stopBtn = (ImageButton) findViewById(R.id.stop);
		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stop(v);//stops recording
				
				customHandler.removeCallbacks(updateTimerThread);
				timerLength = timeInMilliseconds;
				
				
				//then opens a popup window for playback
				LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
				v = layoutInflater.inflate(R.layout.playback, null);
				final PopupWindow popupWindow = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				popupWindow.showAtLocation(v, Gravity.TOP, 0, 200);
				
				final EditText poemName = (EditText)v.findViewById(R.id.poemName);
		        final EditText poemText = (EditText)v.findViewById(R.id.poemInfo);

				popupWindow.setFocusable(true);
				popupWindow.update();
				poemName.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				
				timerValue2 = (TextView)v.findViewById(R.id.timerValue2);
				
				
				pausePlayBack = (ImageButton) v.findViewById(R.id.pauseButton);
				playBack = (ImageButton) v.findViewById(R.id.startButton);
				playBack.setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View v) {
								playback(v);//starts playback
								playBack.setVisibility(8);
								pausePlayBack.setVisibility(0);
								customHandler.postDelayed(updateTimerThread2, 0);
							}
						});

				
				pausePlayBack.setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View v) {
								stopPlayback(v);//stops playback
								pausePlayBack.setVisibility(8);
								playBack.setVisibility(0);
								//timeSwapBuff += timeInMilliseconds;
								customHandler.removeCallbacks(updateTimerThread2);
							}
						});

				final Button delete = (Button) v.findViewById(R.id.deleteButton);
				delete.setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View v) {//closes the popup window so that the user can record a new poem, which will overwrite their last recording
								popupWindow.dismiss();
								InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
								startBtn.setVisibility(0);
								stopBtn.setVisibility(8);
								position=0;
								timerValue.setText("00:00");
							}
						});
				
				final Button postButton = (Button) v.findViewById(R.id.postButton);
				postButton.setOnClickListener(
						new View.OnClickListener() {
														
							@Override
							public void onClick(View v) {
								
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
									
								    /*MultipartEntityBuilder builder = MultipartEntityBuilder.create();
								    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
								    //builder.addPart("file", new FileBody(new File(fileName)));
								    builder.addTextBody("poem_name", "Testing poem_name from Android", ContentType.MULTIPART_FORM_DATA);
								    builder.addTextBody("poem_text", "blah", ContentType.MULTIPART_FORM_DATA);
								    //builder.addTextBody("nickname", "test from android", ContentType.MULTIPART_FORM_DATA);
								    //postRequest.setEntity(builder.build());
								    HttpResponse response = httpClient.execute(postRequest);
									//HttpEntity theEntity = response.getEntity();
								    //theEntity.consumeContent();
								    
								    String postResponseOutput = "Null   ";
							        InputStream iStream = response.getEntity().getContent();
							        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
				                    while ((the_upload_url = reader.readLine()) != null) {
				                    	postResponseOutput += the_upload_url;
				                    }
				                    Log.d("HttpResponse Output", postResponseOutput);
				                    
				                    httpClient.getConnectionManager().shutdown();*/
								    
								    /* List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
							        nameValuePairs.add(new BasicNameValuePair("nickname", "test with NameValuePair"));
							        postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							        HttpResponse response = httpClient.execute(postRequest);
							        Log.d("upload_url_new_place", upload_url);
							        
							        String postResponseOutput = "Null   ";
							        InputStream iStream = response.getEntity().getContent();
							        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
				                    while ((the_upload_url = reader.readLine()) != null) {
				                    	postResponseOutput += the_upload_url;
				                    }
				                    Log.d("HttpResponse Output", postResponseOutput); */
									
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
	
	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			

			int secs = (int) (timeInMilliseconds/1000);
			int mins = secs/60;
			secs = secs %60;

			timerValue.setText("" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
			customHandler.postDelayed(this, 0);	
		}
	};

	private Runnable updateTimerThread2 = new Runnable() {
		public void run() {

			int secs = (int) (myPlayer.getCurrentPosition()/1000);
			int mins = secs/60;
			secs = secs %60;

			timerValue2.setText("" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
			customHandler.postDelayed(this, 0);	
			if (!myPlayer.isPlaying()) {
				customHandler.removeCallbacks(updateTimerThread2);
				position = 0;
				pausePlayBack.setVisibility(8);
				playBack.setVisibility(0);
				timerValue2.setText("00:00");
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
			myPlayer.seekTo(position);
			myPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stopPlayback(View view) {//stops playback
		try {
			if (myPlayer != null) {
				myPlayer.stop();
				position = myPlayer.getCurrentPosition();
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
