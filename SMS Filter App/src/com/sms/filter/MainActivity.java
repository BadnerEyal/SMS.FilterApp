package com.sms.filter;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

/**
 * MainActivity
 * @author Gabriel@proto-mech.com
 *
 *�������� ������ ����� ���� ������ ����
 *���� ����� ����� ������� ���� 
 *��� ����� �� ��� ���� �� ��� ���� ��� ������ ������ �� ��������
 *�������� ����� ���� ����� �� ���� ����� ���� �������� ����� ����� ��� �����
 *���� �������� ����� ����� ������� ����� �� �� ���� ����� ����� ������� ������ 
 *��� ������ �� ����� ������
 *���� �������� ����� 
 *��� ���� ������ �������� ���� �� ��� ����� ����� 
 *����� ��� ����� ��� ������ �� ���� ����� ��� ���� ����� �� ������
 *��� ���� �� ������ ������ ���� ���
 * 
 * BroadcastReceiver 2
 * ��� ������ ������ ������� ������� ���� ���� ������ ������
 * ���� ����� ���� ���� �� ��������� ����� ������
 *onResume
 *������ ����� �onStop()
 */
public class MainActivity extends Activity {

	private ListView messagesListView = null;//����� ����� ������
	private ProgressBar progressBar = null;//��� �����
	private ImageView logoImageView = null;// �����
	private LinearLayout mainLinearLayout = null;//����� �����
	private InnerSMSReciever smsRceiver = null; 
	private DBHandler helper = null;// ����� ���� �������
	private MessagesCursorAdapter adapter = null;//���� ��� ���� ������� ������
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		logoImageView = (ImageView)findViewById(R.id.logoImageView);
		mainLinearLayout = (LinearLayout)findViewById(R.id.mainLinearLayout);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		smsRceiver = new InnerSMSReciever(); 
		messagesListView = (ListView)findViewById(R.id.messagesListView); 
		
		//����� ���� ������� ����� �������
		helper = new DBHandler(this);
		Cursor cursor = helper.getAllMessages(true);
		adapter = new MessagesCursorAdapter(this, cursor);
		messagesListView.setAdapter(adapter);
	}
	
	
	
	
	@Override
	protected void onRestart() 
	{
		super.onRestart();
		
		/**
		 * In case we are back from onStop() we'd like to refresh the list cause it
		 * is possible we've recieved SMS messages while MainActivity was on the background
		 */
		Cursor cursor = helper.getAllMessages(true);
		adapter.changeCursor(cursor);
	}




	@Override
	protected void onResume() 
	{
		super.onResume();
		//����� ������ ����� ���� ��������� ����� ����� 
		//����� ����� ����� �� ������ �� ��� ������ ��� ���� �����
		// THIS INTENT FILTER WILL SERVE US WHEN THE SMSReciever will broadcast us with  
		// "com.sms.filter.SMS_RECEIVED" if the incoming SMS belong to a number from our preferences 
		IntentFilter smsFilter = new IntentFilter(Consts.SMS_RECEIVED_ACTION);
		
		/**
		 * Here we register the InnerSMSReciever to receive only incoming SMS message by
		 * using the intent filter above: smsFilter
		 */
		registerReceiver(smsRceiver, smsFilter);
		
		new LogoTask().execute();
	}

	@Override
	protected void onStop() 
	{
		// Here we un-register the smsRceiver because we have no interest to refresh the list if activity is 
		// on the background or destroyed (if it comes to that)
		super.onStop();
		unregisterReceiver(smsRceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}



	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {


		int id = item.getItemId();
		if (id == R.id.menu_settings) 
		{
			Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
			startActivityForResult(intent, 1);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	private class LogoTask extends AsyncTask<Void, Integer, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			progressBar.setProgress(0);
			progressBar.setMax(100);
			
			for(int i = 10;i<110;i++)
			{
				publishProgress(i);
				
				try {Thread.sleep(20);} catch (InterruptedException e) {}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			
			super.onProgressUpdate(values);
			
			progressBar.setProgress(values[0]);
			
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			logoImageView.setVisibility(View.GONE);
			mainLinearLayout.setVisibility(View.VISIBLE);
			
		}
	}
	
	/**
	 * This reciever will refresh the cursor adupter and list view
	 * ����� ��������� ����� ����� ����� �� ���� ����� ������ ���� ���
	 * ��� ����� �� ������ 
	 */
	private class InnerSMSReciever extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			Cursor cursor = helper.getAllMessages(true);
			adapter.changeCursor(cursor);
		}
	}		
}
