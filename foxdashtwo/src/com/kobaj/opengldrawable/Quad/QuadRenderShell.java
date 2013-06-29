package com.kobaj.opengldrawable.Quad;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
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
	
	// used to see if we need to update something
	private static int old_program;
	private static int old_color;
	private static int old_texture_data_handle;
	private static boolean texture_set = false;
	private static int old_alpha_data_handle;
	private static boolean alpha_set = false;
	private static int old_light_data_handle;
	private static int old_backgroup_data_handle;
	private static int old_foregroup_data_handle;
	static private HashMap<BaseLightShader, Boolean> position_set = new HashMap<BaseLightShader, Boolean>();
	private static FloatBuffer old_tex_coord;
	
	private static ArrayList<Quad> clones_two = new ArrayList<Quad>();
	private static final ArrayList<Quad> single_quad = new ArrayList<Quad>();
	
	public static final void reset()
	{
		program_update = true;
		generated = false;
		
		old_program = 0;
		old_color = 0;
		old_texture_data_handle = 0;
		texture_set = false;
		old_alpha_data_handle = 0;
		alpha_set = false;
		old_light_data_handle = 0;
		old_backgroup_data_handle = 0;
		old_foregroup_data_handle = 0;
		position_set.clear();
		old_tex_coord = null;
		
		single_quad.add(null);
	}
	
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
		
		boolean value = position_set.containsKey(ambient_light);
		if (!value)
		{
			GLES20.glVertexAttribPointer(ambient_light.my_position_handle, 3, GLES20.GL_FLOAT, false, 0, my_position);
			position_set.put(ambient_light, true);
		}
	}
	
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
		if (old_tex_coord != my_tex_coord)
		{
			old_tex_coord = my_tex_coord;
			GLES20.glVertexAttribPointer(ambient_light.my_tex_coord_handle, 2, GLES20.GL_FLOAT, false, 0, my_tex_coord);
		}
	}
	
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
	
	// shadow interaction texture assignment
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
	
	// shadow backgroup and forgroup texture assignment
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
	
	@SuppressWarnings("unused")
	private static final void onSetupBlur(final float my_x_offset, final float my_y_offset, final BlurLightShader blur_light)
	{
		GLES20.glUniform2f(blur_light.my_offset_handle, my_x_offset, my_y_offset);
	}
	
	// the idea here is that you pass in quads that all reference
	// the same image with the same position coordinates and texture coordinates
	// not that you pass in all possible quads.
	// also they are all on the same z layer.
	// so like...particles.
	
	@SuppressLint("WrongCall")
	public static final <T extends BaseLightShader> void onDrawQuad(final float[] my_vp_matrix, final boolean skip_draw_check, final T shader, final ArrayList<Quad> quads)
	{
		int quad_size = quads.size();
		if (quad_size <= 0)
			return;
		
		//clones_two.clear();
		while (clones_two.size() < quad_size) {
	        clones_two.add(null);
	    }
		
		for (int i = quad_size - 1; i >= 0; i--)
			clones_two.set(i, quads.get(i));
		
		// generate some verts
		generateVerts();
		
		// first set our program
		onSetupProgram(shader);
		
		// be sure to draw all of our objects texture handles
		for (int i = quad_size - 1; i >= 0; i--)
		{
			Quad uncompressed = clones_two.get(i);
			boolean removed = false;
			
			if(uncompressed == null)
			{
				clones_two.remove(i);
				removed = true;
				quad_size--;
				continue;
			}
			
			// see if regular texture is on device yet
			if (!uncompressed.setTextureDataHandle())
			{
				clones_two.remove(i);
				removed = true;
				quad_size--;
			}
			
			// see if compressed texture is on device
			if (uncompressed instanceof QuadCompressed)
			{
				QuadCompressed reference = QuadCompressed.class.cast(uncompressed);
				if ((!reference.setAlphaDataHandle() && !removed) || (!skip_draw_check && !com.kobaj.math.Functions.onShader(reference.best_fit_aabb)))
				{
					clones_two.remove(i);
					removed = true;
					quad_size--;
				}
			}
		}
		
		// new size after removal
		int clones_size = quad_size;
		if (clones_size <= 0)
			return;
		
		// then begin passing clones to gpu
		
		Quad zero = clones_two.get(0);
		
		onSetupTexture(zero.my_texture_data_handle, zero.my_tex_coord, shader);
		onSetupPosition(shader);
		
		// compressed
		if (zero instanceof QuadCompressed)
		{
			QuadCompressed zero_compressed = QuadCompressed.class.cast(zero);
			
			if (shader instanceof CompressedLightShader)
				onSetupAlpha(zero_compressed.my_alpha_data_handle, (CompressedLightShader) shader);
			else
				Log.e("Compressed Shader Error", "Attempted to draw a compressed object with a non compressed shader.");
		}
		
		// shadow
		else if (zero instanceof QuadShadow)
		{
			QuadShadow zero_shadow = QuadShadow.class.cast(zero);
			
			if (shader instanceof ShadowLightShader)
			{
				onSetupShadow(zero_shadow.shadow_radius, zero_shadow.shadow_x_pos, zero_shadow.shadow_y_pos, zero_shadow.my_light_data_handle, (ShadowLightShader) shader);
				onSetupShadowBF(zero_shadow.my_backgroup_data_handle, zero_shadow.my_foregroup_data_handle, (ShadowLightShader) shader);
			}
			else
				Log.e("Shadow Shader Error", "Attempted to draw a shadow object with a non shadow shader.");
			
		}
		
		for (int i = clones_size - 1; i >= 0; i--)
		{
			Quad reference = clones_two.get(i);
			onSetupColor(reference.color, shader);
			onSetupModelMatrix(my_vp_matrix, reference.my_model_matrix, shader);
			onDraw();
		}
		
		// very last
		program_update = false;
	}
	
	public static final <T extends BaseLightShader> void onDrawQuad(final float[] my_vp_matrix, final boolean skip_draw_check, final T shader, final Quad quad)
	{
		if (skip_draw_check || com.kobaj.math.Functions.onShader(quad.best_fit_aabb))
		{
			single_quad.set(0, quad);
			
			onDrawQuad(my_vp_matrix, true, shader, single_quad);
		}
	}
}
