package com.kobaj.opengldrawable.Quad;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.openglgraphics.BaseLightShader;
import com.kobaj.openglgraphics.BlurLightShader;
import com.kobaj.openglgraphics.CompressedLightShader;
import com.kobaj.openglgraphics.ShadowLightShader;

public final class QuadRenderShell
{
	// camera
	private static float[] my_mvp_matrix = new float[16];
	
	public static boolean program_update = true;
	
	// methods for generating verticies
	private static float[] my_position_matrix = new float[12];
	private static FloatBuffer my_position;
	private static boolean generated = false;
	
	private static final void generateVerts()
	{
		if (generated)
			return;
		
		float pos_tr_x = 1.0f;
		float pos_tr_y = 1.0f;
		
		float neg_tr_x = -pos_tr_x;
		float neg_tr_y = -pos_tr_y;
		
		final float z_buffer = 0.0f;
		
		// X, Y, Z
		my_position_matrix[0] = neg_tr_x; // top left
		my_position_matrix[1] = pos_tr_y;
		my_position_matrix[2] = z_buffer;
		
		my_position_matrix[3] = neg_tr_x; // bottom left
		my_position_matrix[4] = neg_tr_y;
		my_position_matrix[5] = z_buffer;
		
		// modified
		my_position_matrix[6] = pos_tr_x; // bottom right
		my_position_matrix[7] = neg_tr_y;
		my_position_matrix[8] = z_buffer;
		
		my_position_matrix[9] = pos_tr_x; // top right
		my_position_matrix[10] = pos_tr_y;
		my_position_matrix[11] = z_buffer;
		
		// Initialize the buffers. and store the new coords
		if (my_position == null)
			my_position = ByteBuffer.allocateDirect(my_position_matrix.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		else
			my_position.clear();
		
		my_position.put(my_position_matrix).position(0);
		generated = true;
	}
	
	// methods for
	// drawing stuffs
	private static int old_program;
	
	private static final <T extends BaseLightShader> void onSetupProgram(final T ambient_light)
	{
		if (old_program == ambient_light.my_shader && !program_update)
			return;
		
		// change everything
		old_program = ambient_light.my_shader;
		program_update = true;
		
		// setup the program
		GLES20.glUseProgram(ambient_light.my_shader);
	}
	
	private static int old_color;
	
	private static final <T extends BaseLightShader> void onSetupColor(final int color, final T ambient_light)
	{
		// quick attempt at optimization
		if (color == old_color && !program_update)
			return;
		
		old_color = color;
		
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
			alpha = 1;
		}
		else if (color == Color.TRANSPARENT)
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
	
	private static final <T extends BaseLightShader> void onSetupPosition(final T ambient_light)
	{
		if (!program_update)
			return;
		
		// pass in position information
		GLES20.glVertexAttribPointer(ambient_light.my_position_handle, 3, GLES20.GL_FLOAT, false, 0, my_position);
	}
	
	private static int old_texture_data_handle;
	private static boolean texture_set = false;
	
	private static final <T extends BaseLightShader> void onSetupTexture(final int my_texture_data_handle, final FloatBuffer my_tex_coord, final T ambient_light)
	{
		// quick attempt at optimization
		if (old_texture_data_handle != my_texture_data_handle || program_update)
		{
			old_texture_data_handle = my_texture_data_handle;
			
			// Set the active texture unit to texture unit 0 and bind necissary handles
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			if (!texture_set)
			{
				GLES20.glUniform1i(ambient_light.my_texture_uniform_handle, 0);
				texture_set = true;
			}
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_texture_data_handle);
		}
		
		// Pass in the texture coordinate information
		GLES20.glVertexAttribPointer(ambient_light.my_tex_coord_handle, 2, GLES20.GL_FLOAT, false, 0, my_tex_coord);
	}
	
	private static int old_alpha_data_handle;
	private static boolean alpha_set = false;
	
	private static final void onSetupAlpha(final int my_alpha_data_handle, final CompressedLightShader compressed_light)
	{
		if (old_alpha_data_handle == my_alpha_data_handle && !program_update)
			return;
		
		old_alpha_data_handle = my_alpha_data_handle;
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		if (!alpha_set)
		{
			GLES20.glUniform1i(compressed_light.my_alpha_uniform_handle, 1);
			alpha_set = true;
		}
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_alpha_data_handle);
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
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, my_position_matrix.length / 3); // 3 values per vertex
	}
	
	private static int old_light_data_handle;
	
	private static final void onSetupShadow(final float radius, final float x, final float y, final int my_light_data_handle, final ShadowLightShader shadow_light)
	{
		GLES20.glUniform1f(shadow_light.my_radius_handle, radius);
		GLES20.glUniform2f(shadow_light.my_shadow_position_handle, x, y);
		
		if (old_light_data_handle == my_light_data_handle)
			return;
		
		old_light_data_handle = my_light_data_handle;
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_light_data_handle);
		GLES20.glUniform1i(shadow_light.my_light_uniform_handle, 4);
	}
	
	private static int old_backgroup_data_handle;
	private static int old_foregroup_data_handle;
	
	private static final void onSetupShadowBF(final int my_backgroup_data_handle, final int my_foregroup_data_handle, final ShadowLightShader shadow_light)
	{
		if (old_backgroup_data_handle != my_backgroup_data_handle)
		{
			old_backgroup_data_handle = my_backgroup_data_handle;
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_backgroup_data_handle);
			GLES20.glUniform1i(shadow_light.my_backgroup_uniform_handle, 2);
		}
		
		if (old_foregroup_data_handle != my_foregroup_data_handle)
		{
			old_foregroup_data_handle = my_foregroup_data_handle;
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_foregroup_data_handle);
			GLES20.glUniform1i(shadow_light.my_foregroup_uniform_handle, 3);
		}
	}
	
	private static final void onSetupBlur(final float my_x_offset, final float my_y_offset, final BlurLightShader blur_light)
	{
		GLES20.glUniform2f(blur_light.my_offset_handle, my_x_offset, my_y_offset);
	}
	
	// the idea here is that you pass in quads that all reference
	// the same image with the same position coordinates and texture coordinates
	// not that you pass in all possible quads.
	// also they are all on the same z layer.
	// so like...particles.
	
	private static ArrayList<Quad> clones = new ArrayList<Quad>();

	public static final <T extends BaseLightShader> void onDrawQuad(final float[] my_vp_matrix, final boolean skip_draw_check, final T shader, final ArrayList<Quad> quads)
	{
		int quad_size = quads.size();
		if (quad_size <= 0)
			return;
		
		clones.clear();
		for (int i = quad_size - 1; i >= 0; i--)
			clones.add(quads.get(i));
		
		// generate some verts
		generateVerts();
		
		// first set our program
		onSetupProgram(shader);
		
		// be sure to draw all of our objects texture handles
		for (int i = clones.size() - 1; i >= 0; i--)
		{
			Quad uncompressed = clones.get(i);
			boolean removed = false;
			
			// see if regular texture is on device yet
			if (!uncompressed.setTextureDataHandle())
			{
				clones.remove(i);
				removed = true;
			}
			
			// see if compressed texture is on device
			if (QuadCompressed.class.isAssignableFrom(uncompressed.getClass()))
			{
				QuadCompressed reference = QuadCompressed.class.cast(uncompressed);
				if (!reference.setAlphaDataHandle() && !removed)
				{
					clones.remove(i);
					removed = true;
				}
			}
		}
		
		// new size after removal
		int clones_size = clones.size();
		if (clones_size <= 0)
			return;
		
		// then begin passing clones to gpu
		Quad zero = clones.get(0);
		
		onSetupTexture(zero.my_texture_data_handle, zero.my_tex_coord, shader);
		onSetupPosition(shader);
		
		// shadow
		if (QuadShadow.class.isAssignableFrom(zero.getClass()))
		{
			QuadShadow zero_shadow = QuadShadow.class.cast(zero);
			
			if (ShadowLightShader.class.isAssignableFrom(shader.getClass()))
			{
				onSetupShadow(zero_shadow.shadow_radius, zero_shadow.shadow_x_pos, zero_shadow.shadow_y_pos, zero_shadow.my_light_data_handle, (ShadowLightShader) shader);
				onSetupShadowBF(zero_shadow.my_backgroup_data_handle, zero_shadow.my_foregroup_data_handle, (ShadowLightShader) shader);
			}
			else
				Log.e("Shadow Shader Error", "Attempted to draw a shadow object with a non shadow shader.");
			
		}
		
		// blur
		if (QuadBlur.class.isAssignableFrom(zero.getClass()))
		{
			QuadBlur zero_blur = QuadBlur.class.cast(zero);
			
			if (BlurLightShader.class.isAssignableFrom(shader.getClass()))
				onSetupBlur((float) zero_blur.x_blur_offset, (float) zero_blur.y_blur_offset, (BlurLightShader) shader);
			else
				Log.e("Blur Shader Error", "Attempted to draw a blur object with a non blur shader.");
		}
		
		// compressed
		if (QuadCompressed.class.isAssignableFrom(zero.getClass()))
		{
			QuadCompressed zero_compressed = QuadCompressed.class.cast(zero);
			
			if (CompressedLightShader.class.isAssignableFrom(shader.getClass()))
				onSetupAlpha(zero_compressed.my_alpha_data_handle, (CompressedLightShader) shader);
			else
				Log.e("Compressed Shader Error", "Attempted to draw a compressed object with a non compressed shader.");
		}
		
		for (int i = clones_size - 1; i >= 0; i--)
		{
			Quad reference = clones.get(i);
			
			if (skip_draw_check || com.kobaj.math.Functions.onShader(reference.best_fit_aabb))
			{
				onSetupColor(reference.color, shader);
				onSetupModelMatrix(my_vp_matrix, reference.my_model_matrix, shader);
				onDraw();
			}
		}
		
		// very last
		program_update = false;
	}
	
	private static final ArrayList<Quad> single_quad = new ArrayList<Quad>();
	
	public static final <T extends BaseLightShader> void onDrawQuad(final float[] my_vp_matrix, final boolean skip_draw_check, final T shader, final Quad quad)
	{
		if (skip_draw_check || com.kobaj.math.Functions.onShader(quad.best_fit_aabb))
		{
			single_quad.clear();
			single_quad.add(quad);
			
			onDrawQuad(my_vp_matrix, true, shader, single_quad);
		}
	}
}
