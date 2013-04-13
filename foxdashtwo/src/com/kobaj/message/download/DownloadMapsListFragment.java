package com.kobaj.message.download;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kobaj.account_settings.UserSettings;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.message.animation.ExpandAnimation;
import com.kobaj.message.download.LevelItem.EnumButtonStates;
import com.kobaj.networking.MyTask;
import com.kobaj.networking.NetworkManager;
import com.kobaj.networking.TaskGetDownloadedMaps;
import com.kobaj.networking.TaskGetDownloadedMaps.finishedLoading;

public class DownloadMapsListFragment extends ListFragment
{
	public DownloadMapsFragmentPager parent;
	
	private int current_page;
	private DownloadListAdapter adapter = new DownloadListAdapter();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle data = getArguments();
		current_page = data.getInt("current_page", 0);
		
		// set all these wonderful references
		adapter.parent = this;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		ListView list = this.getListView();
		
		list.setOnScrollListener(new ScrollHelper(this));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
			{
				
				View toolbar = view.findViewById(R.id.toolbar);
				
				if(toolbar == null)
					return;
				
				// Creating the expand animation for the item
				ExpandAnimation expandAni = new ExpandAnimation(toolbar, 500);
				
				// Start the animation on the toolbar
				toolbar.startAnimation(expandAni);
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.download_maps_list, container, false);
		
		tabUpdate();
		
		setListAdapter(adapter);
		
		return v;
	}
	
	public void tabUpdate()
	{
		// determine what we show the user depending on the tab
		if (!isDownloadedPage())
		{
			LoadFeedData load_feed_data = new LoadFeedData(adapter);
			load_feed_data.execute(String.valueOf(adapter.getCount()), DownloadMapsFragmentPager.pages[current_page]);
		}
		else
		{
			// get currently downloaded maps
			LoadDiskData load_disk_data = new LoadDiskData(adapter);
			load_disk_data.execute();
		}
	}
	
	public boolean isDownloadedPage()
	{
		return current_page == DownloadMapsFragmentPager.pages.length - 1;
	}
}

// thanks http://benjii.me/2010/08/endless-scrolling-listview-in-android/

class ScrollHelper implements OnScrollListener
{
	private int visibleThreshold = 1;
	private int previousTotal = 0;
	private boolean loading = true;
	
	private DownloadMapsListFragment fragment_reference;
	
	public ScrollHelper(DownloadMapsListFragment frag)
	{
		this.fragment_reference = frag;
	}
	
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		if (loading)
		{
			if (totalItemCount > previousTotal)
			{
				loading = false;
				previousTotal = totalItemCount;
			}
		}
		if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold))
		{
			// I load the next page of gigs using a background task,
			// but you can call any function here.
			// new LoadGigsTask().execute(currentPage + 1);
			fragment_reference.tabUpdate();
			loading = true;
		}
	}
	
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
	}
}

class LoadDiskData implements finishedLoading
{
	private final DownloadListAdapter my_adapter;
	
	public LoadDiskData(DownloadListAdapter adapter)
	{
		my_adapter = adapter;
	}
	
	public void execute()
	{
		TaskGetDownloadedMaps maps = new TaskGetDownloadedMaps();
		maps.setFinishedLoading(this);
		maps.execute();
	}
	
	public void onTaskComplete(ArrayList<LevelItem> levels)
	{
		my_adapter.onUpdateEntries(levels, true);
	}
}

// thanks
// http://www.developerfusion.com/article/145373/android-listviews-with-dynamic-data/

class LoadFeedData extends MyTask
{
	private final DownloadListAdapter my_adapter;
	
	public LoadFeedData(DownloadListAdapter adapter)
	{
		super();
		my_adapter = adapter;
	}
	
	@Override
	protected String getUrl(String... attributes)
	{
		String the_url = Constants.empty;
		
		// modify the url
		if (attributes.length == 2)
		{
			HashMap<String, String> url_helper = new HashMap<String, String>();
			url_helper.put(NetworkManager.url_file, "game.php");
			url_helper.put(NetworkManager.url_action, "get_map_list");
			
			url_helper.put("count", attributes[0]);
			url_helper.put("page_type", attributes[1]);
			
			if (Constants.logged_in && UserSettings.selected_account_login != -1)
				url_helper.put("user_email", Constants.accounts.get_accounts()[UserSettings.selected_account_login]);
			
			the_url = NetworkManager.genericUrlBuilder(url_helper);
		}
		
		return the_url;
	}
	
	@Override
	protected void parseJSON(JSONObject json)
	{
		boolean success;
		try
		{
			success = json.getBoolean("success");
			if (success)
			{
				JSONArray levels = json.getJSONArray("levels");
				
				// traverse
				ArrayList<LevelItem> level_items = new ArrayList<LevelItem>();
				int level_count = levels.length();
				for (int i = 0; i < level_count; i++)
				{
					try
					{
						JSONObject json_data = levels.getJSONObject(i);
						
						LevelItem temp = new LevelItem();
						temp.name = json_data.getString(TaskGetDownloadedMaps.name);
						temp.lid = json_data.getInt(TaskGetDownloadedMaps.lid);
						temp.changed = json_data.getInt(TaskGetDownloadedMaps.changed);
						
						EnumButtonStates this_button_state = my_adapter.parent.parent.parent.downloaded_maps.get(temp.lid);
						if (this_button_state != null)
							temp.this_state = this_button_state;
						
						level_items.add(temp);
					}
					catch (JSONException e)
					{
						// do nothing
					}
					
				}
				
				my_adapter.onUpdateEntries(level_items, false);
			}
		}
		catch (JSONException e)
		{
			// do nothing
		}
	}
}

class DownloadListAdapter extends BaseAdapter implements ListAdapter
{
	public DownloadMapsListFragment parent;
	
	private ArrayList<LevelItem> level_names = new ArrayList<LevelItem>();
	private LayoutInflater mLayoutInflator;
	
	public DownloadListAdapter()
	{
		mLayoutInflator = (LayoutInflater) Constants.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void onUpdateEntries(ArrayList<LevelItem> levels, boolean clear)
	{
		if (clear)
			level_names.clear();
		
		if (levels.isEmpty() && level_names.isEmpty())
		{
			LevelItem temp = new LevelItem();
			temp.name = "None";
			temp.changed = 0;
			temp.lid = -1;
			
			levels.add(temp);
		}
		
		// and then walk through adding and updating
		int level_count = levels.size();
		for (int i = 0; i < level_count; i++)
		{
			LevelItem reference = levels.get(i);
			reference.setDownloadListAdapter(this);
			level_names.add(reference);
		}
		
		notifyDataSetChanged();
	}
	
	public int getCount()
	{
		return level_names.size();
	}
	
	public Object getItem(int position)
	{
		return level_names.get(position);
	}
	
	public long getItemId(int position)
	{
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup view_parent)
	{
		ViewGroup item_view;
		
		if (convertView == null)
		{
			if (parent.isDownloadedPage())
			{
				item_view = (LinearLayout) mLayoutInflator.inflate(R.layout.download_maps_item_expandable, view_parent, false);
			}
			else
			{
				item_view = (RelativeLayout) mLayoutInflator.inflate(R.layout.download_maps_item, view_parent, false);
			}
		}
		else
		{
			item_view = (ViewGroup) convertView;
		}
		
		// get the main elements
		TextView title_text = (TextView) item_view.findViewById(R.id.title_text);
		TextView description_text = (TextView) item_view.findViewById(R.id.description_text);
		
		ProgressBar progressbar = (ProgressBar) item_view.findViewById(R.id.progressbar_download);
		
		Button play_update_button = (Button) item_view.findViewById(R.id.button_download);
		
		LevelItem this_item = level_names.get(position);
		play_update_button.setOnClickListener(this_item.play_update_listener);
		this_item.button = play_update_button;
		this_item.progressbar = progressbar;
		
		if (this_item.this_state == LevelItem.EnumButtonStates.play)
			play_update_button.setText(R.string.play);
		else if (this_item.this_state == LevelItem.EnumButtonStates.update)
			play_update_button.setText(R.string.update);
		else if (this_item.this_state == LevelItem.EnumButtonStates.download)
			play_update_button.setText(R.string.download);
		
		if (this_item.lid < 1)
		{
			play_update_button.setVisibility(View.INVISIBLE);
			description_text.setVisibility(View.INVISIBLE);
		}
		
		// set the elements
		title_text.setText(this_item.name);
		
		// format the date
		String time = DateUtils.formatDateTime(Constants.context, this_item.changed * 1000, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
		description_text.setText(time);
		
		// then do the secondary elements
		
		return item_view;
	}
}
