package com.kobaj.message.download;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.loader.FileHandler;
import com.kobaj.networking.TaskDownloadMap;
import com.kobaj.networking.TaskDownloadMap.FinishedDownloading;
import com.kobaj.screen.SinglePlayerScreen;

public class LevelItem implements FinishedDownloading
{
	public static enum EnumButtonStates
	{
		download, update, play, delete
	};
	
	public EnumButtonStates this_state = EnumButtonStates.download;
	
	public String name;
	public int lid;
	public long changed;
	public long download_time;
	
	public Button button;
	public ProgressBar progressbar;
	
	private TaskDownloadMap downloader;
	private DownloadListAdapter adapter;
	
	public LevelItem()
	{
		downloader = new TaskDownloadMap();
		downloader.setFinishedDownloading(this);
	}
	
	public void setDownloadListAdapter(DownloadListAdapter adapater)
	{
		this.adapter = adapater;
	}
	
	public OnClickListener play_update_listener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if (this_state == EnumButtonStates.play)
			{
				// play time
				SinglePlayerSave.last_level = FileHandler.download_dir + String.valueOf(lid);
				SinglePlayerSave.last_checkpoint = null;
				
				// load the next level
				adapter.parent.parent.parent.dismiss();
				GameActivity.mGLView.my_game.onChangeScreen(new SinglePlayerScreen());
			}
			else if (this_state == EnumButtonStates.download || this_state == EnumButtonStates.update)
			{
				progressbar.setVisibility(View.VISIBLE);
				v.setVisibility(View.INVISIBLE);
				
				// download the map here.
				downloader.execute(String.valueOf(lid));
			}
		}
	};
	
	public void onTaskCompleted(int lid)
	{
		this_state = EnumButtonStates.play;
		button.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.INVISIBLE);
		adapter.parent.parent.parent.update_downloaded_maps();
		adapter.notifyDataSetChanged();
	}
}
