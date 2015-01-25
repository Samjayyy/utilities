package utilities.autocomplete;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Sammie
 */
public class AutocompleteTree implements IAutocomplete{
	
	private TreeSet<AutocompleteEntry> tree;
	public AutocompleteTree() {
		tree = new TreeSet<AutocompleteEntry>();
	}
	// insert value in three
	public void insert(String word, int val) {
		tree.add(new AutocompleteEntry(word, val));
	}
	public ArrayList<Integer> find(String key){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		Set<AutocompleteEntry> set = tree.subSet(new AutocompleteEntry(key, 0), new AutocompleteEntry(smallestBigger(key), 0));
		for(AutocompleteEntry inSet : set){
			ret.add(inSet.value);
			if(ret.size()==MAX_HITS)break;
		}
		return ret;
	}
	private String smallestBigger(String s){
		if(s==null || s.isEmpty())return s;
		return s.substring(0,s.length()-1)+(char)(s.charAt(s.length()-1)+1);
	}
	class AutocompleteEntry implements Comparable<AutocompleteEntry>{
		String key;
		Integer value;
		public AutocompleteEntry(String key, Integer value) {
			this.key=key;this.value=value;
		}
		@Override
		public int compareTo(AutocompleteEntry o) {
			int ret = this.key.compareTo(o.key);
			if(ret == 0){
				return Integer.signum(this.value - o.value);
			}
			return ret;
		}
	}
}