package com.sms.filter;

import static com.sms.filter.Consts.FROM;
import static com.sms.filter.Consts.ID;
import static com.sms.filter.Consts.MESSAGE;
import static com.sms.filter.Consts.TIMESTAMP;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * MessagesCursorAdapter
 * @author Gabriel@proto-mech.com
 *
 */
public class MessagesCursorAdapter extends CursorAdapter 
{

	final LayoutInflater inflator;
	
	public MessagesCursorAdapter(Context context, Cursor c) 
	{
		super(context, c, true);
		inflator = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) 
	{
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		
		viewHolder.setId(cursor.getLong(cursor.getColumnIndex(ID)));
		viewHolder.setTimestamp(cursor.getLong(cursor.getColumnIndex(TIMESTAMP)));
		viewHolder.setFrom(cursor.getString(cursor.getColumnIndex(FROM)));
		viewHolder.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
	}

	@Override
	public View newView(Context context, Cursor arg1, ViewGroup arg2) {
		View view = inflator.inflate(R.layout.row, null);
		ViewHolder viewHolder = new ViewHolder(view);
		view.setTag(viewHolder);
	
		return view;
	}

}
