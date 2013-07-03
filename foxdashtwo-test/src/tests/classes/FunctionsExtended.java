package tests.classes;

import com.kobaj.math.Constants;
import com.kobaj.math.android.RectF;

public class FunctionsExtended extends com.kobaj.math.Functions
{
	public static RectF publicShaderRectFView()
	{
		return shader_rectf_view;
	}
	
	public static void publicUpdateShaderRectFView()
	{
		updateShaderRectFView();
	}
	
	public static void resetShaderStatics()
	{
		Constants.x_shader_translation = 0;
		Constants.y_shader_translation = 0;
		Constants.z_shader_translation = 0;
		Constants.ratio = 0;
	}
}
