package tests.activity;

//Big thanks to https://github.com/jmschultz/Eclipse-Robolectric-Example

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import tests.classes.FunctionsExtended;
import tests.runner.SampleTestRunner;
import android.graphics.Color;

import com.kobaj.math.Constants;
import com.kobaj.math.android.RectF;

// as of 10/24/12 this is 156 tests :)
// as of 12/15/12 this is 178 tests :D
// as of 07/02/13 this is 214 tests :] 

@RunWith(SampleTestRunner.class)
public class FunctionsTest
{
	@Test
	public void testFunctionsLinearInterpolate() throws Exception
	{
		assertThat(FunctionsExtended.linearInterpolate(-1, 1.0, 1.0, 0, 100), equalTo(100.0));
		assertThat(FunctionsExtended.linearInterpolate(-1, 1.0, -1.0, 0, 100), equalTo(0.0));
		assertThat(FunctionsExtended.linearInterpolate(-1, 1.0, 0.0, 0, 100), equalTo(50.0));
		
		assertThat(FunctionsExtended.linearInterpolate(-1, 1.0, -10.0, 0, 100), equalTo(0.0));
		assertThat(FunctionsExtended.linearInterpolate(-1, 1.0, 100.0, 0, 100), equalTo(100.0));
	}
	
	@Test
	public void testFunctionsLinearInterpolateUnclamped() throws Exception
	{
		assertThat(FunctionsExtended.linearInterpolateUnclamped(0, 100, 100, -1, 1), equalTo(1.0));
		assertThat(FunctionsExtended.linearInterpolateUnclamped(0, 100, 0, -1, 1), equalTo(-1.0));
		assertThat(FunctionsExtended.linearInterpolateUnclamped(0, 100, 50, -1, 1), equalTo(0.0));
		
		assertThat(FunctionsExtended.linearInterpolateUnclamped(0, 100, 200, -1, 1), equalTo(3.0));
	}
	
	@Test
	public void testFunctionsLinearInterpolateColor() throws Exception
	{
		assertThat(FunctionsExtended.linearInterpolateColor(0, 1, 1, Color.WHITE, Color.BLACK), equalTo(Color.BLACK));
		assertThat(FunctionsExtended.linearInterpolateColor(0, 1, 0, Color.WHITE, Color.BLACK), equalTo(Color.WHITE));
		assertThat(FunctionsExtended.linearInterpolateColor(0, 1, .5, Color.WHITE, Color.BLACK), equalTo(-8421505));
	}
	
	@Test
	public void testFunctionsClamp() throws Exception
	{
		assertThat(FunctionsExtended.clamp(100, 10, 0), equalTo(10.0));
		assertThat(FunctionsExtended.clamp(100, 200, 0), equalTo(100.0));
		assertThat(FunctionsExtended.clamp(100, -100, 0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsByteToShader() throws Exception
	{
		assertThat(FunctionsExtended.byteToShader(0), equalTo(0.0));
		assertThat(FunctionsExtended.byteToShader(255), equalTo(1.0));
		assertThat(FunctionsExtended.byteToShader(275), equalTo(1.0));
		assertThat(FunctionsExtended.byteToShader(100) < 1.0, equalTo(true));
		assertThat(FunctionsExtended.byteToShader(100) > 0.0, equalTo(true));
	}
	
	@Test
	public void testFunctionsScreenXToShaderX() throws Exception
	{
		FunctionsExtended.resetShaderStatics();
		Constants.ratio = 1.0;
		Constants.width = 200;
		
		FunctionsExtended.publicUpdateShaderRectFView();
		
		assertThat(FunctionsExtended.screenXToShaderX(200), equalTo(1.0));
		assertThat(FunctionsExtended.screenXToShaderX(0), equalTo(-1.0));
		
		assertThat(FunctionsExtended.screenXToShaderX(400) > 1.0, equalTo(true));
	}
	
	@Test
	public void testFunctionsScreenYToShaderY() throws Exception
	{
		FunctionsExtended.resetShaderStatics();
		Constants.height = 200;
		
		FunctionsExtended.publicUpdateShaderRectFView();
		
		assertThat(FunctionsExtended.screenYToShaderY(200), equalTo(1.0));
		assertThat(FunctionsExtended.screenYToShaderY(0), equalTo(-1.0));
		
		assertThat(FunctionsExtended.screenYToShaderY(400) > 1.0, equalTo(true));
	}
	
	@Test
	public void testFunctionsScreenWidthToShaderWidth() throws Exception
	{
		FunctionsExtended.resetShaderStatics();
		Constants.shader_width = 2.0;
		Constants.width = 200;
		
		FunctionsExtended.publicUpdateShaderRectFView();
		
		assertThat(FunctionsExtended.screenWidthToShaderWidth(200.0), equalTo(2.0));
		assertThat(FunctionsExtended.screenWidthToShaderWidth(0), equalTo(0.0));
		
		assertThat(FunctionsExtended.screenWidthToShaderWidth(400.0) > 2.0, equalTo(true));
	}
	
	@Test
	public void testFunctionsScreenHeightToShaderHeight() throws Exception
	{
		FunctionsExtended.resetShaderStatics();
		Constants.height = 200;
		
		FunctionsExtended.publicUpdateShaderRectFView();
		
		assertThat(FunctionsExtended.screenHeightToShaderHeight(200.0), equalTo(2.0));
		assertThat(FunctionsExtended.screenHeightToShaderHeight(0), equalTo(0.0));
		
		assertThat(FunctionsExtended.screenHeightToShaderHeight(400.0) > 2.0, equalTo(true));
	}
	
	@Test
	public void testFunctionsShaderXToScreenX() throws Exception
	{
		FunctionsExtended.resetShaderStatics();
		Constants.ratio = 1.0;
		Constants.width = 200;
		
		FunctionsExtended.publicUpdateShaderRectFView();
		
		assertThat(FunctionsExtended.shaderXToScreenX(1.0), equalTo(200.0));
		assertThat(FunctionsExtended.shaderXToScreenX(-1.0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsShaderYToScreenY() throws Exception
	{
		FunctionsExtended.resetShaderStatics();
		Constants.height = 200;
		
		FunctionsExtended.publicUpdateShaderRectFView();
		
		assertThat(FunctionsExtended.shaderYToScreenY(1.0), equalTo(200.0));
		assertThat(FunctionsExtended.shaderYToScreenY(-1.0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsShaderWidthToScreenWidth() throws Exception
	{
		FunctionsExtended.resetShaderStatics();
		Constants.shader_width = 2.0;
		Constants.width = 200;
		
		FunctionsExtended.publicUpdateShaderRectFView();
		
		assertThat(FunctionsExtended.shaderWidthToScreenWidth(2.0), equalTo(200.0));
		assertThat(FunctionsExtended.shaderWidthToScreenWidth(0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsShaderHeightToScreenHeight() throws Exception
	{
		FunctionsExtended.resetShaderStatics();
		Constants.height = 200;
		
		FunctionsExtended.publicUpdateShaderRectFView();
		
		assertThat(FunctionsExtended.shaderHeightToScreenHeight(2.0), equalTo(200.0));
		assertThat(FunctionsExtended.shaderHeightToScreenHeight(0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsRandomDouble()
	{
		for (int i = 0; i < 90000; i++)
		{
			double min = i - .4645;
			double max = i + .73840;
			double random = FunctionsExtended.randomDouble(min, max);
			assertThat(random >= min, equalTo(true));
			assertThat(random <= max, equalTo(true));
		}
	}
	
	@Test
	public void testFunctionsInRectF() throws Exception
	{
		RectF my_rect = new RectF(0, 1, 1, 0);
		
		// true
		assertThat(FunctionsExtended.inRectF(my_rect, .5, .5), equalTo(true));
		assertThat(FunctionsExtended.inRectF(my_rect, 1, 1), equalTo(true));
		assertThat(FunctionsExtended.inRectF(my_rect, 0, 0), equalTo(true));
		
		// false
		assertThat(FunctionsExtended.inRectF(my_rect, .5, -.5), equalTo(false));
		assertThat(FunctionsExtended.inRectF(my_rect, .5, 2.0), equalTo(false));
		assertThat(FunctionsExtended.inRectF(my_rect, -.5, .5), equalTo(false));
		assertThat(FunctionsExtended.inRectF(my_rect, 2.0, .5), equalTo(false));
	}
	
	@Test
	public void testFunctionsOnScreen() throws Exception
	{
		// set constants.
		FunctionsExtended.resetShaderStatics();
		
		Constants.width = 800;
		Constants.height = 480;
		Constants.ratio = (double) Constants.width / (double) Constants.height;
		Constants.y_shader_translation = 0.0;
		Constants.x_shader_translation = 0.0;
		Constants.z_shader_translation = 0.0;
		
		FunctionsExtended.publicUpdateShaderRectFView();
		
		// true
		assertThat(FunctionsExtended.onScreen(200, 200), equalTo(true));
		assertThat(FunctionsExtended.onScreen(400, 480), equalTo(true));
		assertThat(FunctionsExtended.onScreen(799, 400), equalTo(true));
		
		// false
		assertThat(FunctionsExtended.onScreen(-200, 200), equalTo(false));
		assertThat(FunctionsExtended.onScreen(400, -480), equalTo(false));
		assertThat(FunctionsExtended.onScreen(-800, -400), equalTo(false));
	}
	
	@Test
	public void testFunctionsOnShader() throws Exception
	{
		// set constants.
		FunctionsExtended.resetShaderStatics();
		
		Constants.ratio = 2.0;
		Constants.y_shader_translation = 1.0;
		Constants.x_shader_translation = 1.0;
		Constants.z_shader_translation = 0.0;
		
		FunctionsExtended.publicUpdateShaderRectFView();
		
		// true
		assertThat(FunctionsExtended.onShader(0, 0), equalTo(true));
		assertThat(FunctionsExtended.onShader(1.0, 1.0), equalTo(true));
		assertThat(FunctionsExtended.onShader(1.9999, 1.9999), equalTo(true));
		
		// false
		assertThat(FunctionsExtended.onShader(-2.0, -2.0), equalTo(false));
		assertThat(FunctionsExtended.onShader(4.0, 4.0), equalTo(false));
		
		// shift the z
		Constants.z_shader_translation = .5;
		FunctionsExtended.publicUpdateShaderRectFView();
		
		// true
		assertThat(FunctionsExtended.onShader(-1.9999, 0), equalTo(true));
		assertThat(FunctionsExtended.onShader(4.0, 1.9999), equalTo(true));
		
		// false
		assertThat(FunctionsExtended.onShader(-2.0, -2.0), equalTo(false));
		assertThat(FunctionsExtended.onShader(4.0, 4.0), equalTo(false));
	}
	
	@Test
	public void testFunctionsUpdateShaderRectFView() throws Exception
	{
		// set constants.
		FunctionsExtended.resetShaderStatics();
		
		Constants.ratio = 2.0;
		Constants.y_shader_translation = 1.0;
		Constants.x_shader_translation = 1.0;
		Constants.z_shader_translation = 0.0;
		
		// update the shader rectf
		tests.classes.FunctionsExtended.publicUpdateShaderRectFView();
		
		// test the shader rectf
		
		// assert correct orientation
		RectF local_copy = tests.classes.FunctionsExtended.publicShaderRectFView();
		assertThat(local_copy.left < local_copy.right, equalTo(true));
		assertThat(local_copy.top > local_copy.bottom, equalTo(true));
		
		// assert actual values
		assertThat(equalRectF(local_copy, new RectF(-1, 2, 3, 0)), equalTo(true));
		
		// shift the z
		Constants.z_shader_translation = .5;
		
		// update the shader rectf
		tests.classes.FunctionsExtended.publicUpdateShaderRectFView();
		
		// test the shader rectf
		local_copy = tests.classes.FunctionsExtended.publicShaderRectFView();
		assertThat(local_copy.left < local_copy.right, equalTo(true));
		assertThat(local_copy.top > local_copy.bottom, equalTo(true));
		
		// assert actual values
		assertThat(equalRectF(local_copy, new RectF(-2, 2.5f, 4, -.5f)), equalTo(true));
	}
	
	@Test
	public void testFunctionsEqualIntersects() throws Exception
	{
		RectF a = new RectF(0, 1, 1, 0);
		RectF b = new RectF(0, 0, 0, 0);
		
		// general collisions
		
		// first that they are the exact same
		b = new RectF(0, 1, 1, 0);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// b is bigger
		b = new RectF(-1, 2, 2, -1);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// b is smaller
		b = new RectF(.25f, .75f, .75f, .25f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// parallel collisions
		
		// b on top collision
		b = new RectF(.25f, 2.0f, .75f, .5f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// b on bottom collision
		b = new RectF(.25f, .5f, .75f, -.5f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// b on left collision
		b = new RectF(-1.0f, .75f, .5f, .25f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// b on right collision
		b = new RectF(.5f, .75f, 2.0f, .25f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// corner collisions
		
		// b on top left collision
		b = new RectF(-1.0f, 2.0f, .75f, .5f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// b on bottom left collision
		b = new RectF(-1.0f, .5f, .75f, -.5f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// b on top right collision
		b = new RectF(.25f, 2.0f, 2.0f, .5f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// b on bottom right collision
		b = new RectF(.25f, .5f, 2.0f, -.5f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(true));
		
		// no collisions small
		
		// b on top
		b = new RectF(.25f, 2.0f, .75f, 1.5f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(false));
		
		// b on bottom
		b = new RectF(.25f, -1.5f, .75f, -2.0f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(false));
		
		// b on left
		b = new RectF(-2.0f, .75f, -1.0f, .25f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(false));
		
		// b on right
		b = new RectF(1.5f, .75f, 2.0f, .25f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(false));
		
		// no collisions big
		
		// b on top
		b = new RectF(-1.0f, 2.0f, 2.0f, 1.5f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(false));
		
		// b on bottom
		b = new RectF(-1.0f, -1.5f, 2.0f, -2.0f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(false));
		
		// b on left
		b = new RectF(-2.0f, 2.0f, -1.0f, -.25f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(false));
		
		// b on right
		b = new RectF(1.5f, 2.0f, 2.0f, -.25f);
		assertThat(FunctionsExtended.equalIntersects(a, b), equalTo(false));
	}
	
	@Test
	public void testFunctionsSetEqualIntersects() throws Exception
	{
		RectF send_back = new RectF();
		RectF a = new RectF(0, 1, 1, 0);
		
		// general collisions
		
		// first that they are the exact same
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, 0, 1, 1, 0), equalTo(true));
		assertThat(a.left, equalTo(send_back.left));
		assertThat(a.top, equalTo(send_back.top));
		assertThat(a.right, equalTo(send_back.right));
		assertThat(a.bottom, equalTo(send_back.bottom));
		
		// b is bigger
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, -1, 2, 2, -1), equalTo(true));
		assertThat(send_back.left, equalTo(0.0f));
		assertThat(send_back.top, equalTo(1.0f));
		assertThat(send_back.right, equalTo(1.0f));
		assertThat(send_back.bottom, equalTo(0.0f));
		
		// b is smaller
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, .25, .75, .75, .25), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.25f, .75f, .75f, .25f)), equalTo(true));
		
		// parallel collisions
		
		// b on top collision
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, .25, 2.0, .75, .5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.25f, 1.0f, .75f, .5f)), equalTo(true));
		
		// b on bottom collision
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, .25, .5, .75, -.5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.25f, .5f, .75f, 0)), equalTo(true));
		
		// b on left collision
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, -1.0, .75, .5, .25), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(0, .75f, .5f, .25f)), equalTo(true));
		
		// b on right collision
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, .5, .75, 2.0, .25), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.5f, .75f, 1.0f, .25f)), equalTo(true));
		
		// corner collisions
		
		// b on top left collision
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, -1.0, 2.0, .75, .5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(0, 1.0f, .75f, .5f)), equalTo(true));
		
		// b on bottom left collision
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, -1.0, .5, .75, -.5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(0, .5f, .75f, 0)), equalTo(true));
		
		// b on top right collision
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, .25, 2.0, 2.0, .5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.25f, 1.0f, 1.0f, .5f)), equalTo(true));
		
		// b on bottom right collision
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, .25, .5, 2.0, -.5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.25f, .5f, 1.0f, 0)), equalTo(true));
		
		// no collisions small
		
		// b on top
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, .25, 2.0, .75, 1.5), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on bottom
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, .25, -1.5, .75, -2.0), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on left
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, -2.0, .75, -1.0, .25), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on right
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, 1.5, .75, 2.0, .25), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// no collisions big
		
		// b on top
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, -1.0, 2.0, 2.0, 1.5), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on bottom
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, -1.0, -1.5, 2.0, -2.0), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on left
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, -2.0, 2.0, -1.0, -.25), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on right
		assertThat(FunctionsExtended.setEqualIntersects(send_back, a, 1.5, 2.0, 2.0, -.25), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
	}
	
	@Test
	public void testEqualRectF() throws Exception
	{
		assertThat(equalRectF(new RectF(0, 1, 1, 0), new RectF(0, 2, 1, 0)), equalTo(false));
		assertThat(equalRectF(new RectF(0, 1, 1, 0), new RectF(0, 1, 1, 0)), equalTo(true));
	}
	
	private boolean equalRectF(RectF a, RectF b)
	{
		return (a.left == b.left && a.top == b.top && a.right == b.right && a.bottom == b.bottom);
	}
	
	@Test
	public void testFunctionsRectangularToRadius() throws Exception
	{
		assertThat(FunctionsExtended.rectangularToRadius(0, 1), equalTo(1.0));
		assertThat(FunctionsExtended.rectangularToRadius(1, 1), equalTo(Math.sqrt(2)));
		assertThat(FunctionsExtended.rectangularToRadius(1, 0), equalTo(1.0));
		assertThat(FunctionsExtended.rectangularToRadius(0, 0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsRectangularToDegree() throws Exception
	{
		assertThat(FunctionsExtended.rectangularToDegree(0, 1), equalTo(90.0));
		assertThat(FunctionsExtended.rectangularToDegree(1, 1), equalTo(45.0));
		assertThat(FunctionsExtended.rectangularToDegree(1, 0), equalTo(0.0));
		assertThat(FunctionsExtended.rectangularToDegree(0, 0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsPolarToX() throws Exception
	{
		assertThat(Math.toRadians(0), equalTo(0.0));
		assertThat(Math.toRadians(180), equalTo(Math.PI));
		assertThat(Math.sin(Math.PI), not(0.0)); // because of this, we have to round below
		
		assertThat(FunctionsExtended.polarToX(0, 1), equalTo(1.0));
		assertThat(Math.round(FunctionsExtended.polarToX(90, 1)), equalTo((long) 0.0));
		assertThat(Math.round(FunctionsExtended.polarToX(-90, 1)), equalTo((long) 0.0));
		assertThat(Math.round(FunctionsExtended.polarToX(180, 1)), equalTo((long) -1.0)); // round here
	}
	
	@Test
	public void testFunctionsPolarToY() throws Exception
	{
		assertThat(Math.toRadians(0), equalTo(0.0));
		assertThat(Math.toRadians(180), equalTo(Math.PI));
		assertThat(Math.sin(Math.PI), not(0.0)); // because of this, we have to round below
		
		assertThat(FunctionsExtended.polarToY(0, 1), equalTo(0.0));
		assertThat(FunctionsExtended.polarToY(90, 1), equalTo(1.0));
		assertThat(FunctionsExtended.polarToY(-90, 1), equalTo(-1.0));
		assertThat(Math.round(FunctionsExtended.polarToY(180, 1)), equalTo((long) 0)); // round here
	}
	
	@Test
	public void testFunctionsNearestPowerOf2() throws Exception
	{
		assertThat(FunctionsExtended.nearestPowerOf2(10), equalTo(16));
		assertThat(FunctionsExtended.nearestPowerOf2(1), equalTo(1));
		assertThat(FunctionsExtended.nearestPowerOf2(0), equalTo(0));
		assertThat(FunctionsExtended.nearestPowerOf2(-1), equalTo(0));
	}
	
	@Test
	public void testFunctionsSpeed() throws Exception
	{
		assertThat(FunctionsExtended.speed(2, -5), equalTo(5.3851648071345040312507104915403));
		assertThat(FunctionsExtended.speed(1, 1), equalTo(1.4142135623730950488016887242097));
		assertThat(FunctionsExtended.speed(0, 0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsRed() throws Exception
	{
		assertThat(FunctionsExtended.red(Color.BLUE), equalTo(0));
		assertThat(FunctionsExtended.red(Color.RED), equalTo(255));
		assertThat(FunctionsExtended.red(Color.GREEN), equalTo(0));
	}
	
	@Test
	public void testFunctionsGreen() throws Exception
	{
		assertThat(FunctionsExtended.green(Color.BLUE), equalTo(0));
		assertThat(FunctionsExtended.green(Color.RED), equalTo(0));
		assertThat(FunctionsExtended.green(Color.GREEN), equalTo(255));
	}
	
	@Test
	public void testFunctionsBlue() throws Exception
	{
		assertThat(FunctionsExtended.blue(Color.BLUE), equalTo(255));
		assertThat(FunctionsExtended.blue(Color.RED), equalTo(0));
		assertThat(FunctionsExtended.blue(Color.GREEN), equalTo(0));
	}
	
	@Test
	public void testFunctionsAlpha() throws Exception
	{
		assertThat(FunctionsExtended.blue(Color.TRANSPARENT), equalTo(0));
		assertThat(FunctionsExtended.alpha(Color.BLUE), equalTo(255));
		assertThat(FunctionsExtended.alpha(Color.RED), equalTo(255));
		assertThat(FunctionsExtended.alpha(Color.RED), equalTo(255));
	}
	
	@Test
	public void testFunctionsMakeColor() throws Exception
	{
		assertThat(FunctionsExtended.makeColor(255, 255, 255, 255), equalTo(Color.WHITE));
		assertThat(FunctionsExtended.makeColor(0, 0, 0, 255), equalTo(Color.BLACK));
	}
	
	@Test
	public void testFunctionsDeviceXToScreenX() throws Exception
	{
		FunctionsExtended.resetShaderStatics();
		
		Constants.width = 2000;
		Constants.height = 2000;
		
		Constants.device_width = 1280;
		Constants.device_height = 720;
		
		Constants.ratio = (double) Constants.width / (double) Constants.height;
		Constants.device_ratio = (double) Constants.device_width / (double) Constants.device_height;
		
		Constants.horizontal_ratio = false;
		
		assertThat(FunctionsExtended.deviceXToScreenX(1000), equalTo(2000.0));
		assertThat(FunctionsExtended.deviceXToScreenX(640), equalTo(1000.0));
		assertThat(FunctionsExtended.deviceXToScreenX(280), equalTo(0.0));
		
		// outside of this range we don't really care :/
	}
	
	@Test
	public void testFunctionsDeviceYToScreenY() throws Exception
	{
		FunctionsExtended.resetShaderStatics();
		
		Constants.width = 2560;
		Constants.height = 1000;
		
		Constants.device_width = 1280;
		Constants.device_height = 720;
		
		Constants.ratio = (double) Constants.width / (double) Constants.height;
		Constants.device_ratio = (double) Constants.device_width / (double) Constants.device_height;
		
		Constants.horizontal_ratio = true;
		
		assertThat(FunctionsExtended.deviceYToScreenY(610), equalTo(1000.0));
		assertThat(FunctionsExtended.deviceYToScreenY(360), equalTo(500.0));
		assertThat(FunctionsExtended.deviceYToScreenY(110), equalTo(0.0));
		
		// outside of this range we don't really care :/
	}
}
