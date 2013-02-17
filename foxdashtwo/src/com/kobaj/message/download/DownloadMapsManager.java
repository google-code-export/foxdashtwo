package com.kobaj.message.download;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kobaj.foxdashtwo.R;

public class DownloadMapsManager extends DialogFragment
{
	@Override
    public void onAttach(Activity activity)
    {
            super.onAttach(activity);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.download_maps_dialog, container);
        getDialog().setTitle("Hola tronco");

        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        DownloadMapsFragmentPager adapter = new DownloadMapsFragmentPager(this.getChildFragmentManager());  
        pager.setAdapter(adapter);

        return view;
    }
	
	/*
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		//inflate a view
		View mView = inflater.inflate(R.layout.download_maps_dialog, null);

		ViewPager pager = (ViewPager) mView.findViewById(R.id.pager);

		DownloadMapsFragmentPager pagerAdapter = new DownloadMapsFragmentPager(this.getChildFragmentManager());

		pager.setAdapter(pagerAdapter);
		
		builder.setTitle("DELETE ME HERP").setCancelable(true).setView(mView);
		
		return builder.create();
	}*/
}
