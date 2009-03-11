package test.net.saff.stubbedtheories;

import static net.saff.glinda.ideas.correspondent.StringMatchers.containsString;
import static org.junit.Assert.assertThat;

import net.saff.stubbedtheories.Capturer;

import org.junit.Test;

public class TestCapturer {
	@Test
	public void capturerWorksOnAnyInterface() {
		Capturer<Object> stringCapture = Capturer.forObject("a", Object.class);
		stringCapture.getProxy().toString();
		assertThat(stringCapture.log(), containsString("toString"));
	}
}
