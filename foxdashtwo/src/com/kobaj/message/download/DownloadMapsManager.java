package com.kobaj.message.download;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.message.download.LevelItem.EnumButtonStates;
import com.kobaj.networking.task.TaskGetDownloadedMaps;
import com.kobaj.networking.task.TaskGetDownloadedMaps.finishedLoading;

public class DownloadMapsManager extends DialogFragment implements finishedLoading
{
	public SparseArray<EnumButtonStates> downloaded_maps = new SparseArray<EnumButtonStates>();
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		// and build our initial list of maps
		update_downloaded_maps();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.download_maps_dialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
		DownloadMapsFragmentPager adapter = new DownloadMapsFragmentPager(this.getChildFragmentManager());
		adapter.parent = this;
		pager.setAdapter(adapter);
		
		Constants.global_draw = false;
		
		return view;
	}
	
	@Override
	public void onDismiss(DialogInterface dialog)
	{
		super.onDismiss(dialog);
		Constants.global_draw = true;
	}
	
	public void update_downloaded_maps()
	{
		TaskGetDownloadedMaps downloaded_maps = new TaskGetDownloadedMaps();
		downloaded_maps.setFinishedLoading(this);
		downloaded_maps.execute();
	}
	
	public void onTaskComplete(ArrayList<LevelItem> levels)
	{
		downloaded_maps.clear();
		
		for (int i = levels.size() - 1; i >= 0; i--)
		{
			LevelItem reference = levels.get(i);
			downloaded_maps.put(reference.lid, reference.this_state);
		}
	}
}
