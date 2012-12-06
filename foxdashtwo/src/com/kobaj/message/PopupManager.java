package com.kobaj.message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.kobaj.foxdashtwo.R;

// Thanks to Google/Android for this.
// https://developer.android.com/guide/topics/ui/dialogs.html
public class PopupManager extends DialogFragment
{
	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface in order to receive event callbacks. Each method passes the DialogFragment in case the host needs to
	 * query it.
	 */
	public interface NoticeDialogListener
	{
		public void onDialogPositiveClick(PopupManager dialog);
	}
	
	// Use this instance of the interface to deliver action events
	private NoticeDialogListener mListener;
	
	public View mView;
	
	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try
		{
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (NoticeDialogListener) activity;
		}
		catch (ClassCastException e)
		{
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		mView = inflater.inflate(R.layout.alert_dialog_text_entry, null);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setTitle(R.string.alert_dialog_xml)
		.setView(mView)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// Send the positive button event back to the host activity
				mListener.onDialogPositiveClick(PopupManager.this);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// do nothing
			}
		});
		return builder.create();
	}
	
}
