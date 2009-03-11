package test.net.saff.floyd;

import static java.util.Arrays.asList;
import static test.net.saff.floyd.FloydMoney.dollars;

import net.saff.glinda.book.StaticParser;
import net.saff.glinda.time.GlindaTimePoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FloydBook extends StaticParser {
	private static final String LEFTOVERS = "Leftovers";

	private Map<String, FloydMoney> expectations = new HashMap<String, FloydMoney>();
	private Map<String, FloydMoney> allocations = new LinkedHashMap<String, FloydMoney>();
	private Map<String, GlindaTimePoint> creations = new LinkedHashMap<String, GlindaTimePoint>();
	private GlindaTimePoint now;

	public void expect(String stuff) {
		expect(parseCategory(stuff), parseAmount(stuff));
	}

	public void expect(String category, FloydMoney expected) {
		expectations.put(category, expected);
		creations.put(category, now);
	}

	private String parseCategory(String stuff) {
		return find(stuff, "for ([^ ]+)");
	}

	private FloydMoney parseAmount(String stuff) {
		return FloydMoney.parseDollars(find(stuff, "of \\$ ?([0-9.]+)"));
	}

	private String find(String content, String regex) {
		Matcher matcher = Pattern.compile(regex).matcher(content);
		if (matcher.find())
			return matcher.group(1);
		return null;
	}

	public void record(String stuff) {
		FloydMoney total = FloydMoney.parseDollars(find(stuff,
				"of \\$ ?([0-9.]+)"));
		Set<String> categoryNames = expectations.keySet();
		for (String each : categoryNames) {
			total = leftoverFromAllocationCatchup(total, each);
		}
		addAllocation(LEFTOVERS, total);
	}

	private FloydMoney leftoverFromAllocationCatchup(FloydMoney amountToGive,
			String categoryName) {
		FloydMoney wanted = getExpectation(categoryName).minus(
				getAllocation(categoryName));
		FloydMoney canGive = Collections.min(asList(amountToGive, wanted));
		addAllocation(categoryName, canGive);
		return amountToGive.minus(canGive);
	}

	private void addAllocation(String categoryName, FloydMoney given) {
		allocations.put(categoryName, getAllocation(categoryName).plus(given));
	}

	public void now(GlindaTimePoint now) {
		this.now = now;
	}

	public String moneyLeft(String categoryName) {
		return categoryName + " " + getAllocation(categoryName);
	}

	public List<String> moneyLeft() {
		ArrayList<String> response = new ArrayList<String>();

		int maxLength = longestCategory().length();

		for (String each : allocatedCategories()) {
			FloydMoney allocated = getAllocation(each);
			response.add(padWithSpaces(each, maxLength) + " " + allocated);
		}
		return response;
	}

	FloydMoney getAllocation(String categoryName) {
		return zeroize(allocations.get(categoryName));
	}

	private FloydMoney getExpectation(String each) {
		return zeroize(expectations.get(each));
	}

	private FloydMoney zeroize(FloydMoney floydMoney) {
		if (floydMoney == null)
			return dollars(0);
		return floydMoney;
	}

	private String padWithSpaces(String string, int length) {
		StringBuffer padded = new StringBuffer(string);
		while (padded.length() < length)
			padded.append(" ");
		return padded.toString();
	}

	private String longestCategory() {
		String longest = "";
		for (String each : allocatedCategories()) {
			if (each.length() > longest.length())
				longest = each;
		}
		return longest;
	}

	private Set<String> allocatedCategories() {
		return allocations.keySet();
	}

	public GlindaTimePoint creationTime(String category) {
		return creations.get(category);
	}
}
