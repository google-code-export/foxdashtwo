package com.kobaj.opengldrawable.Quad;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.opengl.GLES20;

import com.kobaj.loader.GLBitmapReader;
import com.kobaj.loader.GLLoadedTexture;
import com.kobaj.math.Constants;

public class QuadCompressed extends Quad
{
	public int my_alpha_data_handle = -1;
	protected int alpha_resource;
	
	protected QuadCompressed()
	{
		// let the extendor figure it out
	}
	
	public QuadCompressed(int texture_resource, int alpha_resource, int width, int height)
	{
		super();
		
		// load dat texture.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		com.kobaj.loader.GLBitmapReader.loadTextureFromResource(texture_resource, true);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		com.kobaj.loader.GLBitmapReader.loadTextureFromResource(alpha_resource, true);
		
		// create rest of data
		onCreate(texture_resource, width, height);
		this.alpha_resource = alpha_resource;
	}
	
	// these are in shader coordinates. start_x, end_x, start_y, end_y
	// THIS METHOD IS DIFFERENT THAN THE REGULAR QUAD
	@Override
	protected void complexUpdateTexCoords(float one_x, float two_x, float one_y, float two_y)
	{
		// only time I use floats...
		float buffer = -0.005f;
		one_x -= buffer;
		two_x += buffer;
		one_y -= buffer;
		two_y += buffer;
		
		// S, T (or X, Y)
		// Texture coordinate data.
		final float[] cubeTextureCoordinateData = {
				// Front face
				one_x, one_y,//
				one_x, two_y,//
				two_x, one_y,//
				
				one_x, two_y,//
				two_x, two_y,//
				two_x, one_y };//
		
		if (my_tex_coord == null)
			my_tex_coord = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		else
			my_tex_coord.clear();
		
		my_tex_coord.put(cubeTextureCoordinateData).position(0);
	}
	
	public boolean setAlphaDataHandle()
	{
		if (my_alpha_data_handle != -1)
			return true;
		
		if (alpha_resource != -1)
		{
			GLLoadedTexture proposed_handle = GLBitmapReader.loaded_textures.get(alpha_resource);
			if (proposed_handle != null)
			{
				my_alpha_data_handle = proposed_handle.texture_id;
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onDrawAmbient(float[] my_vp_matrix, boolean skip_draw_check)
	{
		QuadRenderShell.onDrawQuad(my_vp_matrix, skip_draw_check, Constants.compressed_light, this);
	}
}
