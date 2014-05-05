package com.sms.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * SMSReciever
 * @author Gabriel@proto-mech.com
 *
 *בקלאס זה נטפל בהודעה נכנסת שאנו מאזינים לה תמיד ללא קשר אם פתוח או סגור
 *במקרה שההודעה באה מאיש קשר שסומן בהגדרות אז
 *נפרק את ההודעה נשמור בבסיס הנתונים
 *ניתן הודעה בבר הנגלל העליון של מערכת ההפעלה
 *ונשלח הודעה
 *broadcastIntent
 *במקרה שיש מי שיאזין לה אז זה אומר שהאפלקציה פתוחה
 *בכל מקרה ישלח הדריגר רק לא בטוח שמישהו יתפוס אותו 
 */


@SuppressLint({ "NewApi", "DefaultLocale" })
public class SMSReciever extends BroadcastReceiver 
{
	private SharedPreferences pref;
	private Context context;
	private static int notificationId = 0;

	@SuppressLint("DefaultLocale")
	@Override
	public void onReceive(Context context, Intent intent) {

		
		this.context = context;
		
		// Read selected contacts on the preferences we've made earlier in the application settings
		//קבלת 
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean all = pref.getBoolean("check_pref", false);
		Set<String> choosenContacts =  pref.getStringSet("list_pref", null);
		Map<String, String> map = new HashMap<String, String>();
		
		// For performance sake, we keep all selected contacts in a map so we can 
		// find it faster
		// This is "foreach" loop. For more information please view
		// the next link http://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html
		
		for(String choosenContact : choosenContacts)
		{   // נעבור על כל השמות ששמרנו על ידי תפריט הגדרות ונשמור במפה
			String contanctName = checkContact(choosenContact);
			if(contanctName != null)
			{
				//map.put(contanctName, choosenContact);
				map.put(contanctName.toLowerCase(), choosenContact);
				
			}
		}
		
		//פירוק ההודעה שקבלנו
		// Now we will extract the SMS message from the bundle
		Bundle extras = intent.getExtras();
		SmsMessage[] msgs = null;
		//אם אין להודעה טקסט
		if(extras==null)
		{
			Log.e("SMSReciever", "Bad SMS data");
		}
		else
		{
			String message = "";
			String from = null;
			
			Object[] pdus = (Object[]) extras.get("pdus");
			
			msgs = new SmsMessage[pdus.length];
			
			for (int i = 0; i < msgs.length; i++) 
			{
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				from = msgs[i].getOriginatingAddress();
				String strMess = msgs[i].getMessageBody().toString();
				message += strMess;
			}
			
			// After we extract the message from the bundle we retrieve the
			// contact display name from the phone number of the SMS sender
			//נשלח לפונקציה את השמות ושם נבקש את המספר לפי השם מבסיס הנתונים של הטלפון

			String contanctName = checkContact(from);
			if(all && choosenContacts != null && !map.containsKey(contanctName.toLowerCase()))
			{
				// if the sender number is not in our preference list of contact names
				// we will ignore it.
				return;
			}

			// Now we know the the sms sender is in our preference list of selected preferences
			// Since we are interested with this incoming SMS we keep it in the database
			DBHandler handler = new DBHandler(context);
			// Add the incoming message into the database
			handler.addMessage(from, message);
			
			
			// NOW WE FINALLY CAN BROADCAST INTENT TO THE MAIN ACTIVITY THAT SAYS "WE HAVE NEW MESSAGE"
			
			// IF AND ONLY IF, THE MAIN ACTIVITY IS ON THE FOREGROUND AND REGISTERED TO LISTEN
			// TO THE INTENT ACTION  "com.sms.filter.SMS_RECEIVED" IT WILL GET THIS INTENT IN 
			// THE INNER CLASS MainActivity.InnerSMSReciever.onRecieve();
			//במקרה שהאפלקציה פתוחה אנו נעיר את האירוע קבלת הודעה למי שרשום אליה
			Intent broadcastIntent = new Intent(Consts.SMS_RECEIVED_ACTION);
			context.sendBroadcast(broadcastIntent);
			
			//בשביל הבר הנגלל למעלה במערכת ההפעלה
			//תמיד יעבוד גם אם סגור או פתוח האפלקציה
			// We also generate a notification that we have a new message on the notification bar
			generateNotification(from, message);
		}
	}
	
	//בנית ההודעה במערכת ההפעלה
	//בבר העליון הנגלל 
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	//מקבל לאחר פירוק ההודעה את מספר הטלפון של המשתמש ואת ההודעה
	private void generateNotification(String strFrom, String message) 
	{
		//מה יופיע בהודעה בבר למעלה
		NotificationCompat.Builder mBuilder =
    	        new NotificationCompat.Builder(context)
    	        .setSmallIcon(R.drawable.ic_launcher)
    	        .setContentTitle("SMS reminder")
    	        .setContentText(strFrom + " sent you message !");
    	
    		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    		//stackBuilder.addParentStack(DialerActivity.class);
    		
    		Intent intent = new Intent(context, MainActivity.class);
    		stackBuilder.addNextIntent(intent);
    	    
    		// Gets a PendingIntent containing the entire back stack
    		//מה יקרה כאשר נלחץ על הודעה בבר הנגלל
    		PendingIntent resultPendingIntent =
    			stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT|
    					android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
    	    //בנית ההודעה כמו בדיאלוג שאומרים לו להופיע
    	    mBuilder.setContentIntent(resultPendingIntent);
    	    Notification notification = mBuilder.build();
    	    // Clear the notification after been launched
    	    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	    
    	    NotificationManager mNotificationManager =
    	        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	    
    	    mNotificationManager.notify(notificationId, notification);
	}




//קבלת המספר שמימנו נשלח ההודעה	
	private String checkContact(String number){
		String name;
		String phoneNumber1;
		String phoneNumber;
		String number1=number;//ללא תוספת +972
		number1= number.substring(4);
		  number1="0"+number1;
		// if(!number.substring(1,4).equals("+972")){
	  number= number.substring(1);
	  number="+972"+number;
		//  }

		//שאילתה לקבלת טבלת אנשי קשר אלינו מבסיס הנתונים של הטלפון
		Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
		while (phones.moveToNext())
		{
		  name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		 // phoneNumber1 = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		 //קבלת המספר 
		  phoneNumber1 = phones.getString(phones.getColumnIndex(Phone.NUMBER));
		  int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
          switch (type) {
              case Phone.TYPE_HOME:
                  // do something with the Home number here...
                  break;
              case Phone.TYPE_MOBILE:
                  // נבדוק אם המספר שקבלנו ממנו את ההודעה שווה למספר שחלצנו מתוך השם שנשמר 
            	  //בהגדרות אז נחזיר את השם ונפעל בהתאמה
            	  phoneNumber=phoneNumber1;
            	  String s1 = phoneNumber.replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
            	  if(number.equals(s1)||number1.equals(s1)){
        			  phones.close();
        			 // number=null;
        			  return name;
        			 // phones.close();
            	  }
        			  break; 
              case Phone.TYPE_WORK:
                  // do something with the Work number here...
                  break;
              default: 
              	
              break;
               
              }
		  
		
		}
		//במקרה שלא שווה נחזיר את המספר 
		//Toast.makeText(this, name +" "+phoneNumber, Toast.LENGTH_LONG).show();
		// number=null;
		 return number;
	}
}


