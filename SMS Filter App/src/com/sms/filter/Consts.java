package com.sms.filter;

public class Consts 
{
	public final static String DataBaseName = "messages.db";
	public final static int DataBaseVersion = 1;
	
	public final static String TABLE_NAME = "message_table";
	public final static String ID = "_id";
	public final static String TIMESTAMP = "_timestamp";
	public final static String MESSAGE = "_message";
	public final static String FROM = "_from";
	
	public final static String SMS_RECEIVED_ACTION = "com.sms.filter.SMS_RECEIVED";
}
