package com.kobaj.openglgraphics;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kobaj.foxdashtwo.R;

public class PointLightShader extends BaseLightShader
{
	// light stuffs
	private final float[] my_light_model_space = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	
	private final float[] my_light_world_space = new float[4];
	
	public final float[] my_light_eye_space = new float[4];
	
	private final  float[] my_light_matrix = new float[16];
	
	public int my_light_pos_handle;
	
	public double my_focus;
	
	public PointLightShader()
	{
		onInitializeShaders(R.raw.point_vertex_shader, R.raw.point_fragment_shader);
		my_light_pos_handle = GLES20.glGetUniformLocation(my_shader, "u_LightPos");
	}
	
	public void setPosition(double x, double y, float[] my_view_matrix)
	{
		Matrix.setIdentityM(my_light_matrix, 0);
		Matrix.translateM(my_light_matrix, 0, (float)x, (float)y, (float)-my_focus);
		
		Matrix.multiplyMV(my_light_world_space, 0, my_light_matrix, 0, my_light_model_space, 0);
		Matrix.multiplyMV(my_light_eye_space, 0, my_view_matrix, 0, my_light_world_space, 0);
	}
}
