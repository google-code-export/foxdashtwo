package com.kobaj.level.LevelEventTypes;

import com.kobaj.account_settings.SinglePlayerSave;

public class LevelEventCheckPoint extends LevelEventBase
{
	public LevelEventCheckPoint(EnumLevelEvent type)
	{
		super(type);
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		if (active)
			if (!id_cache.isEmpty())
				SinglePlayerSave.last_checkpoint = this.id_cache.get(0);
	}
	
}
