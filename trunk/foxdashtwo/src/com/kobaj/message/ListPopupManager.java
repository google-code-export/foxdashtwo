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
import com.kobaj.math.Constants;

// Thanks to Google/Android for this.
// https://developer.android.com/guide/topics/ui/dialogs.html
public class ListPopupManager extends DialogFragment
{
	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface in order to receive event callbacks. Each method passes the DialogFragment in case the host needs to
	 * query it.
	 */
	public interface NoticeDialogListener
	{
		public void onDialogListSelect(int id);
	}
	
	// Use this instance of the interface to deliver action events
	private NoticeDialogListener mListener;
	
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
			throw new ClassCastException(activity.toString() + PopupManager.implement_error);
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// get all the accounts
		final String[] accounts = Constants.accounts.get_accounts();
		
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View mView = inflater.inflate(R.layout.alert_list_dialog, null);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setTitle(R.string.account_list).setCancelable(false).setView(mView).setItems(accounts, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				mListener.onDialogListSelect(id);
			}
		});
		
		return builder.create();
	}
	
}
