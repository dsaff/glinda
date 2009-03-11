package test.net.saff.glinda.units.interpretation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import net.saff.glinda.ideas.search.KeyValuePair;
import net.saff.glinda.ideas.search.SearchKey;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class TestSearchCriteria extends LoqBookDataPoints {
	@DataPoint public static SearchKey INTERESTED = SearchKey.interest;
	
	@Theory public void accessorsWork(SearchKey key, String value) {
		KeyValuePair criteria = new KeyValuePair(key, value);
		assertThat(criteria.getKey(), is(key));
		assertThat(criteria.getValue(), is(value));
	}
}
