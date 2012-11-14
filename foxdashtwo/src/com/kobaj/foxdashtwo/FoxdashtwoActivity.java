package com.kobaj.foxdashtwo;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.kobaj.screen.SinglePlayerScreen;
import com.kobaj.message.*;

public final class FoxdashtwoActivity extends GameActivity implements PopupManager.NoticeDialogListener 
{
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
		mGLView.my_game.onChangeScreen(new SinglePlayerScreen());
	}

	public void onDialogPositiveClick(DialogFragment dialog)
	{
		// TODO Auto-generated method stub
		
	}

	public void onDialogNegativeClick(DialogFragment dialog)
	{
		// TODO Auto-generated method stub
		
	}
}