package tests.activity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import tests.runner.SampleTestRunner;

import com.kobaj.math.RectFExtended;

@RunWith(SampleTestRunner.class)
public class MoreTest
{
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
