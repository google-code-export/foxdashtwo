package tests.activity;

//Big thanks to https://github.com/jmschultz/Eclipse-Robolectric-Example

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.kobaj.foxdashtwo.FoxdashtwoActivity;

import tests.runner.SampleTestRunner;
import android.graphics.Color;

@RunWith(SampleTestRunner.class)
public class SampleTest {
	@Test
    public void testFunctionsBlue() throws Exception {
		//FoxdashtwoActivity activity = new FoxdashtwoActivity();
	    //activity.onCreate(null);
		
		assertThat(com.kobaj.math.Functions.blue(Color.BLUE), equalTo(255));
		assertThat(com.kobaj.math.Functions.blue(Color.RED), equalTo(0));	
    }
}
