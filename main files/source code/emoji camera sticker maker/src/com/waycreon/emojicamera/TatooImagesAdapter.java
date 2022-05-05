package com.waycreon.emojicamera;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.waycreon.emojicamera.R;

public class TatooImagesAdapter extends BaseAdapter{

	ImageView imgIcon ;
	ArrayList<Mylist> myList = new ArrayList<Mylist>(); 
	LayoutInflater inflater; 
	Context context;
	Activity activity;
	String fragmentName;

	public TatooImagesAdapter(Activity activity2,ArrayList<Mylist> myList2) {

		this.myList = myList2; 
		this.activity=activity2;
		inflater = LayoutInflater.from(activity);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myList.size();
	}

	public Mylist getItem(int position) {

		return myList.get(position);
	}


	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final MyViewHolder mViewHolder;   
		if(convertView == null) 
		{ 	

			mViewHolder = new MyViewHolder(); 
			convertView = inflater.inflate(R.layout.tatoo_image_list, null);
			
			
			mViewHolder.mImageView=(ImageView)convertView.findViewById(R.id.tatoo_image);
			convertView.setTag(mViewHolder);
		} 
		else
		{
			mViewHolder = (MyViewHolder) convertView.getTag();
		}
		mViewHolder.mImageView.setImageResource(myList.get(position).getId());
		
		
		return convertView;
	}
	private class MyViewHolder 
	{ 
		TextView mTextViewName;
		TextView mTextViewKms;
		TextView mTextViewPrice;
		TextView mTextViewAds;
		ImageView mImageView;
	} 
}
