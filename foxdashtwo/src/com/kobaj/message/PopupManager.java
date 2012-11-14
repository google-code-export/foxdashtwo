package com.kobaj.message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.kobaj.foxdashtwo.R;

/*public class PopupManager
 {
 // save map functionality
 private static final String save_map_title = "Save";
 private static final String save_map_message = "Whats the XML?";
 private static final String OK = "Ok";
 private static final String CANCEL = "Cancel";
 private static final String empty = "";
 private static final String space = " ";

 private static void alertSaveMap()
 {
 AlertDialog.Builder alert = new AlertDialog.Builder(Constants.context);

 alert.setTitle(save_map_title);
 alert.setMessage(save_map_message);

 // Set an EditText view to get user input
 final EditText input = new EditText(Constants.context);
 alert.setView(input);

 alert.setPositiveButton(OK, new DialogInterface.OnClickListener()
 {
 public void onClick(DialogInterface dialog, int whichButton)
 {
 String value = input.getText().toString();
 if (value.equals(empty) || value.equals(space))
 return;

 FileHandler.writeTextFile(SinglePlayerScreen.save_file_name , value);

 FoxdashtwoActivity.mGLView.my_game.onChangeScreen(new SinglePlayerScreen());
 // save the file and then call the loader
 }
 });

 alert.setNegativeButton(CANCEL, new DialogInterface.OnClickListener()
 {
 public void onClick(DialogInterface dialog, int whichButton)
 {
 // Canceled.
 }
 });

 alert.show();
 }

 public static void showSimplePopUp(final EnumPopupType the_type)
 {
 FoxdashtwoActivity.itself.post(new Runnable()
 {
 public void run()
 {
 if(the_type == EnumPopupType.save_map)
 alertSaveMap();
 }
 });
 }
 }*/

/*public class PopupManager extends Activity {
 private static final int DIALOG_YES_NO_MESSAGE = 1;
 private static final int DIALOG_YES_NO_LONG_MESSAGE = 2;
 private static final int DIALOG_LIST = 3;
 private static final int DIALOG_PROGRESS = 4;
 private static final int DIALOG_SINGLE_CHOICE = 5;
 private static final int DIALOG_MULTIPLE_CHOICE = 6;
 private static final int DIALOG_TEXT_ENTRY = 7;
 private static final int DIALOG_MULTIPLE_CHOICE_CURSOR = 8;
 private static final int DIALOG_YES_NO_ULTRA_LONG_MESSAGE = 9;
 private static final int DIALOG_YES_NO_OLD_SCHOOL_MESSAGE = 10;
 private static final int DIALOG_YES_NO_HOLO_LIGHT_MESSAGE = 11;

 private static final int MAX_PROGRESS = 100;

 private ProgressDialog mProgressDialog;
 private int mProgress;
 private Handler mProgressHandler;

 @Override
 protected Dialog onCreateDialog(int id) {
 switch (id) {
 case DIALOG_TEXT_ENTRY:
 // This example shows how to add a custom layout to an AlertDialog
 LayoutInflater factory = LayoutInflater.from(this);
 final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
 return new AlertDialog.Builder(PopupManager.this)
 //.setTitle(R.string.alert_dialog_text_entry)
 .setView(textEntryView)
 .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
 public void onClick(DialogInterface dialog, int whichButton) {

 // User clicked OK so do some stuff 
 }
 })
 .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
 public void onClick(DialogInterface dialog, int whichButton) {

 /// User clicked cancel so do some stuff 
 }
 })
 .create();
 }
 return null;
 }

 @Override
 protected void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);

 //setContentView(R.layout.alert_dialog_text_entry);

 // Display a text entry dialog 
 showDialog(DIALOG_TEXT_ENTRY);

 mProgressHandler = new Handler() {
 @Override
 public void handleMessage(Message msg) {
 super.handleMessage(msg);
 if (mProgress >= MAX_PROGRESS) {
 mProgressDialog.dismiss();
 } else {
 mProgress++;
 mProgressDialog.incrementProgressBy(1);
 mProgressHandler.sendEmptyMessageDelayed(0, 100);
 }
 }
 };
 }
 }*/

public class PopupManager extends DialogFragment
{
	
	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface in order to receive event callbacks. Each method passes the DialogFragment in case the host needs to
	 * query it.
	 */
	public interface NoticeDialogListener
	{
		public void onDialogPositiveClick(DialogFragment dialog);
		
		public void onDialogNegativeClick(DialogFragment dialog);
	}
	
	// Use this instance of the interface to deliver action events
	NoticeDialogListener mListener;
	
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

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.alert_dialog_text_entry, null))
	    
		.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// Send the positive button event back to the host activity
				mListener.onDialogPositiveClick(PopupManager.this);
			}
		})
		.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// Send the negative button event back to the host activity
				mListener.onDialogNegativeClick(PopupManager.this);
			}
		});
		return builder.create();
	}
	
}
