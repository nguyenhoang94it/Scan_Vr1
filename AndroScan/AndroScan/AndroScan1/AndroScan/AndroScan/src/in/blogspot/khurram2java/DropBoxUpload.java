package in.blogspot.khurram2java;

import java.util.ArrayList;
import java.util.logging.Logger;
import com.dropbox.client2.session.Session.AccessType;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

public class DropBoxUpload extends Activity {

	private LinearLayout container;
	private DropboxAPI dropboxApi;
	private boolean isUserLoggedIn;
	private Button loginButton;
	private Button uploadFileBtn;
	private Button listFileBtn;
	
	private final static String DROPBOX_FILE_DIR = "";
	private final static String DROPBOX_NAME = "dropbox_prefs";
	private final static String ACCESS_KEY = "g1uvgg5yl3iu0i0";
	private final static String ACCESS_SECRET = "1xsuw5tsl20111c";
	private final static AccessType ACCESS_TYPE = AccessType.DROPBOX;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dropbox_sync);
		
		    //Get Intent which call this activity
				Intent callerIntent = getIntent();
		    //Get bundle based on MyPackage
				Bundle packageFromCaller = callerIntent.getBundleExtra("MyPackage");
			//Get path of CSV file
				final String csvFilePath = packageFromCaller.getString("csvFilePath") + ".csv";
				final String csvFileName = packageFromCaller.getString("csvFileName") + ".csv";
				Toast.makeText(this, csvFilePath, Toast.LENGTH_LONG).show();
		
		loginButton = (Button) findViewById(R.id.loginBtn);
		loginButton.setOnClickListener(new OnClickListener() {
	
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isUserLoggedIn){
					dropboxApi.getSession().unlink();
					loggedIn(false);
				} else {
					((AndroidAuthSession) dropboxApi.getSession())
							.startAuthentication(DropBoxUpload.this);
				}
			}
		});
		uploadFileBtn = (Button) findViewById(R.id.uploadFileBtn);
		uploadFileBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//upLoad2Dropbox();
				UploadFile uploadFile = new UploadFile(DropBoxUpload.this, csvFileName, csvFilePath, dropboxApi, DROPBOX_FILE_DIR);
				uploadFile.execute();
			}
		});
		listFileBtn = (Button) findViewById(R.id.listFilesBtn);
		listFileBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ListFiles listFiles = new ListFiles(dropboxApi, DROPBOX_FILE_DIR, handler);
				listFiles.execute();
			}
		});
		container = (LinearLayout) findViewById(R.id.container_files);
		
		loggedIn(false);
		
		AppKeyPair appKeyPair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);
		AndroidAuthSession session;
		
		SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
		String key = prefs.getString(ACCESS_KEY, null);
		String secret = prefs.getString(ACCESS_SECRET, null);
		
		if(key != null && secret != null) {
			AccessTokenPair token = new AccessTokenPair(key, secret);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, token);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}
		
		dropboxApi = new DropboxAPI(session);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		AndroidAuthSession session = (AndroidAuthSession)dropboxApi.getSession();
		if(session.authenticationSuccessful()) {
			try {
				session.finishAuthentication();
				
				TokenPair tokens = session.getAccessTokenPair();
				SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
				Editor editor = prefs.edit();
				editor.putString(ACCESS_KEY, tokens.key);
				editor.putString(ACCESS_SECRET, tokens.secret);
				editor.commit();
				
				loggedIn(true);
			} catch (IllegalStateException e) {
				Toast.makeText(this, "Error during Dropbox auth", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private final Handler handler = new Handler() {
		public void handleMessage(Message message) {
			ArrayList<String> result = message.getData().getStringArrayList("data");
			
			for(String fileName : result) {
				TextView textView = new TextView(DropBoxUpload.this);
				textView.setText(fileName);
				container.addView(textView);
			}
		}
	};
	
	
	public void loggedIn(boolean userLoggedIn) {
		isUserLoggedIn = userLoggedIn;
		uploadFileBtn.setEnabled(userLoggedIn);
		uploadFileBtn.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
		listFileBtn.setEnabled(userLoggedIn);
		listFileBtn.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
		loginButton.setText(userLoggedIn ? "Logout" : "Log in");
	}

}
