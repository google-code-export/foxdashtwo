package com.kobaj.message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.kobaj.foxdashtwo.R;
import com.kobaj.loader.FileHandler;
import com.kobaj.networking.task.HalfTaskDeleteMap;

// Thanks to Google/Android for this.
// https://developer.android.com/guide/topics/ui/dialogs.html
public class DeletePopupManager extends DialogFragment
{
	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface in order to receive event callbacks. Each method passes the DialogFragment in case the host needs to
	 * query it.
	 */
	
	public HalfTaskDeleteMap deleter;
	public String name;
	public int lid;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setTitle("Delete " + name + "?").setCancelable(true).setPositiveButton(R.string.ok, new OnClickListener()
		{
			
			public void onClick(DialogInterface dialog, int which)
			{
				FileHandler.deleteFile(FileHandler.download_dir + String.valueOf(lid));
				dialog.dismiss();
				deleter.onFinishedDeleteing();
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener()
		{
			
			public void onClick(DialogInterface arg0, int arg1)
			{
				arg0.dismiss();
			}
		});
		
		return builder.create();
	}
	
}
