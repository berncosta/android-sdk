package com.df.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.df.utils.IAppConstants;
import com.df.utils.PrefUtil;
import com.dreamfactory.api.DbApi;
import com.dreamfactory.model.Record;
import com.dreamfactory.model.Records;

public class MainActivity extends Activity implements OnItemSelectedListener{
	private Button buttonIncome,buttonExpenses,buttonLogout,buttonAccount;
	private Spinner spinAcc;
	private ProgressDialog progressDialog;
	private String dsp_url;
	private String session_id;
	private TextView tvSaldo, tvReceitas, tvDespesas;
	private Float totalReceita = (float) 0.00;
	private Float totalDespesa = (float) 0.00;
	private Float saldo = (float) 0.00;
	List<String> accs = new ArrayList<String>();

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
		buttonAccount = (Button) findViewById(R.id.btnAcc);
		
		spinAcc = (Spinner) findViewById(R.id.spinnerAcc);
		spinAcc.setOnItemSelectedListener(this);
		
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
		buttonAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				goToAccounts();
			}
		});
		
		GetRecordsTask listItem = new GetRecordsTask();
		listItem.execute();

	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
		String item = (String) parent.getItemAtPosition(pos);
		System.out.println(item);

	}
	
	public void onNothingSelected(AdapterView<?> parent){

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
				totalReceita = (float) 0;
				totalDespesa = (float) 0;
				
				accs.clear();

				for (Record rec:records.getRecord()){
					if (rec.getTipo().equals("r")) {
							totalReceita += Float.parseFloat(rec.getValor());
					} else if (rec.getTipo().equals("d")) {
						totalDespesa += Float.parseFloat(rec.getValor());
					} else if (rec.getTipo().equals("c")) {
						accs.add(rec.getValor());						
					}
				}
				
				ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, accs);
				
				spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				
				spinAcc.setAdapter(spinAdapter);
				
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent ){
		GetRecordsTask listItem = new GetRecordsTask();
		listItem.execute();
	}
	
	private void goToIncome() {

		
		Intent intent = new Intent(getApplicationContext(), IncomeActivity.class);
		startActivityForResult(intent, 0);
		//finish();
		
	}
	
	private void goToExpenses() {

		
		Intent intent = new Intent(getApplicationContext(), ExpensesActivity.class);
		startActivityForResult(intent, 0);
		
	}
	
	private void goToAccounts(){
		Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
		startActivityForResult(intent, 0);
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
