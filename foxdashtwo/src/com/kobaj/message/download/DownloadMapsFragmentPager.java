package com.kobaj.message.download;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

// thanks to 
// http://wptrafficanalyzer.in/blog/implementing-horizontal-view-swiping-using-viewpager-and-fragmentpageradapter-in-android/

public class DownloadMapsFragmentPager extends FragmentPagerAdapter
{
	public final static String[] pages = { "New Maps", "Highest Rated", "Downloaded" };
	
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
		data.putInt("current_page", arg0);
		myFragment.setArguments(data);
		return myFragment;
	}
	
	/** Returns the number of pages */
	@Override
	public int getCount()
	{
		return pages.length;
	}
	
	@Override
	public CharSequence getPageTitle(int position)
	{
		return pages[position];
	}
}
