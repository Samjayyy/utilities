package utilities.autocomplete;

import java.util.ArrayList;

public interface IAutocomplete {
	public static final int MAX_HITS = 100;
	public void insert(String word, int val);
	public ArrayList<Integer> find(String key);
}
