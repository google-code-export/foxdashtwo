package com.kobaj.level.LevelEventTypes;

public enum EnumLevelEvent
{
	none, death, right_arrow, left_arrow, up_arrows, invisible_wall, 
	
	toggle_active, off_active, on_active, touch_active, anti_touch_active,
	
	next_level, checkpoint, color, snow,
	
	/* one last thing */
	thought_bubble, movement, erase_checkpoint, right_arrow_delay
}
