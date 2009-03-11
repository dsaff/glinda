/**
 * 
 */
package net.saff.glinda.ideas;

import java.util.ArrayList;
import java.util.List;

import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.ideas.search.KeyValueMatcher;

public class BucketValueSet {
	String[] bucketValueStrings() {
		ArrayList<String> list = new ArrayList<String>();
		for (Bucket bucket : Bucket.values()) {
			list.add(bucket.toString());
		}
		return list.toArray(new String[0]);
	}

	String userInput(KeyValueMatcher idea, Correspondent correspondent) {
		String question = idea + ": please choose time frame";
		return correspondent.getAnswer(question, bucketValueStrings());
	}

	List<String> categorize(Iterable<Idea> unbucketed,
			Correspondent correspondent) {
		List<String> lines = new ArrayList<String>();
		for (KeyValueMatcher idea : unbucketed) {
			lines.add(String.format("#bucket %s %s", idea, userInput(idea,
					correspondent)));
		}
		return lines;
	}
}