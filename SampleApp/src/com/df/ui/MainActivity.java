package com.df.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.df.utils.IAppConstants;
import com.df.utils.PrefUtil;
import com.dreamfactory.api.DbApi;
import com.dreamfactory.model.Record;
import com.dreamfactory.model.Records;

public class MainActivity extends Activity {
	private Button buttonIncome,buttonExpenses,buttonLogout;
	private ProgressDialog progressDialog;
	private String dsp_url;
	private String session_id;
	private TextView tvSaldo, tvReceitas, tvDespesas;
	private Float totalReceita = (float) 0.00;
	private Float totalDespesa = (float) 0.00;
	private Float saldo = (float) 0.00;

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
		
		tvSaldo = (TextView) findViewById(R.id.textViewSaldo);
		tvReceitas = (TextView) findViewById(R.id.textViewReceitas);
		tvDespesas = (TextView) findViewById(R.id.textViewDespesas);
		
		
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
		
		GetRecordsTask listItem = new GetRecordsTask();
		listItem.execute();

	}
	
	public void onRestart(Bundle savedInstanceState){
		GetRecordsTask listItem = new GetRecordsTask();
		listItem.execute();
	}
	
	public void onResume(Bundle savedInstanceState){
		GetRecordsTask listItem = new GetRecordsTask();
		listItem.execute();
	}
	
	class GetRecordsTask extends AsyncTask<Void, Records, Records>{
		private String errorMsg;

		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected Records doInBackground(Void... params) {
			DbApi dbApi = new DbApi();
			dbApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
			dbApi.addHeader("X-DreamFactory-Session-Token", session_id);
			dbApi.setBasePath(dsp_url);
			try {
				Records records = dbApi.getRecords(IAppConstants.TABLE_NAME, null, null, null, null, null, "tipo%2C%20valor", null, true, null, null);
				log(records.toString());
				return records;
			} catch (Exception e) {
				e.printStackTrace();
				errorMsg = e.getMessage();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Records records) {
			if(progressDialog != null && progressDialog.isShowing()){
				progressDialog.cancel();
			}
			if(records != null){ // success
				
				//soma os valores das receitas e coloca na tela

				for (Record rec:records.getRecord()){
					if (rec.getTipo().equals("r")) {
							totalReceita += Float.parseFloat(rec.getValor());
					} else {
						totalDespesa += Float.parseFloat(rec.getValor());
					}
				}
				
				tvReceitas.setText(totalReceita.toString());
				tvDespesas.setText("-"+totalDespesa.toString());
		
				saldo = totalReceita - totalDespesa;
				
				tvSaldo.setText(saldo.toString());
				
				
			}else{ // some error show dialog
				Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
				logout();
			}
		}
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


	private void log(String message){
		System.out.println("log: " + message);
	}
}
