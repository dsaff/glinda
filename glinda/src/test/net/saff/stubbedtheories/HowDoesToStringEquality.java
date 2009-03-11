package test.net.saff.stubbedtheories;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.saff.glinda.time.DayOfWeek;
import net.saff.stubbedtheories.ToStringEquality;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class) public class HowDoesToStringEquality {
	@DataPoints public static ToStringEquality[] objects = {
			DayOfWeek.Monday, DayOfWeek.Tuesday };

	@Theory public void equalIffToStringsEqual(ToStringEquality a,
			ToStringEquality b) {
		assertThat(a.equals(b), is(a.toString().equals(b.toString())));
	}
}
