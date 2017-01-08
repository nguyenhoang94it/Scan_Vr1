package in.blogspot.khurram2java;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
public class NoteActivity extends Activity {

	EditText edtNote;
	Button btUpdate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_layout);
		
		edtNote = (EditText) findViewById(R.id.edtNote);
		btUpdate = (Button) findViewById(R.id.btUpdate);
		
		//Get Intent which call this activity
		Intent callerIntentClick = getIntent();
		//Get bundle based on MyPackage
		Bundle packageFromCaller = callerIntentClick.getBundleExtra("PackageClick");
		//Get path of CSV file
		final String clickedCode = packageFromCaller.getString("clickedCode");
		final String clickedNote = packageFromCaller.getString("clickedNote");
		//Toast.makeText(this, clickedCode + " " + clickedNote, Toast.LENGTH_LONG).show();
		
		edtNote.setText(clickedNote);
		
		btUpdate.setOnClickListener(new OnClickListener() {
		
			
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			send2Main(MainActivity.RESULT_CODE_UPDATE);
			
			}

			private void send2Main(int resultCode) {
				// TODO Auto-generated method stub
				Intent intent = getIntent();
				 String noteUpdated = edtNote.getText().toString();
				 intent.putExtra("noteUpdated", noteUpdated);
				 intent.putExtra("clickedCode_return", clickedCode);
				 //intent.putExtra("clickNote_return", clickedNote);
				 setResult(resultCode, intent);
				 finish();
			}
		});
		
		
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		//return super.onCreateOptionsMenu(menu);
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}
	
}
