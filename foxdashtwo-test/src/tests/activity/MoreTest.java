package tests.activity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import tests.runner.SampleTestRunner;
import android.opengl.Matrix;
import android.util.SparseArray;

import com.kobaj.level.CoordMap;
import com.kobaj.level.LevelObject;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.Physics;
import com.kobaj.math.RectFExtended;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadEmpty;

@RunWith(SampleTestRunner.class)
public class MoreTest
{
	@Test
	public void testCoordMap_InsertObj() throws Exception
	{
		Constants.width = 20;
		Constants.height = 20;
		Constants.ratio = 1.0;
		Constants.shader_width = 2.0;
		Functions.adjustConstantsToScreen();
		
		ArrayList<LevelObject> objects = new ArrayList<LevelObject>();
		
		LevelObject huge_span = new LevelObject();
		huge_span.quad_object = new QuadEmpty(14, 14);
		huge_span.quad_object.setXYPos(18, 8, EnumDrawFrom.bottom_left);
		
		objects.add(huge_span);
		
		CoordMap coord_temp = new CoordMap(40, 30, objects);
		
		SparseArray<SparseArray<ArrayList<LevelObject>>> calculated_objects = coord_temp.calculated_objects;
		
		for (int i = 17; i <= 20; i++)
		{
			for (int e = 8; e <= 10; e++)
			{
				boolean ass = false;
				if (18 <= i && i <= 20 && //
						1 <= 8 && e <= 10)
					ass = true;
				
				if (calculated_objects.get(i, null) != null)
					if (calculated_objects.get(i).get(e, null) != null)
					{
						
						boolean actual_value = calculated_objects.get(i).get(e).size() == 1;
						
						assertThat(actual_value, equalTo(ass));
					}
			}
		}
		
		Functions.setCamera(-1, -1, 0);
		coord_temp.updated_visible_objects();
		
		assertThat(coord_temp.visible_object_count, equalTo(0));
		
		Functions.setCamera(18, 8, 0);
		coord_temp.updated_visible_objects();
		
		assertThat(coord_temp.visible_object_count, equalTo(1));
	}
	
	@Test
	public void testCoordMap_XYGen() throws Exception
	{
		Constants.width = 800;
		Constants.height = 600;
		
		CoordMap coord_temp = new CoordMap(0, 0, null);
		
		int[] x_y = coord_temp.calculate_x_y(12000, 3400);
		
		assertThat(x_y[0], equalTo(15));
		assertThat(x_y[1], equalTo(6));
		
		Constants.width = 851;
		Constants.height = 361;
		x_y = coord_temp.calculate_x_y(12000, 3400);
		
		assertThat(x_y[0], equalTo(29));
		assertThat(x_y[1], equalTo(19));
	}
	
	@Test
	public void testPhysicsCleanCollision() throws Exception
	{
		RectF rectangle = new RectF();
		Constants.collision_detection_height = 0;
		
		rectangle.top = -1;
		rectangle.bottom = 1;
		rectangle.left = 5;
		rectangle.right = -5;
		
		Physics.cleanCollision(rectangle, true);
		
		// RectFs are UPSIDE DOWN
		assertThat(rectangle.top, equalTo(-1f));
		assertThat(rectangle.bottom, equalTo(1f));
		assertThat(rectangle.width(), equalTo(0f));
		
		rectangle.top = -5;
		rectangle.bottom = 5;
		rectangle.left = 1;
		rectangle.right = -1;
		
		Physics.cleanCollision(rectangle, true);
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
