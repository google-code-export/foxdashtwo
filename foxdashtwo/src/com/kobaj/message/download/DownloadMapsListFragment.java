package com.kobaj.message.download;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.loader.FileHandler;
import com.kobaj.loader.RawTextReader;
import com.kobaj.math.Constants;
import com.kobaj.networking.MyTask;
import com.kobaj.networking.NetworkManager;
import com.kobaj.networking.TaskDownloadMap;
import com.kobaj.screen.SinglePlayerScreen;

public class DownloadMapsListFragment extends ListFragment
{
	int current_page;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle data = getArguments();
		
		current_page = data.getInt("current_page", 0);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.download_maps_list, container, false);
		
		DownloadListAdapter adapter = new DownloadListAdapter();
		setListAdapter(adapter);
		
		// determine what we show the user depending on the tab
		if (current_page != DownloadMapsFragmentPager.pages.length - 1)
		{
			LoadFeedData load_feed_data = new LoadFeedData(adapter);
			load_feed_data.execute(String.valueOf(adapter.getCount()), DownloadMapsFragmentPager.pages[current_page]);
		}
		else
		{
			LoadDiskData load_disk_data = new LoadDiskData(adapter);
			load_disk_data.get_downloaded_levels();
		}
		
		return v;
	}
	
}

class LoadDiskData
{
	private final DownloadListAdapter my_adapter;
	private static final String download_directory = "/download";
	
	public LoadDiskData(DownloadListAdapter adapter)
	{
		my_adapter = adapter;
	}
	
	public void get_downloaded_levels()
	{
		String[] files = FileHandler.getFileList(download_directory);
		
		ArrayList<LevelItem> levels = new ArrayList<LevelItem>();
		for (int i = files.length - 1; i >= 0; i--)
		{
			String full_file = files[i];
			
			String the_level = FileHandler.readTextFile(FileHandler.download_dir, full_file);
			
			if (the_level != null)
			{
				LevelItem temp = new LevelItem();
				temp.downloaded = true;
				
				temp.lid = Integer.valueOf(RawTextReader.findValueInXML(the_level, "lid"));
				temp.name = RawTextReader.findValueInXML(the_level, "name");
				temp.changed_time = Long.valueOf(RawTextReader.findValueInXML(the_level, "changed"));
				
				levels.add(temp);
				
				the_level = null;
			}
		}
		
		my_adapter.onUpdateEntries(levels);
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
			Uri.Builder b = Uri.parse(NetworkManager.server).buildUpon();
			
			b.path(NetworkManager.php_extension + "/game.php");
			
			b.appendQueryParameter("action", "get_map_list");
			b.appendQueryParameter("count", attributes[0]); // how many are currently grabbed
			b.appendQueryParameter("page_type", attributes[1]); // what type to grab
			
			the_url = b.build().toString();
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
					JSONObject json_data = levels.getJSONObject(i);
					LevelItem temp = new LevelItem();
					temp.name = json_data.getString("name");
					temp.lid = json_data.getInt("lid");
					temp.changed_time = json_data.getInt("changed_time");
					
					level_items.add(temp);
				}
				
				my_adapter.onUpdateEntries(level_items);
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
	private ArrayList<LevelItem> level_names = new ArrayList<LevelItem>();
	private LayoutInflater mLayoutInflator;
	
	public DownloadListAdapter()
	{
		mLayoutInflator = (LayoutInflater) Constants.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void onUpdateEntries(ArrayList<LevelItem> levels)
	{
		if (levels.isEmpty())
		{
			LevelItem temp = new LevelItem();
			temp.name = "None";
			temp.changed_time = 0;
			temp.lid = -1;
			
			levels.add(temp);
		}
		
		level_names.addAll(levels);
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
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		RelativeLayout item_view;
		
		if (convertView == null)
		{
			item_view = (RelativeLayout) mLayoutInflator.inflate(R.layout.download_maps_item, parent, false);
		}
		else
		{
			item_view = (RelativeLayout) convertView;
		}
		
		LevelItem this_item = level_names.get(position);
		
		// get the elements
		TextView title_text = (TextView) item_view.findViewById(R.id.title_text);
		TextView description_text = (TextView) item_view.findViewById(R.id.description_text);
		
		ProgressBar progressbar = (ProgressBar) item_view.findViewById(R.id.progressbar_download);
		this_item.progressbar = progressbar;
		
		Button button = (Button) item_view.findViewById(R.id.button_download);
		button.setOnClickListener(this_item.listener);
		this_item.button = button;
		
		if (this_item.downloaded)
			button.setText(R.string.play);
		else if (this_item.lid == -1)
		{
			button.setVisibility(View.INVISIBLE);
			description_text.setVisibility(View.INVISIBLE);
		}
		
		// set the elements
		title_text.setText(this_item.name);
		
		// format the date
		String time = DateUtils.formatDateTime(Constants.context, this_item.changed_time * 1000, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
		description_text.setText(time);
		
		return item_view;
	}
}

class LevelItem
{
	public boolean downloaded = false;
	
	public String name;
	public int lid;
	public long changed_time;
	
	public Button button;
	public ProgressBar progressbar;
	
	OnClickListener listener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if (downloaded)
			{
				// play time
				SinglePlayerSave.last_level = FileHandler.download_dir + String.valueOf(lid);
				SinglePlayerSave.last_checkpoint = null;
				
				// load the next level
				GameActivity.mGLView.my_game.onChangeScreen(new SinglePlayerScreen());
			}
			else
			{
				progressbar.setVisibility(View.VISIBLE);
				v.setVisibility(View.INVISIBLE);
				
				// download the map here.
				TaskDownloadMap downloader = new TaskDownloadMap();
				downloader.execute(String.valueOf(lid));
			}
		}
	};
}
