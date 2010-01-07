package de.topicmapslab.aranuka.utils;

import gnu.trove.THashSet;

import java.util.HashSet;
import java.util.Set;

public class HashUtil {
	public static <T> Set<T> createSet() {
		Set<T> result;
		
		try {
			result = new THashSet();
		} catch (Exception e) {
			result = new HashSet();
		}
		
		return result;
	}
}
