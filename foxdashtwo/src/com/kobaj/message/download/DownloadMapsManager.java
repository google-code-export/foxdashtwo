package com.kobaj.message.download;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.kobaj.foxdashtwo.R;

public class DownloadMapsManager extends DialogFragment
{
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{	
		View view = inflater.inflate(R.layout.download_maps_dialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
		DownloadMapsFragmentPager adapter = new DownloadMapsFragmentPager(this.getChildFragmentManager());
		pager.setAdapter(adapter);
		
		return view;
	}
}
