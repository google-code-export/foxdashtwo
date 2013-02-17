package com.kobaj.message.download;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DownloadMapsFragmentPager extends FragmentPagerAdapter
{
	
	final int PAGE_COUNT = 5;
	
	/** Constructor of the class */
	public DownloadMapsFragmentPager(FragmentManager fm)
	{
		super(fm);
	}
	
	/** This method will be invoked when a page is requested to create */
	@Override
	public Fragment getItem(int arg0)
	{
		DownloadMapsListFragment myFragment = new DownloadMapsListFragment();
		Bundle data = new Bundle();
		data.putInt("current_page", arg0 + 1);
		myFragment.setArguments(data);
		return myFragment;
	}
	
	/** Returns the number of pages */
	@Override
	public int getCount()
	{
		return PAGE_COUNT;
	}
	
	@Override
	public CharSequence getPageTitle(int position)
	{
		return "Page #" + (position + 1);
	}
}
