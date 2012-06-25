package com.kobaj.openglgraphics;

import android.opengl.GLES20;

import com.kobaj.foxdashtwo.R;

public class SpotLightShader extends PointLightShader
{
	public int my_light_dir_handle;
	public int my_light_angle_handle;
	
	//trying to do this without buffers
	public double my_direction_y;
	public double my_direction_x;
	
	public double my_angle;
	
	public SpotLightShader()
	{
		onInitializeShaders(R.raw.spot_vertex_shader, R.raw.spot_fragment_shader);
		my_light_pos_handle = GLES20.glGetUniformLocation(my_shader, "u_LightPos");
		
		my_light_dir_handle = GLES20.glGetUniformLocation(my_shader, "u_LightDir");
		my_light_angle_handle = GLES20.glGetUniformLocation(my_shader, "u_LightAngle");
	}
}
