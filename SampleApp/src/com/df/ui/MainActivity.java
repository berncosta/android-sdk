package com.df.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.df.ui.R;
import com.df.adapters.ToDoAdapter;
import com.df.utils.IAppConstants;
import com.df.utils.PrefUtil;
import com.dreamfactory.model.Record;
import com.dreamfactory.model.Records;

public class MainActivity extends Activity {
	private Button buttonIncome,buttonExpenses,buttonLogout;
	private EditText editTextAddTask;
	private ListView list_view;
	private ProgressDialog progressDialog;
	private ToDoAdapter adapter = null;
	private String dsp_url;
	private String session_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			//ActionBar actionbar = getActionBar();
			//actionbar.setTitle("");
			//actionbar.setIcon(R.drawable.df_logo_txt);
		}catch(Exception e){
		}

		dsp_url = PrefUtil.getString(getApplicationContext(), IAppConstants.DSP_URL);
		dsp_url += IAppConstants.DSP_URL_SUFIX;
		session_id = PrefUtil.getString(getApplicationContext(), IAppConstants.SESSION_ID);

		setContentView(R.layout.activity_main);
		progressDialog = new ProgressDialog(MainActivity.this);
		progressDialog.setMessage(getString((R.string.loading_message)));


		buttonIncome = (Button) findViewById(R.id.btnIncome);
		buttonExpenses = (Button) findViewById(R.id.btnExpenses);
		buttonLogout = (Button) findViewById(R.id.btnLogout);
		
		buttonIncome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToIncome();
			}

			
		});
		buttonExpenses.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToExpenses();
			}

			
		});
		buttonLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logout();
			}
		});

	}
	
	private void goToIncome() {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(getApplicationContext(), IncomeActivity.class);
		startActivity(intent);
		//finish();
		
	}
	
	private void goToExpenses() {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(getApplicationContext(), ExpensesActivity.class);
		startActivity(intent);
		
	}


	private void logout(){
		PrefUtil.putString(getApplicationContext(), IAppConstants.SESSION_ID, "");
		PrefUtil.putString(getApplicationContext(), IAppConstants.EMAIL, "");
		PrefUtil.putString(getApplicationContext(), IAppConstants.PWD, "");
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("EXIT", true);
		startActivity(intent);
		finish();
	}

	class GetRecordsTask extends AsyncTask<Void, Records, Records>{
		private String errorMsg;

		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected Records doInBackground(Void... params) {
			
			return null;
		}
		@Override
		protected void onPostExecute(Records records) {
			
		}
	}

	class CreateRecordTask extends AsyncTask<String, Void, Record>{
		private String errorMsg;

		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected Record doInBackground(String... params) {
			return null;
		}
		@Override
		protected void onPostExecute(Record record) {	
			//null
		}
	}

	private void log(String message){
		System.out.println("log: " + message);
	}
}
