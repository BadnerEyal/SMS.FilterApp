package com.sms.filter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;

//קלאס לתפריט הגדרות
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private String name;
	private String phoneNumber;
	private ArrayList<String> contacts_names=null;
	private ArrayList<String> contacts_phones=null;
	
	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_layout);
		checkContact();
		
		//בשביל אוביקט בחירה מרובה של אנשי קשר
		MultiSelectListPreference mlp = (MultiSelectListPreference) findPreference("list_pref");
		//השם
		final CharSequence[] chars = contacts_names.toArray(new CharSequence[contacts_names.size()]);
		mlp.setEntries(chars);
		//מספר הטלפון
		final CharSequence[] chars2 = contacts_phones.toArray(new CharSequence[contacts_phones.size()]);
		mlp.setEntryValues(chars2);
		
		
	}

	

	//לכל איש קשר שנלחץ ישמר השם ומספר הטלפון שלו במערך
	private void checkContact(){
		contacts_names = new ArrayList<String>();
		contacts_phones = new ArrayList<String>();
		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
		while (phones.moveToNext())
		{
		  name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		  phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		  String s1 = phoneNumber.replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
		  contacts_phones.add(s1);
		  contacts_names.add(name);
		 
		}
		phones.close();
		//Toast.makeText(this, name +" "+phoneNumber, Toast.LENGTH_LONG).show();
	}



	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}


}
