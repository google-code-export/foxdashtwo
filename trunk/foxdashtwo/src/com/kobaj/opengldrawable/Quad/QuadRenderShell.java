package com.kobaj.opengldrawable.Quad;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.openglgraphics.BaseLightShader;
import com.kobaj.openglgraphics.CompressedLightShader;

public final class QuadRenderShell
{
	// camera
	private static float[] my_mvp_matrix = new float[16];
	
	// methods for
	// drawing stuffs
	private static final <T extends BaseLightShader> void onSetupProgram(final T ambient_light)
	{
		// setup the program
		GLES20.glUseProgram(ambient_light.my_shader);
	}
	
	private static final <T extends BaseLightShader> void onSetupColor(final int color, final T ambient_light)
	{
		// quick attempt at optimization
		// this is white
		float red = 1;
		float green = 1;
		float blue = 1;
		float alpha = 1;
		
		if (color == Color.BLACK)
		{
			red = 0;
			green = 0;
			blue = 0;
			alpha = 0;
		}
		else if (color != Color.WHITE)
		{
			red = (float) Functions.byteToShader(Functions.red(color));
			green = (float) Functions.byteToShader(Functions.green(color));
			blue = (float) Functions.byteToShader(Functions.blue(color));
			alpha = (float) Functions.byteToShader(Functions.alpha(color));
		}
		
		// pass in color
		GLES20.glUniform4f(ambient_light.my_color_handle, red, green, blue, alpha);
	}
	
	private static final <T extends BaseLightShader> void onSetupPosition(final FloatBuffer my_position, final T ambient_light)
	{
		// pass in position information
		GLES20.glVertexAttribPointer(ambient_light.my_position_handle, 3, GLES20.GL_FLOAT, false, 0, my_position);
	}
	
	private static final <T extends BaseLightShader> void onSetupTexture(final int my_texture_data_handle, final FloatBuffer my_tex_coord, final T ambient_light)
	{
		// Set the active texture unit to texture unit 0 and bind necissary handles
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_texture_data_handle);
		GLES20.glUniform1i(ambient_light.my_texture_uniform_handle, 0);
		
		// Pass in the texture coordinate information
		GLES20.glVertexAttribPointer(ambient_light.my_tex_coord_handle, 2, GLES20.GL_FLOAT, false, 0, my_tex_coord);
	}
	
	private static final void onSetupAlpha(final int my_alpha_data_handle, final CompressedLightShader compressed_light)
	{
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_alpha_data_handle);
		GLES20.glUniform1i(compressed_light.my_alpha_uniform_handle, 1);
	}
	
	private static final <T extends BaseLightShader> void onSetupModelMatrix(final float[] my_vp_matrix, final float[] my_model_matrix, final T ambient_light)
	{
		// multiplies the vp matrix with the model matrix
		// result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(my_mvp_matrix, 0, my_vp_matrix, 0, my_model_matrix, 0);
		
		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(ambient_light.my_mvp_matrix_handle, 1, false, my_mvp_matrix, 0);
	}
	
	@SuppressWarnings("unused")
	private static final void onFinishSetup()
	{
		// Clear the currently bound buffer (so future OpenGL calls do not use
		// this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
	
	// main stuffs
	private static final void onDraw()
	{
		Constants.quads_drawn_screen++;
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
	
	// the idea here is that you pass in quads that all reference
	// the same image with the same position coordinates and texture coordinates
	// not that you pass in all possible quads.
	// also they are all on the same z layer.
	// so like...particles.
	public static final <T extends BaseLightShader> void onDrawQuad(final float[] my_vp_matrix, final boolean skip_draw_check, final T shader, final ArrayList<Quad> quads)
	{
		if (quads.size() <= 0)
			return;
		
		// first set our program
		onSetupProgram(shader);
		
		// then begin passing quads to gpu
		Quad zero = quads.get(0);
		
		// if we have a handle, draw.
		if (!zero.setTextureDataHandle())
			return;
		
		onSetupTexture(zero.my_texture_data_handle, zero.my_tex_coord, shader);
		onSetupPosition(zero.my_position, shader);
		
		if (QuadCompressed.class.isAssignableFrom(zero.getClass()))
		{
			QuadCompressed zero_compressed = QuadCompressed.class.cast(zero);
			onSetupAlpha(zero_compressed.my_alpha_data_handle, (CompressedLightShader) shader);
			
			if(!zero_compressed.setAlphaDataHandle())
				return;
		}

		for (int i = quads.size() - 1; i >= 0; i--)
		{
			Quad reference = quads.get(i);
			
			if(skip_draw_check || com.kobaj.math.Functions.onShader(reference.best_fit_aabb))
			{
				onSetupColor(reference.color, shader);
				onSetupModelMatrix(my_vp_matrix, reference.my_model_matrix, shader);
				onDraw();
			}
		}
	}
	
	private static final ArrayList<Quad> single_quad = new ArrayList<Quad>();
	
	public static final <T extends BaseLightShader> void onDrawQuad(final float[] my_vp_matrix, final boolean skip_draw_check, final T shader, final Quad quad)
	{
		if(skip_draw_check || com.kobaj.math.Functions.onShader(quad.best_fit_aabb))
		{
			single_quad.clear();
			single_quad.add(quad);
		
			onDrawQuad(my_vp_matrix, true, shader, single_quad);
		}
	}
}
