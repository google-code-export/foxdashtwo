package com.kobaj.foxdashtwo;

import android.os.Bundle;

import com.kobaj.screen.TitleScreen;

public final class FoxdashtwoActivity extends GameActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mGLView.my_game.onChangeScreen(new TitleScreen());
	}
}
