package com.df.adapters;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.df.ui.R;
import com.df.utils.IAppConstants;
import com.df.utils.PrefUtil;
import com.dreamfactory.api.DbApi;
import com.dreamfactory.client.ApiException;
import com.dreamfactory.model.Record;

public class ToDoAdapter extends BaseAdapter { 
	private LayoutInflater inflater;
	private Context context;
	private ToDoHolder holder=  null;
	private ProgressDialog progressDialog;
	private List<Record> records;
	private String session_id;
	private String dsp_url;
	
	public ToDoAdapter(Context context, List<Record> records) {
		this.records = records;
		this.context = context;
		this.session_id = PrefUtil.getString(context, IAppConstants.SESSION_ID, "");
		this.dsp_url =  PrefUtil.getString(context, IAppConstants.DSP_URL, "");
		this.dsp_url += IAppConstants.DSP_URL_SUFIX;
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context.getString((R.string.loading_message)));
		inflater = LayoutInflater.from(this.context); 
	}
	
	public void addTask(Record record){
		records.add(record);
	}

	@Override
	public int getCount() {
		return records.size();
	}
	@Override
	public Object getItem(int position) {
		return records.get(position);
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		

		holder = new ToDoHolder();
		Record record = records.get(position);
		if (convertView == null){
			convertView = inflater.inflate(R.layout.list_item, null);
			holder.iv_icon = (ImageView) convertView.findViewById(R.id.icon_list);
			holder.tv_task = (TextView) convertView.findViewById(R.id.text_view_list);
			convertView.setTag(holder);
		}
		else {
			holder = (ToDoHolder) convertView.getTag();
		}

		//		if (position % 2 ==0){
		//			convertView.setBackgroundResource(R.color.list_bg_1);
		//		}
		//		else {
		//			convertView.setBackgroundResource(R.color.list_bg_2);
		//		}
		holder.iv_icon.setTag( ""+position);

		holder.iv_icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = Integer.parseInt((String)v.getTag());
				new DeleteRecordTask().execute(records.get(pos), Integer.valueOf(pos));
			}
		});
		holder.tv_task.setId(position);
		holder.tv_task.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int pos = v.getId();
				new UpdateRecordTask().execute(records.get(pos));
			}
		});

		holder.tv_task.setText(record.getData(5)+" | "+record.getCategoria(4)+" | "+record.getValor());
//		if (record.iscomplete()){
//			holder.tv_task.setPaintFlags(holder.tv_task.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
//		}
//		else {
//			holder.tv_task.setPaintFlags((holder.tv_task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)));
//		}
		return convertView;
	}	

	private class ToDoHolder{
		ImageView iv_icon;
		TextView tv_task;
	}
	
	class DeleteRecordTask extends AsyncTask<Object, String, String>{

		@Override
		protected String doInBackground(Object... params) {
			Record record = (Record)params[0];
			int pos = (Integer)params[1];
			DbApi dbApi = new DbApi();
			dbApi.setBasePath(dsp_url);
			dbApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.TABLE_NAME);
			dbApi.addHeader("X-DreamFactory-Session-Token", session_id);
			try {
				dbApi.deleteRecord(IAppConstants.TABLE_NAME, record.getId(), null, null, null);
				records.remove(pos);
			} catch (ApiException e) {
				return e.getMessage();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			progressDialog.cancel();
			if(result == null){ // success
				notifyDataSetChanged();
			}else{
				Toast.makeText(context, result, Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}
		
	}
	
	class UpdateRecordTask extends AsyncTask<Object, String, String>{

		@Override
		protected String doInBackground(Object... params) {
			Record record = (Record)params[0];
			//record.setComplete(!record.iscomplete());
			DbApi dbApi = new DbApi();
			dbApi.setBasePath(dsp_url);
			dbApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
			dbApi.addHeader("X-DreamFactory-Session-Token", PrefUtil.getString(context, IAppConstants.SESSION_ID, ""));
			try {
				Record result = dbApi.updateRecord(IAppConstants.TABLE_NAME, record.getId(), null, record, null, null);
				System.out.println("UpdateRecord Result " + result.toString());
			} catch (ApiException e) {
				return e.getMessage();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			progressDialog.cancel();
			if(result == null){ // success
				notifyDataSetChanged();
			}else{
				Toast.makeText(context, result, Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}
		
	}
}
