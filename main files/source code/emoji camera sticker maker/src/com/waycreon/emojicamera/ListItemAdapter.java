package com.waycreon.emojicamera;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.waycreon.emojicamera.R;


public class ListItemAdapter extends BaseAdapter  {
	
	
	LayoutInflater inflater;
	
	
	
	MyViewHolder mViewHolder;
	
	Activity activity;
		public ListItemAdapter(Activity mActivity) {
		this.activity = mActivity;
		inflater = LayoutInflater.from(mActivity);	
		
		mViewHolder = new MyViewHolder();
		//this.activity=activity;// only context can also be use
	}
/*
 * 
 * counts the size of list for showing in listview it is same as no. of records in table boat of local database 
 */
	@Override
	public int getCount() {
		return 10;
	}

//	@Override
//	public MessageDetail getItem(int position) {
//		return myList.get(position);
//	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		
		if(convertView == null) 
		{
			
				convertView = inflater.inflate(R.layout.tatoo_image_list, null);
				
				
				
				convertView.setTag(mViewHolder);
		} 
		else 
		{
			mViewHolder = (MyViewHolder) convertView.getTag();
		}
		
		
		
		
		return convertView;
	}
	

	private class MyViewHolder
	{
		TextView mTextViewName;
		TextView mTextViewMessage;
		TextView mTextViewTime;

		LinearLayout mLinearLayoutRoot;
		
		ImageView mImageView;
		ImageView mImageViewUnread;
		
	}

	public String localtime(String utcTime)
	{
		long unixSeconds = (Double.valueOf(utcTime)).longValue();

		Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
		sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // give a timezone reference for formating (see comment at the bottom
		String formattedDate = sdf.format(date);
		return formattedDate;
	}


	public String printDifference(Date startDate, Date endDate){

		String time = "";

		//milliseconds
		long different = endDate.getTime() - startDate.getTime();

		//		System.out.println("startDate : " + startDate);
		//		System.out.println("endDate : "+ endDate);
		//		System.out.println("different : " + different);

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		//		System.out.printf(
		//				"%d days, %d hours, %d minutes, %d seconds%n", 
		//				elapsedDays,
		//				elapsedHours, elapsedMinutes, elapsedSeconds);

		if(elapsedDays > 0)
		{
			time = elapsedDays+" days ago";
		}
		else if(elapsedHours > 0)
		{
			time = elapsedHours+" hours ago";
		}
		else if(elapsedMinutes > 0)
			time = elapsedMinutes+" minutes ago";
		else 
			time = elapsedSeconds+" seconds ago";
		
		return time;

	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	

	}
