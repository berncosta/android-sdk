package com.df.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.df.adapters.ToDoAdapter;
import com.df.utils.IAppConstants;
import com.df.utils.PrefUtil;
import com.dreamfactory.api.DbApi;
import com.dreamfactory.model.Record;
import com.dreamfactory.model.Records;

public class IncomeActivity extends Activity {
	private static Button buttonAdd, buttonDate;
	private EditText editTextAddTask;
	private Spinner spinnerCategoria;
	private ListView list_view;
	private ProgressDialog progressDialog;
	private ToDoAdapter adapter = null;
	private String dsp_url;
	private String session_id;
	private TextView totalIncome;
	Float total = (float) 0.00;
	private static DatePickerFragment df = new DatePickerFragment();
	private String accSel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			//ActionBar actionbar = getActionBar();
			//actionbar.setTitle("");
			//actionbar.setIcon(R.drawable.df_logo_txt);
		}catch(Exception e){
		}
		
		Intent intent = getIntent();
		accSel = intent.getStringExtra("ACC");

		dsp_url = PrefUtil.getString(getApplicationContext(), IAppConstants.DSP_URL);
		dsp_url += IAppConstants.DSP_URL_SUFIX;
		session_id = PrefUtil.getString(getApplicationContext(), IAppConstants.SESSION_ID);

		setContentView(R.layout.activity_income);
		totalIncome = (TextView) findViewById(R.id.textView_tital);
		progressDialog = new ProgressDialog(IncomeActivity.this);
		progressDialog.setMessage(getString((R.string.loading_message)));
		list_view = (ListView) findViewById(R.id.list_view_strik_text);
		editTextAddTask = (EditText) findViewById(R.id.editText_add_task);
		spinnerCategoria = (Spinner) findViewById(R.id.spinnerCat);
		buttonAdd = (Button) findViewById(R.id.btnAdd);
		buttonAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String valor = editTextAddTask.getText().toString();
				String data = buttonDate.getText().toString();
				String cat = spinnerCategoria.getSelectedItem().toString();
				if(valor.length()==0 || data.length()==0 || data.length()<5 || cat.length()==0){
					Toast.makeText(IncomeActivity.this, getText(R.string.task_blank), Toast.LENGTH_SHORT).show();
				}
				else {
					CreateRecordTask addToDoItem = new CreateRecordTask();
					addToDoItem.execute(valor, data, cat);
				}
			}
		});
		buttonDate = (Button) findViewById(R.id.btnDate);
		buttonDate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				df.show(getFragmentManager(),"datePicker");
			
			}
		});
		GetRecordsTask listItem = new GetRecordsTask();
		listItem.execute();
	}
	
	public void onBackPressed(){
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		setResult(RESULT_CANCELED, intent);
		finish();
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
			DbApi dbApi = new DbApi();
			dbApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
			dbApi.addHeader("X-DreamFactory-Session-Token", session_id);
			dbApi.setBasePath(dsp_url);
			try {
				String filter = "tipo='r'%20AND%20cart1='"+accSel+"'";
				Records records = dbApi.getRecords(IAppConstants.TABLE_NAME, null, filter, null, null, "data%20DESC", null, null, true, null, null);
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
				adapter = new ToDoAdapter(IncomeActivity.this, records.getRecord());
				list_view.setAdapter(adapter);
				
				//soma os valores das receitas e coloca na tela

				for (Record rec:records.getRecord()){
					total += Float.parseFloat(rec.getValor());
				}
				
				totalIncome.setText("Total Receita: "+total.toString());
				
				
			}else{ // some error show dialog
				Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
				logout();
			}
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
			String valor = params[0];
			String data = params[1]; //no formato do app
			String cat = params[2];
			Record record = new Record();
			record.setValor(valor);
			record.setTipo("r"); //receita
			record.setCart1(accSel);
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

			Date pData = new Date(0);
			try {
				pData = sdf1.parse(data);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			record.setData(pData); //no formato do DB
			record.setCategoria(cat);

			DbApi dbApi = new DbApi();
			dbApi.setBasePath(dsp_url);
			dbApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
			dbApi.addHeader("X-DreamFactory-Session-Token", session_id);
			try {
				String id = ""+System.currentTimeMillis();
				Record resultRecord = dbApi.createRecord(IAppConstants.TABLE_NAME, id, null, record, null, null);
				resultRecord.setValor(valor);
				resultRecord.setData(pData);
				resultRecord.setCategoria(cat);
				log(resultRecord.toString());
				return resultRecord;
			} catch (Exception e) {
				e.printStackTrace();
				errorMsg = e.getMessage();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Record record) {	
			progressDialog.cancel();
			if(record != null){
				adapter.addTask(record);
				adapter.notifyDataSetChanged();
				editTextAddTask.setText("");
				
				total += Float.parseFloat(record.getValor());
				totalIncome.setText("Total Receita: "+total.toString());
			}else {				
				Toast.makeText(IncomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void log(String message){
		System.out.println("log: " + message);
	}
	
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
		
		private Calendar cal = Calendar.getInstance();
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState){
			final Calendar c = 	Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			view.updateDate(year,month,day);
			cal.set(year,month,day);
			changeDateButton();
	
		}

		public Calendar getCalendar(){
			return cal;
		}
	}
	
	public static void changeDateButton() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		buttonDate.setText(sdf.format(df.getCalendar().getTime()));
		
	}
	
}


