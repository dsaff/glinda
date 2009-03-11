package net.saff.glinda.ideas.search;

public interface SearchCriteria {
	public abstract boolean match(KeyValueMatcher object);
}