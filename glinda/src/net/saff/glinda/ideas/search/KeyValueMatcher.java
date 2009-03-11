package net.saff.glinda.ideas.search;


public interface KeyValueMatcher {

	public abstract boolean matches(SearchKey searchKey, String searchValue);

}