package net.saff.glinda.ideas.search;


public class KeyValuePair implements SearchCriteria {
	private final String searchValue;
	private final SearchKey searchKey;

	public static <T> SearchCriteria all() {
		return new Everything();
	}

	public KeyValuePair(SearchKey searchKey, String searchValue) {
		this.searchKey = searchKey;
		this.searchValue = searchValue;
	}

	public String getValue() {
		return searchValue;
	}

	public static SearchCriteria from(String string) {
		if (string == null || string.equals("ALL"))
			return all();

		String[] split = string.split("=");
		return new KeyValuePair(SearchKey.valueOf(split[0]), split[1]);
	}

	public SearchKey getKey() {
		return searchKey;
	}

	public boolean match(KeyValueMatcher idea) {
		return idea.matches(searchKey, searchValue);
	}
}
