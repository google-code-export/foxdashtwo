package com.kobaj.message.download;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;
import com.kobaj.message.DeletePopupManager;
import com.kobaj.networking.task.HalfTaskDeleteMap;
import com.kobaj.networking.task.HalfTaskDeleteMap.FinishedDeleteing;
import com.kobaj.networking.task.TaskDownloadMap;
import com.kobaj.networking.task.TaskDownloadMap.FinishedDownloading;
import com.kobaj.screen.SinglePlayerScreen;

public class LevelItem implements FinishedDownloading, FinishedDeleteing
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
	
	public Button play_update_button;
	public ProgressBar progressbar;
	
	private TaskDownloadMap downloader;
	private DownloadListAdapter adapter;
	
	private HalfTaskDeleteMap deleter;
	
	public LevelItem()
	{
		downloader = new TaskDownloadMap();
		downloader.setFinishedDownloading(this);
		
		deleter = new HalfTaskDeleteMap();
		deleter.setFinishedDeleteing(this);
	}
	
	public void setDownloadListAdapter(DownloadListAdapter adapater)
	{
		this.adapter = adapater;
	}
	
	public OnClickListener delete_listener = new OnClickListener()
	{
		public void onClick(View v)
		{
			DeletePopupManager popup = new DeletePopupManager();
			popup.deleter = deleter;
			popup.name = name;
			popup.lid = lid;
			popup.show(Constants.fragment_manager, "delete_tag");
		}
	};
	
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
	
	public void onDownloadCompleted(int lid)
	{
		this_state = EnumButtonStates.play;
		play_update_button.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.INVISIBLE);
		adapter.parent.parent.parent.update_downloaded_maps();
		adapter.notifyDataSetChanged();
	}
	
	public void onDeleteCompleted()
	{
		adapter.parent.parent.parent.downloaded_maps.remove(lid);
		adapter.onRemoveEntry(lid);
	}
}
