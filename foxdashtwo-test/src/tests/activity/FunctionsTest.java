package tests.activity;

//Big thanks to https://github.com/jmschultz/Eclipse-Robolectric-Example

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import tests.runner.SampleTestRunner;
import android.graphics.Color;
import com.kobaj.math.android.RectF;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;

// as of 10/24/12 this is 156 tests :)
// as of 12/15/12 this is 178 tests :D

@RunWith(SampleTestRunner.class)
public class FunctionsTest
{
	@Test
	public void testFunctionsLinearInterpolate() throws Exception
	{
		assertThat(Functions.linearInterpolate(-1, 1.0, 1.0, 0, 100), equalTo(100.0));
		assertThat(Functions.linearInterpolate(-1, 1.0, -1.0, 0, 100), equalTo(0.0));
		assertThat(Functions.linearInterpolate(-1, 1.0, 0.0, 0, 100), equalTo(50.0));
		
		assertThat(Functions.linearInterpolate(-1, 1.0, -10.0, 0, 100), equalTo(0.0));
		assertThat(Functions.linearInterpolate(-1, 1.0, 100.0, 0, 100), equalTo(100.0));
	}
	
	@Test
	public void testFunctionsLinearInterpolateUnclamped() throws Exception
	{
		assertThat(Functions.linearInterpolateUnclamped(0, 100, 100, -1, 1), equalTo(1.0));
		assertThat(Functions.linearInterpolateUnclamped(0, 100, 0, -1, 1), equalTo(-1.0));
		assertThat(Functions.linearInterpolateUnclamped(0, 100, 50, -1, 1), equalTo(0.0));
		
		assertThat(Functions.linearInterpolateUnclamped(0, 100, 200, -1, 1), equalTo(3.0));
	}
	
	@Test
	public void testFunctionsLinearInterpolateColor() throws Exception
	{
		assertThat(Functions.linearInterpolateColor(0, 1, 1, Color.WHITE, Color.BLACK), equalTo(Color.BLACK));
		assertThat(Functions.linearInterpolateColor(0, 1, 0, Color.WHITE, Color.BLACK), equalTo(Color.WHITE));
		assertThat(Functions.linearInterpolateColor(0, 1, .5, Color.WHITE, Color.BLACK), equalTo(-8421505));	
	}
	
	@Test
	public void testFunctionsClamp() throws Exception
	{
		assertThat(Functions.clamp(100, 10, 0), equalTo(10.0));
		assertThat(Functions.clamp(100, 200, 0), equalTo(100.0));
		assertThat(Functions.clamp(100, -100, 0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsByteToShader() throws Exception
	{
		assertThat(Functions.byteToShader(0), equalTo(0.0));
		assertThat(Functions.byteToShader(255), equalTo(1.0));
		assertThat(Functions.byteToShader(275), equalTo(1.0));
		assertThat(Functions.byteToShader(100) < 1.0, equalTo(true));
		assertThat(Functions.byteToShader(100) > 0.0, equalTo(true));
	}
	
	@Test
	public void testFunctionsScreenXToShaderX() throws Exception
	{
		Constants.ratio = 1.0;
		Constants.width = 200;
		
		assertThat(Functions.screenXToShaderX(200), equalTo(1.0));
		assertThat(Functions.screenXToShaderX(0), equalTo(-1.0));
		
		assertThat(Functions.screenXToShaderX(400) > 1.0, equalTo(true));
	}
	
	@Test
	public void testFunctionsScreenYToShaderY() throws Exception
	{
		Constants.height = 200;
		
		assertThat(Functions.screenYToShaderY(200), equalTo(1.0));
		assertThat(Functions.screenYToShaderY(0), equalTo(-1.0));
		
		assertThat(Functions.screenYToShaderY(400) > 1.0, equalTo(true));
	}
	
	@Test
	public void testFunctionsScreenWidthToShaderWidth() throws Exception
	{
		Constants.shader_width = 2.0;
		Constants.width = 200;
		
		assertThat(Functions.screenWidthToShaderWidth(200.0), equalTo(2.0));
		assertThat(Functions.screenWidthToShaderWidth(0), equalTo(0.0));
		
		assertThat(Functions.screenWidthToShaderWidth(400.0) > 2.0, equalTo(true));
	}
	
	@Test
	public void testFunctionsScreenHeightToShaderHeight() throws Exception
	{
		// Constants.Screen_height = 2.0;
		Constants.height = 200;
		
		assertThat(Functions.screenHeightToShaderHeight(200.0), equalTo(2.0));
		assertThat(Functions.screenHeightToShaderHeight(0), equalTo(0.0));
		
		assertThat(Functions.screenHeightToShaderHeight(400.0) > 2.0, equalTo(true));
	}
	
	@Test
	public void testFunctionsShaderXToScreenX() throws Exception
	{
		Constants.ratio = 1.0;
		Constants.width = 200;
		
		assertThat(Functions.shaderXToScreenX(1.0), equalTo(200.0));
		assertThat(Functions.shaderXToScreenX(-1.0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsShaderYToScreenY() throws Exception
	{
		Constants.height = 200;
		
		assertThat(Functions.shaderYToScreenY(1.0), equalTo(200.0));
		assertThat(Functions.shaderYToScreenY(-1.0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsShaderWidthToScreenWidth() throws Exception
	{
		Constants.shader_width = 2.0;
		Constants.width = 200;
		
		assertThat(Functions.shaderWidthToScreenWidth(2.0), equalTo(200.0));
		assertThat(Functions.shaderWidthToScreenWidth(0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsShaderHeightToScreenHeight() throws Exception
	{
		// Constants.shader_height = 2.0;
		Constants.height = 200;
		
		assertThat(Functions.shaderHeightToScreenHeight(2.0), equalTo(200.0));
		assertThat(Functions.shaderHeightToScreenHeight(0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsInRectF() throws Exception
	{
		RectF my_rect = new RectF(0, 1, 1, 0);
		
		// true
		assertThat(Functions.inRectF(my_rect, .5, .5), equalTo(true));
		assertThat(Functions.inRectF(my_rect, 1, 1), equalTo(true));
		assertThat(Functions.inRectF(my_rect, 0, 0), equalTo(true));
		
		// false
		assertThat(Functions.inRectF(my_rect, .5, -.5), equalTo(false));
		assertThat(Functions.inRectF(my_rect, .5, 2.0), equalTo(false));
		assertThat(Functions.inRectF(my_rect, -.5, .5), equalTo(false));
		assertThat(Functions.inRectF(my_rect, 2.0, .5), equalTo(false));
	}
	
	@Test
	public void testFunctionsOnScreen() throws Exception
	{
		// set constants.
		Constants.width = 800;
		Constants.height = 480;
		Constants.ratio = Constants.width / Constants.height;
		Constants.y_shader_translation = 0.0;
		Constants.x_shader_translation = 0.0;
		Constants.z_shader_translation = 0.0;
		
		// true
		assertThat(Functions.onScreen(200, 200), equalTo(true));
		assertThat(Functions.onScreen(400, 480), equalTo(true));
		assertThat(Functions.onScreen(800, 400), equalTo(true));
		
		// false
		assertThat(Functions.onScreen(-200, 200), equalTo(false));
		assertThat(Functions.onScreen(400, -480), equalTo(false));
		assertThat(Functions.onScreen(-800, -400), equalTo(false));
	}
	
	@Test
	public void testFunctionsOnShader() throws Exception
	{
		// set constants.
		Constants.ratio = 2.0;
		Constants.y_shader_translation = 1.0;
		Constants.x_shader_translation = 1.0;
		Constants.z_shader_translation = 0.0;
		
		// true
		assertThat(Functions.onShader(0, 0), equalTo(true));
		assertThat(Functions.onShader(1.0, 1.0), equalTo(true));
		assertThat(Functions.onShader(2.0, 2.0), equalTo(true));
		
		// false
		assertThat(Functions.onShader(-2.0, -2.0), equalTo(false));
		assertThat(Functions.onShader(4.0, 4.0), equalTo(false));
		
		// shift the z
		Constants.z_shader_translation = .5;
		
		// true
		assertThat(Functions.onShader(-2.0, 0), equalTo(true));
		assertThat(Functions.onShader(4.0, 2.0), equalTo(true));
		
		// false
		assertThat(Functions.onShader(-2.0, -2.0), equalTo(false));
		assertThat(Functions.onShader(4.0, 4.0), equalTo(false));
	}
	
	@Test
	public void testFunctionsUpdateShaderRectFView() throws Exception
	{
		// set constants.
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
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// b is bigger
		b = new RectF(-1, 2, 2, -1);
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// b is smaller
		b = new RectF(.25f, .75f, .75f, .25f);
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// parallel collisions
		
		// b on top collision
		b = new RectF(.25f, 2.0f, .75f, .5f);
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// b on bottom collision
		b = new RectF(.25f, .5f, .75f, -.5f);
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// b on left collision
		b = new RectF(-1.0f, .75f, .5f, .25f);
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// b on right collision
		b = new RectF(.5f, .75f, 2.0f, .25f);
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// corner collisions
		
		// b on top left collision
		b = new RectF(-1.0f, 2.0f, .75f, .5f);
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// b on bottom left collision
		b = new RectF(-1.0f, .5f, .75f, -.5f);
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// b on top right collision
		b = new RectF(.25f, 2.0f, 2.0f, .5f);
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// b on bottom right collision
		b = new RectF(.25f, .5f, 2.0f, -.5f);
		assertThat(Functions.equalIntersects(a, b), equalTo(true));
		
		// no collisions small
		
		// b on top
		b = new RectF(.25f, 2.0f, .75f, 1.5f);
		assertThat(Functions.equalIntersects(a, b), equalTo(false));
		
		// b on bottom
		b = new RectF(.25f, -1.5f, .75f, -2.0f);
		assertThat(Functions.equalIntersects(a, b), equalTo(false));
		
		// b on left
		b = new RectF(-2.0f, .75f, -1.0f, .25f);
		assertThat(Functions.equalIntersects(a, b), equalTo(false));
		
		// b on right
		b = new RectF(1.5f, .75f, 2.0f, .25f);
		assertThat(Functions.equalIntersects(a, b), equalTo(false));
		
		// no collisions big
		
		// b on top
		b = new RectF(-1.0f, 2.0f, 2.0f, 1.5f);
		assertThat(Functions.equalIntersects(a, b), equalTo(false));
		
		// b on bottom
		b = new RectF(-1.0f, -1.5f, 2.0f, -2.0f);
		assertThat(Functions.equalIntersects(a, b), equalTo(false));
		
		// b on left
		b = new RectF(-2.0f, 2.0f, -1.0f, -.25f);
		assertThat(Functions.equalIntersects(a, b), equalTo(false));
		
		// b on right
		b = new RectF(1.5f, 2.0f, 2.0f, -.25f);
		assertThat(Functions.equalIntersects(a, b), equalTo(false));
	}
	
	@Test
	public void testFunctionsSetEqualIntersects() throws Exception
	{
		RectF send_back = new RectF();
		RectF a = new RectF(0, 1, 1, 0);
		
		// general collisions
		
		// first that they are the exact same
		assertThat(Functions.setEqualIntersects(send_back, a, 0, 1, 1, 0), equalTo(true));
		assertThat(a.left, equalTo(send_back.left));
		assertThat(a.top, equalTo(send_back.top));
		assertThat(a.right, equalTo(send_back.right));
		assertThat(a.bottom, equalTo(send_back.bottom));
		
		// b is bigger
		assertThat(Functions.setEqualIntersects(send_back, a, -1, 2, 2, -1), equalTo(true));
		assertThat(send_back.left, equalTo(0.0f));
		assertThat(send_back.top, equalTo(1.0f));
		assertThat(send_back.right, equalTo(1.0f));
		assertThat(send_back.bottom, equalTo(0.0f));
		
		// b is smaller
		assertThat(Functions.setEqualIntersects(send_back, a, .25, .75, .75, .25), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.25f, .75f, .75f, .25f)), equalTo(true));
		
		// parallel collisions
		
		// b on top collision
		assertThat(Functions.setEqualIntersects(send_back, a, .25, 2.0, .75, .5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.25f, 1.0f, .75f, .5f)), equalTo(true));
		
		// b on bottom collision
		assertThat(Functions.setEqualIntersects(send_back, a, .25, .5, .75, -.5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.25f, .5f, .75f, 0)), equalTo(true));
		
		// b on left collision
		assertThat(Functions.setEqualIntersects(send_back, a, -1.0, .75, .5, .25), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(0, .75f, .5f, .25f)), equalTo(true));
		
		// b on right collision
		assertThat(Functions.setEqualIntersects(send_back, a, .5, .75, 2.0, .25), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.5f, .75f, 1.0f, .25f)), equalTo(true));
		
		// corner collisions
		
		// b on top left collision
		assertThat(Functions.setEqualIntersects(send_back, a, -1.0, 2.0, .75, .5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(0, 1.0f, .75f, .5f)), equalTo(true));
		
		// b on bottom left collision
		assertThat(Functions.setEqualIntersects(send_back, a, -1.0, .5, .75, -.5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(0, .5f, .75f, 0)), equalTo(true));
		
		// b on top right collision
		assertThat(Functions.setEqualIntersects(send_back, a, .25, 2.0, 2.0, .5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.25f, 1.0f, 1.0f, .5f)), equalTo(true));
		
		// b on bottom right collision
		assertThat(Functions.setEqualIntersects(send_back, a, .25, .5, 2.0, -.5), equalTo(true));
		assertThat(equalRectF(send_back, new RectF(.25f, .5f, 1.0f, 0)), equalTo(true));
		
		// no collisions small
		
		// b on top
		assertThat(Functions.setEqualIntersects(send_back, a, .25, 2.0, .75, 1.5), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on bottom
		assertThat(Functions.setEqualIntersects(send_back, a, .25, -1.5, .75, -2.0), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on left
		assertThat(Functions.setEqualIntersects(send_back, a, -2.0, .75, -1.0, .25), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on right
		assertThat(Functions.setEqualIntersects(send_back, a, 1.5, .75, 2.0, .25), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// no collisions big
		
		// b on top
		assertThat(Functions.setEqualIntersects(send_back, a, -1.0, 2.0, 2.0, 1.5), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on bottom
		assertThat(Functions.setEqualIntersects(send_back, a, -1.0, -1.5, 2.0, -2.0), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on left
		assertThat(Functions.setEqualIntersects(send_back, a, -2.0, 2.0, -1.0, -.25), equalTo(false));
		assertThat(equalRectF(send_back, new RectF(0, 0, 0, 0)), equalTo(true));
		
		// b on right
		assertThat(Functions.setEqualIntersects(send_back, a, 1.5, 2.0, 2.0, -.25), equalTo(false));
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
		assertThat(Functions.rectangularToRadius(0, 1), equalTo(1.0));
		assertThat(Functions.rectangularToRadius(1, 1), equalTo(Math.sqrt(2)));
		assertThat(Functions.rectangularToRadius(1, 0), equalTo(1.0));
		assertThat(Functions.rectangularToRadius(0, 0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsRectangularToDegree() throws Exception
	{
		assertThat(Functions.rectangularToDegree(0, 1), equalTo(90.0));
		assertThat(Functions.rectangularToDegree(1, 1), equalTo(45.0));
		assertThat(Functions.rectangularToDegree(1, 0), equalTo(0.0));
		assertThat(Functions.rectangularToDegree(0, 0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsPolarToX() throws Exception
	{
		assertThat(Math.toRadians(0), equalTo(0.0));
		assertThat(Math.toRadians(180), equalTo(Math.PI));
		assertThat(Math.sin(Math.PI), not(0.0)); // because of this, we have to round below
		
		assertThat(Functions.polarToX(0, 1), equalTo(1.0));
		assertThat(Math.round(Functions.polarToX(90, 1)), equalTo((long) 0.0));
		assertThat(Math.round(Functions.polarToX(-90, 1)), equalTo((long) 0.0));
		assertThat(Math.round(Functions.polarToX(180, 1)), equalTo((long) -1.0)); // round here
	}
	
	@Test
	public void testFunctionsPolarToY() throws Exception
	{
		assertThat(Math.toRadians(0), equalTo(0.0));
		assertThat(Math.toRadians(180), equalTo(Math.PI));
		assertThat(Math.sin(Math.PI), not(0.0)); // because of this, we have to round below
		
		assertThat(Functions.polarToY(0, 1), equalTo(0.0));
		assertThat(Functions.polarToY(90, 1), equalTo(1.0));
		assertThat(Functions.polarToY(-90, 1), equalTo(-1.0));
		assertThat(Math.round(Functions.polarToY(180, 1)), equalTo((long) 0)); // round here
	}
	
	@Test
	public void testFunctionsNearestPowerOf2() throws Exception
	{
		assertThat(Functions.nearestPowerOf2(10), equalTo(16));
		assertThat(Functions.nearestPowerOf2(1), equalTo(1));
		assertThat(Functions.nearestPowerOf2(0), equalTo(0));
		assertThat(Functions.nearestPowerOf2(-1), equalTo(0));
	}
	
	@Test
	public void testFunctionsSpeed() throws Exception
	{
		assertThat(com.kobaj.math.Functions.speed(2, -5), equalTo(5.3851648071345040312507104915403));
		assertThat(com.kobaj.math.Functions.speed(1, 1), equalTo(1.4142135623730950488016887242097));
		assertThat(com.kobaj.math.Functions.speed(0, 0), equalTo(0.0));
	}
	
	@Test
	public void testFunctionsRed() throws Exception
	{
		assertThat(com.kobaj.math.Functions.red(Color.BLUE), equalTo(0));
		assertThat(com.kobaj.math.Functions.red(Color.RED), equalTo(255));
		assertThat(com.kobaj.math.Functions.red(Color.GREEN), equalTo(0));
	}
	
	@Test
	public void testFunctionsGreen() throws Exception
	{
		assertThat(com.kobaj.math.Functions.green(Color.BLUE), equalTo(0));
		assertThat(com.kobaj.math.Functions.green(Color.RED), equalTo(0));
		assertThat(com.kobaj.math.Functions.green(Color.GREEN), equalTo(255));
	}
	
	@Test
	public void testFunctionsBlue() throws Exception
	{
		assertThat(com.kobaj.math.Functions.blue(Color.BLUE), equalTo(255));
		assertThat(com.kobaj.math.Functions.blue(Color.RED), equalTo(0));
		assertThat(com.kobaj.math.Functions.blue(Color.GREEN), equalTo(0));
	}
	
	@Test
	public void testFunctionsAlpha() throws Exception
	{
		assertThat(com.kobaj.math.Functions.blue(Color.TRANSPARENT), equalTo(0));
		assertThat(com.kobaj.math.Functions.alpha(Color.BLUE), equalTo(255));
		assertThat(com.kobaj.math.Functions.alpha(Color.RED), equalTo(255));
		assertThat(com.kobaj.math.Functions.alpha(Color.RED), equalTo(255));
	}
	
	@Test
	public void testFunctionsMakeColor() throws Exception
	{
		assertThat(com.kobaj.math.Functions.makeColor(255, 255, 255, 255), equalTo(Color.WHITE));
		assertThat(com.kobaj.math.Functions.makeColor(0, 0, 0, 255), equalTo(Color.BLACK));
	}
}
