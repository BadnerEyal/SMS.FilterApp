package com.sms.filter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.view.View;
import android.widget.TextView;


/**
 * ViewHolder
 * @author Gabriel@proto-mech.com
 *
 */
public class ViewHolder 
{
	//בשביל להוסיף את שעת קבלת ההודעה
	final static DateFormat DATETIME_FORMATTER = new SimpleDateFormat("HH:MM\ndd/mm");
	private TextView messageTextView;
	private TextView fromTextView;
	private TextView timestampTextView;
	
	private long id;
	private String from;
	private String message;
	private long timestamp;
	
	
	public ViewHolder(View view) 
	{
		messageTextView = (TextView)view.findViewById(R.id.messageTextView);
		fromTextView = (TextView)view.findViewById(R.id.fromTextView);;
		timestampTextView = (TextView)view.findViewById(R.id.timestampTextView);
	}
	public TextView getMessageTextView() {
		return messageTextView;
	}
	public void setMessageTextView(TextView messageTextView) {
		this.messageTextView = messageTextView;
	}
	public TextView getFromTextView() {
		return fromTextView;
	}
	public void setFromTextView(TextView fromTextView) {
		this.fromTextView = fromTextView;
	}
	public TextView getTimestampTextView() {
		return timestampTextView;
	}
	public void setTimestampTextView(TextView timestampTextView) {
		this.timestampTextView = timestampTextView;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
		 fromTextView.setText(from);
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
		messageTextView.setText(message);
	}
	public long getTimestamp() 
	{
		return timestamp;
	}
	public void setTimestamp(long timestamp) 
	{
		this.timestamp = timestamp;
		timestampTextView.setText(DATETIME_FORMATTER.format(new Date(timestamp)));
	}

	
}
