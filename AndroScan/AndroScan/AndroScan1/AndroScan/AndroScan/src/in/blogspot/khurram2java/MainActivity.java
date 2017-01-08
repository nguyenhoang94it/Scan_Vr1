package in.blogspot.khurram2java;

import in.blogspot.khurram2java.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.dropbox.client2.session.Session.AccessType;
import com.google.zxing.client.android.Intents.Scan;
import com.opencsv.CSVWriter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.ExportedProperty;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int REQUEST_CODE_INPUT = 113;
	public static final int RESULT_CODE_UPDATE = 115;
	final String FILE_NAME = "dbScanner";
	final String DB_FILE_NAME = FILE_NAME + ".db";
	final String TABLE_NAME = "tblAndroScan";
	final String clickedNote = null;
	final String clickedCode = null;
	File exportDir = null;
	File exportDirCSV = null;
	private ScannedItems dataLongClick = null;
	private int posLongClick = -1;
	String CSV_FILE_NAME = null;
	EditText edtFileName;
	TextView tvStatus;
	//TextView tvResult;
	TextView tvFileName;
	ListView lvHistory;
	SQLiteDatabase sqlHistory = null;
	String currentCode = null;
	String noteReceived = null;
	ArrayList<ScannedItems> arrCode = new ArrayList<ScannedItems>();
	TwoLineAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		edtFileName = (EditText) findViewById(R.id.edtFileName);
		tvFileName = (TextView) findViewById(R.id.tvFileName);
		lvHistory = (ListView) findViewById(R.id.lvHistory);
		
		Button scanBtn = (Button) findViewById(R.id.btnScan);
		Button btExport2CSV = (Button) findViewById(R.id.btExportCSV);
		Button btEnter = (Button) findViewById(R.id.btEnter);
		Button btExport = (Button) findViewById(R.id.btExportCSV);
		
		Button btSync = (Button) findViewById(R.id.btSync);
		
		registerForContextMenu(btSync);
		
		
//		btSync.setOnClickListener(new OnClickListener() {
//
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				openDropBoxUpload();
//			}
//
//		});
		
		btSync.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openContextMenuButton(v);
				
			}

			
		});
		
		CSV_FILE_NAME = "TestScanner"; //Default CSV file name
					
		displayToastLong("Author: Nguyen Trung Kien" + "\n" + "Email: kiennt4@mobifone.vn)" + "\n"
														+ "Mobile: 0903561122");
		
//Create sqlHistory
				
		doCreatDB();
		if (sqlHistory != null)
		{
			doCreateTbl();
		}
		
		//resetID();
		arrCode = selectAll();
		displayAll();
		tvFileName.setText("File: sdcard/ScannedCode/TestScanner.csv" );
		
		//registerForContextMenu(lvHistory);
		
		
//Create new file (new table in database):
		btEnter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edtFileName.getText().toString() == null)
				{
				  CSV_FILE_NAME = "TestScanner";
				} else
					{
						CSV_FILE_NAME = edtFileName.getText().toString();
					}
				tvFileName.setText("File: sdcard/ScannedCode/" + CSV_FILE_NAME + ".csv " );
				edtFileName.setText("");
				//sqlHistory.delete(TABLE_NAME, null, null);
				arrCode = selectAll();
				displayAll();
				//Hide keyboard after pressing Enter
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				mgr.hideSoftInputFromWindow(edtFileName.getWindowToken(),0); 
			}
			
			
		}
		);
		
//Write to SD card
		try {
			writeToSD();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		
		
		
//Gerenate CSV file
		btExport2CSV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Write to SD card
				try {
					writeToSD();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				File dbFile = getDatabasePath(DB_FILE_NAME);

				//It retrieves the root path of the directory.

					exportDir = new File(Environment.getExternalStorageDirectory().getPath() + "/ScannedCode", "");

					if (!exportDir.exists())

						{

						exportDir.mkdirs();

						}

					File file = new File(exportDir, CSV_FILE_NAME +".csv");

					try

						{

						file.createNewFile();
						SQLiteOpenHelper dbHelper;
						CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

						//SQLiteDatabase db = dbHelper.getReadableDatabase();

						Cursor curCSV = sqlHistory.rawQuery("SELECT * FROM " + TABLE_NAME , null);

						csvWrite.writeNext(curCSV.getColumnNames());

					while (curCSV.moveToNext()) {

						String arrStr[] = { 
						curCSV.getString(0),
						curCSV.getString(1),
						curCSV.getString(2),
						curCSV.getString(3)
						};

						csvWrite.writeNext(arrStr);

						}
					csvWrite.close();

					curCSV.close();
						} catch (SQLException sqlEx) {
							Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
							} catch (IOException e) {
								Log.e("MainActivity", e.getMessage(), e);
							}

					Toast.makeText(getApplicationContext(), "Exported",
							Toast.LENGTH_SHORT).show();
					// Delete table then reset ID to 1 by deleting my table name in table "sequence_sqlite"
					sqlHistory.delete(TABLE_NAME, null, null);
					sqlHistory.delete("sqlite_sequence", "name=?", new String[] {TABLE_NAME});
					
			}
		});
		
		
		 
		 
	///////////////////
		 
		 
//Start scan, add scanned code to database and display 
		scanBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				try {
					
					Intent intent = new Intent(
							"com.google.zxing.client.android.SCAN");
					intent.putExtra("SAVE_HISTORY", true);
					//displayToast("Start scan");
					intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A,QR_CODE");
					startActivityForResult(intent, 0);	
					//Write to SD card
					try {
						writeToSD();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "ERROR:" + e, 1).show();

				}

			}
		});
		
// Long click to historical code
	     lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//		 @Override
		 public void onItemClick(AdapterView<?> arg0, View arg1,
		 int arg2, long arg3) {
		 //final ArrayList<String> arrLongClick = new ArrayList<String>();
		 //dataLongClick = arrCode.get(arg2).getCode(); 
		 //lvHistory.showContextMenuForChild(arg1);
		 dataLongClick = arrCode.get(arg2);
		 posLongClick = arg2;
		 displayToastLong("Code: " + dataLongClick.getCode() + "\n" + "Note: " 
				 			   + dataLongClick.getNote());
		 onItemClickEvent(lvHistory);
		 //return false;
		} 
		 
	});

}
	    

//-----------------------------------------------------------------------


	//Create database
	public SQLiteDatabase doCreatDB() {
		// TODO Auto-generated method stub
		try {
			sqlHistory = openOrCreateDatabase(DB_FILE_NAME, MODE_PRIVATE, null);
			//sqlHistory = openOrCreateDatabase(DB_FILE_NAME, MODE_APPEND, null);
			//displayToast("Create " + FILE_NAME + " success!");
		}catch (SQLiteException e){
//			displayToast(e.getMessage());
		}
		return sqlHistory;
	}

// Create Table	
	public void doCreateTbl() {
		// TODO Auto-generated method stub
		try {
			String tblCreationString = "create table " + TABLE_NAME + "("
					+"id integer PRIMARY KEY autoincrement, "
					+ "code text, "
					+ "time text, "
					+ "note text);";
			sqlHistory.execSQL(tblCreationString);
			//displayToast("Create table " + TABLE_NAME + " success!");
		} 
		catch (SQLException e){
			//displayToast(e.getMessage());
		}
	}
	
// Show toast
	private void displayToast(String message) {
		// TODO Auto-generated method stub
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	private void displayToastLong(String message_long) {
		// TODO Auto-generated method stub
		Toast toast = Toast.makeText(this, message_long, Toast.LENGTH_LONG);
		toast.show();
	}
	
//Write to SD card
	
	private void writeToSD() throws IOException {
		File sd = Environment.getExternalStorageDirectory();
		String stateSDCard = Environment.getExternalStorageState();
		//displayToast("SD Card state is " + stateSDCard);
	    File currentDB = getApplicationContext().getDatabasePath(DB_FILE_NAME); 
	    if (sd.canWrite()) {
	        File backupDB = new File(sd, "backupScannedItems.db"); 
	        if (currentDB.exists()) {
	        	//displayToast("OK, db file is exist!");
	            FileChannel src = new FileInputStream(currentDB).getChannel();
	            FileChannel dst = new FileOutputStream(backupDB).getChannel();
	            dst.transferFrom(src, 0, src.size());
	            src.close();
	            dst.close();
	        } 
	    } 
	    //displayToast("Copying to SD card is OK!");
	} 

//Add
	
	private void doAdd(String code, String note) {
		// TODO Auto-generated method stub
		try {			
			ContentValues values = new ContentValues();
			values.put("code", code);
			values.put("note", note);
			//Add timestamp
			values.put("time",new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new Date()));
			
			int insertResult = (int) sqlHistory.insertOrThrow(TABLE_NAME, null, values);
			
			if(insertResult != -1){
				displayToast("Insert [" + code + "] successfully!");
			}else{
				displayToast("Insert failed!");
			}
//			if(adapter!=null){
//				adapter.addAll(code);	
//			}
		} catch (SQLiteException e) {
			displayToast(e.getMessage());
		}
		
	
	}


	// Select all
	
	private ArrayList<ScannedItems> selectAll() {
		// TODO Auto-generated method stub
		ArrayList<ScannedItems> arrCode_temp = new ArrayList<ScannedItems>();
		Cursor cursor = sqlHistory.query(TABLE_NAME
				, null, null, null, null, null, null);
		String[] columns = {"id", "code", "time", "note"};
		int colId = cursor.getColumnIndex(columns[0]);
		int colCode = cursor.getColumnIndex(columns[1]);
		int colTime = cursor.getColumnIndex(columns[2]);
		int colNote = cursor.getColumnIndex(columns[3]);
		//Bring cursor to the first row
		cursor.moveToLast();
		//Select row-by-row till the end
		while(!cursor.isBeforeFirst())
		{
			arrCode_temp.add(new ScannedItems(Integer.parseInt(cursor.getString(colId)), 
					cursor.getString(colCode), cursor.getString(colTime), cursor.getString(colNote)));
			cursor.moveToPrevious();
		}
		cursor.close();
		return arrCode_temp;
	}
	
	
//In the same activity you’ll need the following to retrieve the results:
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {

			if (resultCode == RESULT_OK) {
				currentCode = intent.getStringExtra("SCAN_RESULT");
				doAdd(currentCode,"");
				displayToastLong("Format of this code is: " + intent.getStringExtra("SCAN_RESULT_FORMAT"));
				arrCode = selectAll();
				displayAll();
								
			} 
			else if (resultCode == RESULT_CANCELED) {
				displayToast("Cancelled!");
				//For test
				doAdd("123456789","Test Note");
				arrCode = selectAll();
				displayAll();
			}
		} else if (requestCode == REQUEST_CODE_INPUT){
			if (resultCode == RESULT_CODE_UPDATE){
				noteReceived = intent.getStringExtra("noteUpdated");
				String clickedCode_received = intent.getStringExtra("clickedCode_return");
			    updateNote(clickedCode_received, noteReceived);
				arrCode = selectAll();
				displayAll();
			} else
				{
				displayToast("Updated nothing");
				}
		}
		
	}
	
// Display History
//	@SuppressLint("NewApi")
	private void displayAll() {
		// TODO Auto-generated method stub
		
//		ArrayList<ScannedItems> arrCode = new ArrayList<ScannedItems>();
		//arrCode = selectAll();
		int resource = 0;
		adapter = new TwoLineAdapter (MainActivity.this, resource, arrCode);
		lvHistory.setAdapter(adapter);
//		adapter.addAll(arrCode);
		//displayToast("Display History OK");
		}
// Open DropBoxUpload	    			
		private void openDropBoxUpload() {
			// TODO Auto-generated method stub
			Intent myIntent=new Intent(MainActivity.this, DropBoxUpload.class);
			 
			// Create a Bundle to bring path of CSV file to DropBoxUpload
				Bundle myBundle = new Bundle();
			//Get path of created CSV file 
				File sdPath = Environment.getExternalStorageDirectory();
				String filePath = sdPath.getAbsolutePath() + "/ScannedCode/" + CSV_FILE_NAME;
			//Put this path into bundle
				myBundle.putString("csvFilePath",filePath);
				myBundle.putString("csvFileName",CSV_FILE_NAME);
			//Put bundle into Intent
				myIntent.putExtra("MyPackage", myBundle);
			//Start activity "dropbox_sync"
				startActivity(myIntent);
		}

		
		// Update Note by ID	
		public void updateNote(String this_code, String new_note) {	
			  ContentValues values = new ContentValues();
			  values.put("note", new_note);
	          int resultValue = sqlHistory.update(TABLE_NAME, values,"code=?", new String[]{this_code});
	          if(resultValue == 0) {
	        	  displayToast("Update note failed");
	          } else {
	        	  displayToast("Updated");
	          }
			 }
//Menu of main activity		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			getMenuInflater().inflate(R.menu.activity_main, menu);
			 return true;
		}
	
		
//Menu of item in listview
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// TODO Auto-generated method stub
			super.onCreateContextMenu(menu, v, menuInfo);
//			getMenuInflater().inflate(R.menu.menu_item_layout, menu);
			
			switch (v.getId()) {
	          case R.id.btSync:
//	             menu.setHeaderTitle(getString(R.string.option1));  
//	             menu.add(0, v.getId(), 0, getString(R.string.option2));   
//	             menu.add(0, v.getId(), 0, getString(R.string.options3)); 
	        	  getMenuInflater().inflate(R.menu.menu_send_file, menu); 
	           break; 
	          case R.id.lvHistory:
	        	  getMenuInflater().inflate(R.menu.menu_item_layout, menu);
	           break; 
			}
		}
		
//
		@Override
		 public boolean onContextItemSelected(MenuItem item) {
		 switch(item.getItemId())
		 {
		 case R.id.item_edit_note:
			 doUpdateNote();
		 break;
		 case R.id.item_delete_code:
			 doDeleteCode();
		 break;
		 case R.id.item_google_search:
			 doGoogleSearch();
		 break;
		 case R.id.itemDropbox:
			 openDropBoxUpload();
		 break;
		 case R.id.itemMail:
			 sendMail();
		 break;
		 }
		 return super.onContextItemSelected(item);
		 }



private void sendMail() {
	// TODO Auto-generated method stub
	Intent i = new Intent(Intent.ACTION_SEND);
	File sdPath = Environment.getExternalStorageDirectory();
	String filePath = sdPath.getAbsolutePath() + "/ScannedCode/" + CSV_FILE_NAME;
	exportDirCSV = new File(Environment.getExternalStorageDirectory().getPath() + "/ScannedCode", CSV_FILE_NAME + ".csv");
	//i.setType("message/rfc822");
	i.setType("text/plain"); //refer to: http://stackoverflow.com/questions/13065838/what-are-the-possible-intent-types-for-intent-settypetype
	//i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
	i.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
	//i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
	i.putExtra(Intent.EXTRA_SUBJECT, CSV_FILE_NAME + ".csv");
	i.putExtra(Intent.EXTRA_TEXT, "");
	i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(exportDirCSV));
	//displayToastLong(message_long);
	try { 
	    startActivity(Intent.createChooser(i, "Send via..."));
	} catch (android.content.ActivityNotFoundException ex) {
		displayToastLong("There are no email clients installed.");
	}
}


private void doGoogleSearch() {
	// TODO Auto-generated method stub
//	Uri uri = Uri.parse("http://www.google.com/#q=" + dataLongClick.getCode());
//	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//	startActivity(intent);
	Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
	intent.putExtra(SearchManager.QUERY, dataLongClick.getCode()); // query contains search string
	startActivity(intent);
}



private void doUpdateNote() {
			// TODO Auto-generated method stub
	final String clickedCode = arrCode.get(posLongClick).getCode(); 
	final String clickedNote = arrCode.get(posLongClick).getNote();
	//posClick = position;
	//Open activity with REQUEST_CODE_INPUT
	Intent intentNote = new Intent(MainActivity.this, NoteActivity.class);
	
	// Create a Bundle to bring path of CSV file to DropBoxUpload
	Bundle bundleClick = new Bundle();
	//Put this path into bundle
	bundleClick.putString("clickedCode",clickedCode);
	bundleClick.putString("clickedNote",clickedNote);
	//Put bundle into Intent
	intentNote.putExtra("PackageClick", bundleClick);
	
	//Call startActivityForResult
	startActivityForResult(intentNote, REQUEST_CODE_INPUT);
	//Write to SD card
	try {
		writeToSD();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
		}



private void doDeleteCode() {
	// TODO Auto-generated method stub
//	Toast.makeText(MainActivity.this, "This code is: [" + dataLongClick.toString() + 
//	           "]", Toast.LENGTH_LONG).show();
AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
b.setTitle("Remove");
b.setMessage(" Delete this code?");
b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	 
	 @Override
	  public void onClick(DialogInterface dialog, int which) {
	  // TODO Auto-generated method stub
	  
	  int n = sqlHistory.delete(TABLE_NAME, "code=?", 
			  new String[]{dataLongClick.getCode()});
	  
	  if(n>0)
	  {
		  displayToast("Remove " + String.valueOf(n) + " item(s) OK");
	  arrCode.remove(posLongClick);
	  arrCode = selectAll();
	  displayAll();
	  
	//Write to SD card
		try {
			writeToSD();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	  }
	  else
	  {
		  displayToast("Remove NOK, n= " + String.valueOf(n));
	  }
	  }
	  });
	  b.setNegativeButton("No", new DialogInterface.OnClickListener() {
	  
	 @Override
	  public void onClick(DialogInterface dialog, int which) {
	  // TODO Auto-generated method stub
	  dialog.cancel();
	  }
	  });
	  b.show();
}
	
//Click item to show context menu
	public void onItemClickEvent(ListView myListview){
		 registerForContextMenu(myListview);
		 openContextMenu(myListview);
		 unregisterForContextMenu(myListview);
	}
	
	private void openContextMenuButton(View v) {
		// TODO Auto-generated method stub
		openContextMenu(v);
	}
	
 } 
	
