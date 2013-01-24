package com.kobaj.opengldrawable.Quad;

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
		reverse_texture_y = false;
		
		// create rest of data
		onCreate(texture_resource, width, height);
		this.alpha_resource = alpha_resource;
	}
	
	// do not delete
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		GLBitmapReader.unloadTexture(alpha_resource);
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
