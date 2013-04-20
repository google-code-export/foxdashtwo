package com.kobaj.message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.networking.task.TaskSendRate;

// Thanks to Google/Android for this.
// https://developer.android.com/guide/topics/ui/dialogs.html
public class RatePopupManager extends DialogFragment
{
	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface in order to receive event callbacks. Each method passes the DialogFragment in case the host needs to
	 * query it.
	 */
	
	public String name;
	public int lid;
	public int current_rateing;
	public TaskSendRate sender;
	
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
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		final View mView = inflater.inflate(R.layout.rateing_dialog, null);
		final RatingBar rateing_bar = (RatingBar) mView.findViewById(R.id.ratingbar);
		rateing_bar.setRating(current_rateing);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setTitle("Rate " + name).setCancelable(true).setView(mView).setPositiveButton(R.string.ok, new OnClickListener()
		{
			
			public void onClick(DialogInterface dialog, int which)
			{
				Constants.accounts.sendRate(lid, (int) rateing_bar.getRating(), sender);
				dialog.dismiss();
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
