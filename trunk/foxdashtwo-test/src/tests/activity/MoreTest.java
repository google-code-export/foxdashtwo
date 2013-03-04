package tests.activity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import tests.runner.SampleTestRunner;
import android.opengl.Matrix;

import com.kobaj.math.Constants;
import com.kobaj.math.Physics;
import com.kobaj.math.RectFExtended;
import com.kobaj.math.android.RectF;

@RunWith(SampleTestRunner.class)
public class MoreTest
{
	@Test
	public void testPhysicsCleanCollision() throws Exception
	{
		RectF rectangle = new RectF();
		Constants.collision_detection_height = 0;
		
		rectangle.top = -1;
		rectangle.bottom = 1;
		rectangle.left = 5;
		rectangle.right = -5;
		
		Physics.cleanCollision(rectangle);
		
		// RectFs are UPSIDE DOWN
		assertThat(rectangle.top, equalTo(-1f));
		assertThat(rectangle.bottom, equalTo(1f));
		assertThat(rectangle.width(), equalTo(0f));
		
		rectangle.top = -5;
		rectangle.bottom = 5;
		rectangle.left = 1;
		rectangle.right = -1;

		Physics.cleanCollision(rectangle);
		assertThat(rectangle.left, equalTo(-1f));
		assertThat(rectangle.right, equalTo(1f));
		assertThat(rectangle.height(), equalTo(0f));
	}
	
	@Test
	public void testSanityCheck() throws Exception
	{
		int x1 = 5;
		int x2 = 5;
		
		x1 -= 7;
		x2 = x2 - 7;
		assertThat(x1, equalTo(x2));
		
		float[] my_matrix = new float[16];
		
		Matrix.setIdentityM(my_matrix, 0);
		Matrix.translateM(my_matrix, 0, 0, 0, 0);
		for (int i = 0; i < 16; i++)
			assertThat(my_matrix[i], equalTo(0.0f));
	}
	
	@Test
	public void testExtendedRectFConstruct() throws Exception
	{
		RectFExtended test = new RectFExtended(1.0, 2.0, 3.0, .5);
		assertThat(test.main_rect.left, equalTo(1.0f));
		assertThat(test.main_rect.top, equalTo(2.0f));
		assertThat(test.main_rect.right, equalTo(3.0f));
		assertThat(test.main_rect.bottom, equalTo(.5f));
	}
	
	@Test
	public void testExtendedRectFSet() throws Exception
	{
		RectFExtended test = new RectFExtended(1.0, 2.0, 3.0, .5);
		test.setPositionWithOffset(0, 0);
		assertThat(test.main_rect.left, equalTo(1.0f));
		assertThat(test.main_rect.top, equalTo(2.0f));
		assertThat(test.main_rect.right, equalTo(3.0f));
		assertThat(test.main_rect.bottom, equalTo(.5f));
		
		test.setPositionWithOffset(1, 1);
		assertThat(test.main_rect.left, equalTo(2.0f));
		assertThat(test.main_rect.top, equalTo(3.0f));
		assertThat(test.main_rect.right, equalTo(4.0f));
		assertThat(test.main_rect.bottom, equalTo(1.5f));
	}
}
