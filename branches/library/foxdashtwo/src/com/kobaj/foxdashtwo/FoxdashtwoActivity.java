package com.kobaj.foxdashtwo;

import android.os.Bundle;
import android.widget.EditText;

import com.kobaj.loader.FileHandler;
import com.kobaj.message.PopupManager;
import com.kobaj.screen.SinglePlayerScreen;

public final class FoxdashtwoActivity extends GameActivity implements PopupManager.NoticeDialogListener
{
	// this is awesome http://www.regexplanet.com/advanced/java/index.html
	private static final String empty = "";
	private static final String space = " ";
	private static final String period = ".";
	private static final String reg_remove_space = "\\s*\\.\\s*";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mGLView.my_game.onChangeScreen(new SinglePlayerScreen());
	}
	
	public void onDialogPositiveClick(PopupManager dialog)
	{
		EditText view = (EditText) dialog.mView.findViewById(R.id.username_edit);
		String value = view.getText().toString().trim().replaceAll(reg_remove_space, period);
		if (value.equals(empty) || value.equals(space) || value.equals(false))
			return;
		FileHandler.writeTextFile(SinglePlayerScreen.save_file_name, value);
		mGLView.my_game.onChangeScreen(new SinglePlayerScreen());
	}
}
